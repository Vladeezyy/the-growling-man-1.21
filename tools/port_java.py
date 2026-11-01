"""Mechanical Forge 1.20.1 / NeoForge 1.20.6 -> NeoForge 1.21.1 rewrites over src/main/java (idempotent).

The rules mirror what MCreator itself changed between the Forge 1.20.1 and NeoForge 1.20.6 builds of The Deep Void
(1697 file pairs at v1.98.1), plus the 1.20.6 -> 1.21.1 API changes. Anything not covered is fixed by hand.
"""
import os, re, sys

ROOT = sys.argv[1] if len(sys.argv) > 1 else 'src/main/java'


# ---------------------------------------------------------------- helpers
def match_paren(s, i):
    """s[i] is an opening bracket; return index of the matching closer."""
    depth = 0; q = None; n = len(s)
    while i < n:
        c = s[i]
        if q:
            if c == '\\': i += 2; continue
            if c == q: q = None
        elif c in '"\'': q = c
        elif c in '([{': depth += 1
        elif c in ')]}':
            depth -= 1
            if depth == 0: return i
        i += 1
    raise ValueError('unbalanced at %d' % i)


def split_args(s, start):
    """s[start] == '('; return (end, [args])."""
    end = match_paren(s, start)
    inner = s[start + 1:end]; args = []; depth = 0; cur = 0; q = None; i = 0
    while i < len(inner):
        c = inner[i]
        if q:
            if c == '\\': i += 2; continue
            if c == q: q = None
        elif c in '"\'': q = c
        elif c in '([{': depth += 1
        elif c in ')]}': depth -= 1
        elif c == ',' and depth == 0:
            args.append(inner[cur:i]); cur = i + 1
        i += 1
    args.append(inner[cur:])
    return end, args


def receiver_start(s, j):
    """s[j] is the '.' before a method call; walk back over the receiver expression."""
    i = j
    while i > 0:
        c = s[i - 1]
        if c == ')' or c == ']':
            # find matching opener backwards
            depth = 0; k = i - 1; q = None
            while k >= 0:
                ch = s[k]
                if ch in ')]': depth += 1
                elif ch in '([':
                    depth -= 1
                    if depth == 0: break
                k -= 1
            i = k
        elif c.isalnum() or c in '_.$':
            i -= 1
        else:
            break
    return i


def stmt_prefix_ok(s, i):
    """True if s[:i] ends at a statement boundary (only whitespace since ; { } or ) / else)."""
    k = i - 1
    while k >= 0 and s[k] in ' \t\r\n': k -= 1
    if k < 0: return True
    if s[k] in ';{}': return True
    if s[k] == ')': return True  # if (...) stmt / for (...) stmt
    if s[max(0, k - 3):k + 1] == 'else': return True
    if s[k] == '>' and s[k-1] == '-': return True  # lambda body
    return False


IMPORTS = {
    'DeferredHolder': 'net.neoforged.neoforge.registries.DeferredHolder',
    'DeferredRegister': 'net.neoforged.neoforge.registries.DeferredRegister',
    'BuiltInRegistries': 'net.minecraft.core.registries.BuiltInRegistries',
    'Registries': 'net.minecraft.core.registries.Registries',
    'DataComponents': 'net.minecraft.core.component.DataComponents',
    'CustomData': 'net.minecraft.world.item.component.CustomData',
    'EventBusSubscriber': 'net.neoforged.fml.common.EventBusSubscriber',
    'NeoForge': 'net.neoforged.neoforge.common.NeoForge',
    'ModConfigSpec': 'net.neoforged.neoforge.common.ModConfigSpec',
    'DeferredSpawnEggItem': 'net.neoforged.neoforge.common.DeferredSpawnEggItem',
    'ServerLevel': 'net.minecraft.server.level.ServerLevel',
    'SynchedEntityData': 'net.minecraft.network.syncher.SynchedEntityData',
    'ResourceLocation': 'net.minecraft.resources.ResourceLocation',
    'Attributes': 'net.minecraft.world.entity.ai.attributes.Attributes',
    'EntityTypeTags': 'net.minecraft.tags.EntityTypeTags',
    'LivingEntity': 'net.minecraft.world.entity.LivingEntity',
    'Explosion': 'net.minecraft.world.level.Explosion',
    'Level': 'net.minecraft.world.level.Level',
    'Item': 'net.minecraft.world.item.Item',
    'ICancellableEvent': 'net.neoforged.bus.api.ICancellableEvent',
    'AdvancementHolder': 'net.minecraft.advancements.AdvancementHolder',
    'Holder': 'net.minecraft.core.Holder',
    'TriState': 'net.neoforged.neoforge.common.util.TriState',
    'EquipmentSlotGroup': 'net.minecraft.world.entity.EquipmentSlotGroup',
    'NeoForgeMod': 'net.neoforged.neoforge.common.NeoForgeMod',
    'PacketDistributor': 'net.neoforged.neoforge.network.PacketDistributor',
    'PortImageButton': 'net.voxla.growlingman.port.PortImageButton',
    'TagKey': 'net.minecraft.tags.TagKey',
    'BlockTags': 'net.minecraft.tags.BlockTags',
    'Block': 'net.minecraft.world.level.block.Block',
    'Tier': 'net.minecraft.world.item.Tier',
    'FastColor': 'net.minecraft.util.FastColor',
    'ItemAttributeModifiers': 'net.minecraft.world.item.component.ItemAttributeModifiers',
    'PotionContents': 'net.minecraft.world.item.alchemy.PotionContents',
    'RegisterBrewingRecipesEvent': 'net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent',
    'DeferredSoundType': 'net.neoforged.neoforge.common.util.DeferredSoundType',
    'PathType': 'net.minecraft.world.level.pathfinder.PathType',
    'LevelReader': 'net.minecraft.world.level.LevelReader',
    'Vec3': 'net.minecraft.world.phys.Vec3',
    'Entity': 'net.minecraft.world.entity.Entity',
    'Iterables': 'com.google.common.collect.Iterables',
    'Player': 'net.minecraft.world.entity.player.Player',
    'PortUtil': 'net.voxla.growlingman.port.PortUtil',
    'Nullable': 'javax.annotation.Nullable',
    'ItemStack': 'net.minecraft.world.item.ItemStack',
    'RegisterSpawnPlacementsEvent': 'net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent',
    'SpawnPlacementTypes': 'net.minecraft.world.entity.SpawnPlacementTypes',
    'EventHooks': 'net.neoforged.neoforge.event.EventHooks',
    'FoodProperties': 'net.minecraft.world.food.FoodProperties',
}


def add_imports(s):
    have = set(re.findall(r'^import\s+([\w.]+);', s, re.M))
    simple = {h.rsplit('.', 1)[1] for h in have}
    pkg = re.search(r'^package\s+([\w.]+);', s, re.M)
    body = re.sub(r'^import .*$', '', s, flags=re.M)
    body = re.sub(r'"(?:\\.|[^"\\])*"', '""', body)
    add = []
    for name, fq in IMPORTS.items():
        if name in simple or fq in have: continue
        if pkg and fq.rsplit('.', 1)[0] == pkg.group(1): continue
        if re.search(r'(?<![\w.])' + name + r'\b', body):
            add.append('import %s;' % fq)
    if not add: return s
    m = pkg
    if not m: return s
    pos = m.end()
    return s[:pos] + '\n\n' + '\n'.join(add) + s[pos:]


# ---------------------------------------------------------------- rules
PKG = [
    (r'net\.minecraftforge\.registries\.RegistryObject', 'net.neoforged.neoforge.registries.DeferredHolder'),
    (r'net\.minecraftforge\.registries\.DeferredRegister', 'net.neoforged.neoforge.registries.DeferredRegister'),
    (r'import net\.minecraftforge\.registries\.ForgeRegistries;', 'import net.minecraft.core.registries.BuiltInRegistries;'),
    (r'net\.minecraftforge\.eventbus\.api\.', 'net.neoforged.bus.api.'),
    (r'import net\.minecraftforge\.fml\.common\.Mod;', 'import net.neoforged.fml.common.Mod;\nimport net.neoforged.fml.common.EventBusSubscriber;'),
    (r'net\.minecraftforge\.api\.distmarker\.', 'net.neoforged.api.distmarker.'),
    (r'net\.minecraftforge\.fml\.', 'net.neoforged.fml.'),
    (r'net\.minecraftforge\.common\.ForgeConfigSpec', 'net.neoforged.neoforge.common.ModConfigSpec'),
    (r'net\.minecraftforge\.common\.MinecraftForge', 'net.neoforged.neoforge.common.NeoForge'),
    (r'net\.minecraftforge\.common\.ForgeSpawnEggItem', 'net.neoforged.neoforge.common.DeferredSpawnEggItem'),
    (r'net\.minecraftforge\.event\.', 'net.neoforged.neoforge.event.'),
    (r'net\.minecraftforge\.client\.', 'net.neoforged.neoforge.client.'),
    (r'net\.minecraftforge\.common\.', 'net.neoforged.neoforge.common.'),
    (r'net\.minecraftforge\.items\.', 'net.neoforged.neoforge.items.'),
    (r'net\.minecraftforge\.fluids\.', 'net.neoforged.neoforge.fluids.'),
    # GeckoLib 4.0 (1.20.1) -> 4.5+ package layout
    (r'software\.bernie\.geckolib\.core\.animatable\.instance\.', 'software.bernie.geckolib.animatable.instance.'),
    (r'software\.bernie\.geckolib\.core\.animatable\.model\.CoreGeoBone', 'software.bernie.geckolib.cache.object.GeoBone'),
    (r'software\.bernie\.geckolib\.core\.animatable\.GeoAnimatable', 'software.bernie.geckolib.animatable.GeoAnimatable'),
    (r'software\.bernie\.geckolib\.core\.object\.PlayState', 'software.bernie.geckolib.animation.PlayState'),
    (r'software\.bernie\.geckolib\.core\.object\.DataTicket', 'software.bernie.geckolib.constant.dataticket.DataTicket'),
    (r'software\.bernie\.geckolib\.core\.animation\.', 'software.bernie.geckolib.animation.'),
    (r'software\.bernie\.geckolib\.core\.keyframe\.', 'software.bernie.geckolib.animation.keyframe.'),
    (r'software\.bernie\.geckolib\.util\.RenderUtils\b', 'software.bernie.geckolib.util.RenderUtil'),
    (r'\bRenderUtils\.', 'RenderUtil.'),
    (r'\bCoreGeoBone\b', 'GeoBone'),
]

REG = {  # ForgeRegistries field -> (Registries/BuiltInRegistries field, base type)
    'ITEMS': ('ITEM', 'Item'), 'BLOCKS': ('BLOCK', 'Block'), 'ENTITY_TYPES': ('ENTITY_TYPE', 'EntityType<?>'),
    'SOUND_EVENTS': ('SOUND_EVENT', 'SoundEvent'), 'MOB_EFFECTS': ('MOB_EFFECT', 'MobEffect'),
    'PARTICLE_TYPES': ('PARTICLE_TYPE', 'ParticleType<?>'), 'POTIONS': ('POTION', 'Potion'),
    'BLOCK_ENTITY_TYPES': ('BLOCK_ENTITY_TYPE', 'BlockEntityType<?>'), 'MENU_TYPES': ('MENU', 'MenuType<?>'),
    'ATTRIBUTES': ('ATTRIBUTE', 'Attribute'), 'FLUIDS': ('FLUID', 'Fluid'),
    'RECIPE_SERIALIZERS': ('RECIPE_SERIALIZER', 'RecipeSerializer<?>'), 'RECIPE_TYPES': ('RECIPE_TYPE', 'RecipeType<?>'),
    'FEATURES': ('FEATURE', 'Feature<?>'), 'ENCHANTMENTS': ('ENCHANTMENT', 'Enchantment'),
    'PAINTING_VARIANTS': ('PAINTING_VARIANT', 'PaintingVariant'), 'VILLAGER_PROFESSIONS': ('VILLAGER_PROFESSION', 'VillagerProfession'),
    'POI_TYPES': ('POINT_OF_INTEREST_TYPE', 'PoiType'), 'CREATIVE_MODE_TABS': ('CREATIVE_MODE_TAB', 'CreativeModeTab'),
    'BIOMES': ('BIOME', 'Biome'), 'GAME_EVENTS': ('GAME_EVENT', 'GameEvent'),
}

BASE_OF_ELEMENT = {  # RegistryObject<T> where T is not the registry base type
    'EntityType': 'EntityType<?>', 'BlockEntityType': 'BlockEntityType<?>', 'MenuType': 'MenuType<?>',
    'ParticleType': 'ParticleType<?>', 'SimpleParticleType': 'ParticleType<?>', 'RecipeSerializer': 'RecipeSerializer<?>',
    'RecipeType': 'RecipeType<?>', 'Feature': 'Feature<?>',
}


def fix_registry_objects(s):
    m = re.search(r'DeferredRegister<(.+?)>\s+REGISTRY\s*=', s)
    class_base = m.group(1) if m else None

    out = []; i = 0
    while True:
        j = s.find('RegistryObject<', i)
        if j < 0: out.append(s[i:]); break
        if j > 0 and (s[j - 1].isalnum() or s[j - 1] == '_'):
            out.append(s[i:j + 1]); i = j + 1; continue
        k = j + len('RegistryObject')
        depth = 0; e = k
        while True:
            if s[e] == '<': depth += 1
            elif s[e] == '>':
                depth -= 1
                if depth == 0: break
            e += 1
        elem = s[k + 1:e].strip()
        head = re.match(r'(?:\? extends )?([\w.]+)', elem).group(1)
        if head in BASE_OF_ELEMENT: base = BASE_OF_ELEMENT[head]
        elif head in BASE_TYPES: base = head
        elif class_base and '<' not in elem: base = class_base
        else: base = head if '<' not in elem else elem
        out.append(s[i:j]); out.append('DeferredHolder<%s, %s>' % (base, elem)); i = e + 1
    return ''.join(out)


BASE_TYPES = {'Item', 'Block', 'SoundEvent', 'MobEffect', 'Potion', 'Attribute', 'Fluid', 'Enchantment', 'PaintingVariant',
              'VillagerProfession', 'PoiType', 'CreativeModeTab', 'Biome', 'GameEvent', 'FluidType'}


def fix_forge_registries(s):
    # DeferredRegister.create(ForgeRegistries.X, ...) -> DeferredRegister.create(Registries.Y, ...)
    s = re.sub(r'DeferredRegister\.create\(ForgeRegistries\.(\w+)',
               lambda m: 'DeferredRegister.create(Registries.%s' % REG.get(m.group(1), (m.group(1), ''))[0], s)
    s = re.sub(r'ForgeRegistries\.(\w+)\.getValue\(', lambda m: 'BuiltInRegistries.%s.get(' % REG.get(m.group(1), (m.group(1),))[0], s)
    s = re.sub(r'ForgeRegistries\.(\w+)\.containsKey\(', lambda m: 'BuiltInRegistries.%s.containsKey(' % REG.get(m.group(1), (m.group(1),))[0], s)
    s = re.sub(r'ForgeRegistries\.(\w+)\.getValues\(\)', lambda m: 'BuiltInRegistries.%s.stream().toList()' % REG.get(m.group(1), (m.group(1),))[0], s)
    s = re.sub(r'ForgeRegistries\.(\w+)\.getKeys\(\)', lambda m: 'BuiltInRegistries.%s.keySet()' % REG.get(m.group(1), (m.group(1),))[0], s)
    s = re.sub(r'ForgeRegistries\.(\w+)\b', lambda m: 'BuiltInRegistries.%s' % REG.get(m.group(1), (m.group(1),))[0], s)
    return s


def fix_events(s):
    s = re.sub(r'@Mod\.EventBusSubscriber', '@EventBusSubscriber', s)
    s = s.replace('Mod.EventBusSubscriber.Bus', 'EventBusSubscriber.Bus')
    s = s.replace('MinecraftForge.EVENT_BUS', 'NeoForge.EVENT_BUS')
    s = re.sub(r'\bForgeConfigSpec\b', 'ModConfigSpec', s)
    s = re.sub(r'\bForgeSpawnEggItem\b', 'DeferredSpawnEggItem', s)
    return s


def fix_effect_holders(s):
    # 1.20.5+: MobEffectInstance / hasEffect / getEffect / removeEffect take Holder<MobEffect>; DeferredHolder is one
    s = re.sub(r'\b(GrowlingManModMobEffects\.\w+)\.get\(\)', r'\1', s)
    s = re.sub(r'\b(GrowlingManModPotions\.\w+)\.get\(\)', r'\1', s)
    return s


TAG_TYPES = {'putDouble': 'double', 'putBoolean': 'boolean', 'putString': 'String', 'putInt': 'int', 'putFloat': 'float', 'putLong': 'long'}


def fix_item_tags(s):
    # writes: R.getOrCreateTag().putX(k, v);  ->  { final String _tagName = k; ... CustomData.update(...) }
    key = '.getOrCreateTag().'
    out = []; i = 0
    while True:
        j = s.find(key, i)
        if j < 0: out.append(s[i:]); break
        r0 = receiver_start(s, j)
        recv = s[r0:j]
        mname = re.match(r'(\w+)\(', s[j + len(key):])
        meth = mname.group(1) if mname else None
        p = j + len(key) + (len(meth) if meth else 0)
        done = False
        if meth and (meth in TAG_TYPES or meth == 'remove') and stmt_prefix_ok(s, r0):
            end, args = split_args(s, p)
            if end + 1 < len(s) and s[end + 1] == ';':
                args = [a.strip() for a in args]
                ind = re.search(r'[ \t]*$', s[:r0]).group(0)
                if meth == 'remove':
                    rep = 'CustomData.update(DataComponents.CUSTOM_DATA, %s, tag -> tag.remove(%s));' % (recv, args[0])
                else:
                    rep = ('{\n%s\tfinal String _tagName = %s;\n%s\tfinal %s _tagValue = %s;\n%s\tCustomData.update(DataComponents.CUSTOM_DATA, %s, tag -> tag.%s(_tagName, _tagValue));\n%s}'
                           % (ind, args[0], ind, TAG_TYPES[meth], args[1], ind, recv, meth, ind))
                out.append(s[i:r0]); out.append(rep); i = end + 2; done = True
        if not done:
            out.append(s[i:j]); out.append('.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().'); i = j + len(key)
    return ''.join(out)


def fix_synched_data(s):
    # 1.20.5: defineSynchedData() { super...; this.entityData.define(A, v); } -> builder form
    m = re.search(r'protected void defineSynchedData\(\)\s*\{', s)
    if not m: return s
    end = match_paren(s, m.end() - 1)
    body = s[m.end():end]
    body = body.replace('super.defineSynchedData();', 'super.defineSynchedData(builder);')
    body = re.sub(r'this\.entityData\.define\(', 'builder.define(', body)
    return s[:m.start()] + 'protected void defineSynchedData(SynchedEntityData.Builder builder) {' + body + s[end:]


def fix_rl(s):
    out = []; i = 0; key = 'new ResourceLocation('
    while True:
        j = s.find(key, i)
        if j < 0: out.append(s[i:]); break
        out.append(s[i:j])
        end, args = split_args(s, j + len(key) - 1)
        args = [a.strip() for a in args]
        if len(args) == 2: out.append('ResourceLocation.fromNamespaceAndPath(%s, %s)' % (args[0], args[1]))
        else: out.append('ResourceLocation.parse(%s)' % args[0])
        i = end + 1
    return ''.join(out)


def fix_misc(s):
    s = re.sub(r'(\w+)\.setMaxUpStep\(([^;]+)\);', r'\1.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(\2);', s)
    s = re.sub(r'(?<![\w.])setMaxUpStep\(([^;]+)\);', r'this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(\1);', s)
    s = s.replace('AttributeModifier.Operation.ADDITION', 'AttributeModifier.Operation.ADD_VALUE')
    s = s.replace('AttributeModifier.Operation.MULTIPLY_BASE', 'AttributeModifier.Operation.ADD_MULTIPLIED_BASE')
    s = s.replace('AttributeModifier.Operation.MULTIPLY_TOTAL', 'AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL')
    s = s.replace('BASE_ATTACK_DAMAGE_UUID', 'BASE_ATTACK_DAMAGE_ID').replace('BASE_ATTACK_SPEED_UUID', 'BASE_ATTACK_SPEED_ID')
    return s


def fix_player_variables(s):
    s = re.sub(r'\((\w+)\.getCapability\(GrowlingManModVariables\.PLAYER_VARIABLES_CAPABILITY, null\)\.orElse\(new GrowlingManModVariables\.PlayerVariables\(\)\)\)',
               r'\1.getData(GrowlingManModVariables.PLAYER_VARIABLES)', s)
    key = '.getCapability(GrowlingManModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {'
    while True:
        j = s.find(key)
        if j < 0: break
        r0 = receiver_start(s, j); recv = s[r0:j]
        ob = j + len(key) - 1
        cb = match_paren(s, ob)
        assert s[cb + 1:cb + 3] == ');', s[cb:cb + 10]
        body = s[ob + 1:cb].replace('capability.', '_vars.')
        ind = re.search(r'[ \t]*$', s[:r0]).group(0)
        body = re.sub(r'\n' + ind + r'\t', '\n' + ind, body).rstrip()
        rep = 'GrowlingManModVariables.PlayerVariables _vars = %s.getData(GrowlingManModVariables.PLAYER_VARIABLES);%s' % (recv, body)
        s = s[:r0] + rep + s[cb + 3:]
    return s


RENDER_METHODS = r'(?:renderToBuffer|preRender|actuallyRender|renderRecursively|postRender)'


def fix_render_color(s):
    # 1.21: model / GeckoLib render methods take one packed ARGB int instead of four floats
    names = set()
    def decl(m):
        names.add((m.group(2), m.group(3), m.group(4), m.group(5)))
        return m.group(1) + 'int color)'
    s = re.sub(r'(public void ' + RENDER_METHODS + r'\([^{;]*?)float (\w+),\s*float (\w+),\s*float (\w+),\s*float (\w+)\)', decl, s)
    for r, g, b, a in names:
        s = re.sub(r'\b%s,\s*%s,\s*%s,\s*%s\)' % (r, g, b, a), 'color)', s)
    s = re.sub(r'(\.reRender\([^;]*?OverlayTexture\.NO_OVERLAY),\s*1,\s*1,\s*1,\s*1\)', r'\1, -1)', s)
    return s


def remove_method(s, sig_regex):
    """Remove '@Override? <sig> { ... }' blocks whose signature matches sig_regex."""
    while True:
        m = re.search(r'\n([ \t]*)(?:@Override\s*\n[ \t]*)?' + sig_regex + r'[^{;]*\{', s)
        if not m: return s
        end = match_paren(s, m.end() - 1)
        s = s[:m.start()] + s[end + 1:]


def fix_121(s):
    s = fix_render_color(s)
    s = remove_method(s, r'public Packet<ClientGamePacketListener> getAddEntityPacket\(\)')
    s = re.sub(r'public int getUseDuration\(ItemStack (\w+)\)', r'public int getUseDuration(ItemStack \1, LivingEntity entity)', s)
    s = re.sub(r'\.getUseDuration\(\)', '.getUseDuration(null)', s)
    s = re.sub(r'public boolean canChangeDimensions\(\)', 'public boolean canChangeDimensions(Level from, Level to)', s)
    s = re.sub(r'public boolean ignoreExplosion\(\)', 'public boolean ignoreExplosion(Explosion explosion)', s)
    s = re.sub(r'public void appendHoverText\(ItemStack (\w+), Level (\w+), List<Component> (\w+), TooltipFlag (\w+)\)',
               r'public void appendHoverText(ItemStack \1, Item.TooltipContext \2, List<Component> \3, TooltipFlag \4)', s)
    s = re.sub(r'super\.appendHoverText\((\w+), (\w+), (\w+), (\w+)\)', r'super.appendHoverText(\1, \2, \3, \4)', s)
    return s


def fix_entities(s):
    s = remove_method(s, r'public \w+\(PlayMessages\.SpawnEntity \w+, Level \w+\)')
    s = re.sub(r'protected double getAttackReachSqr\(LivingEntity (\w+)\) \{\s*return ([^;]+);',
               lambda m: 'protected boolean canPerformAttack(LivingEntity %s) {\n\t\t\t\treturn this.isTimeToAttack() && this.mob.distanceToSqr(%s) < %s && this.mob.getSensing().hasLineOfSight(%s);'
               % (m.group(1), m.group(1), m.group(2), m.group(1)), s)
    s = remove_method(s, r'public MobType getMobType\(\)')
    s = re.sub(r'public EntityDimensions getDimensions\(Pose (\w+)\) \{\s*return super\.getDimensions\(\1\)',
               r'public EntityDimensions getDefaultDimensions(Pose \1) {\n\t\treturn super.getDefaultDimensions(\1)', s)
    # finalizeSpawn lost its CompoundTag parameter in 1.20.5
    s = re.sub(r'(public SpawnGroupData finalizeSpawn\([^)]*?SpawnGroupData \w+),\s*@Nullable CompoundTag \w+\)', r'\1)', s)
    s = re.sub(r'(public SpawnGroupData finalizeSpawn\([^)]*?SpawnGroupData \w+),\s*CompoundTag \w+\)', r'\1)', s)
    s = re.sub(r'(\.finalizeSpawn\([^;]*?MobSpawnType\.\w+, [^,;()]+), (?:null|\w+)\)', r'\1)', s)
    s = s.replace('net.minecraftforge.event.ForgeEventFactory.', 'EventHooks.').replace('ForgeEventFactory.', 'EventHooks.')
    s = s.replace('net.neoforged.neoforge.event.ForgeEventFactory', 'net.neoforged.neoforge.event.EventHooks')
    s = re.sub(r'\bSpawnPlacementRegisterEvent\b', 'RegisterSpawnPlacementsEvent', s)
    s = s.replace('net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent', 'net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent')
    s = re.sub(r'public static void init\(\) \{(\s*)SpawnPlacements\.register\(', r'public static void init(RegisterSpawnPlacementsEvent event) {\1event.register(', s)
    s = re.sub(r'public static void init\(\) \{(\s*)\}', r'public static void init(RegisterSpawnPlacementsEvent event) {\1}', s)
    s = re.sub(r'SpawnPlacements\.Type\.(\w+)', r'SpawnPlacementTypes.\1', s)
    m = re.search(r'public static void init\(RegisterSpawnPlacementsEvent event\) \{', s)
    if m:
        e = match_paren(s, m.end() - 1)
        body = s[m.end():e]
        body = re.sub(r'\}\);(\s*)$', r'}, RegisterSpawnPlacementsEvent.Operation.REPLACE);\1', body) if 'Operation.REPLACE' not in body else body
        s = s[:m.end()] + body + s[e:]
    s = s.replace('item.isEdible() && this.isFood(itemstack)', 'this.isFood(itemstack)')
    s = s.replace('this.heal((float) item.getFoodProperties().getNutrition());',
                  'FoodProperties foodproperties = itemstack.getFoodProperties(this);\n\t\t\t\t\t\tfloat nutrition = foodproperties != null ? (float) foodproperties.nutrition() : 1;\n\t\t\t\t\t\tthis.heal(nutrition);')
    return s


def fix_attribute_modifiers(s):
    s = re.sub(r'UUID\.fromString\(("[0-9a-fA-F-]{36}")\)(?=\)|,)', lambda m: 'ResourceLocation.fromNamespaceAndPath("growling_man", %s)' % m.group(1).lower(), s)
    out = []; i = 0; key = 'new AttributeModifier('
    while True:
        j = s.find(key, i)
        if j < 0: out.append(s[i:]); break
        end, args = split_args(s, j + len(key) - 1)
        if len(args) == 4 and args[1].strip().startswith('"'):
            args = [args[0], args[2], args[3]]
        out.append(s[i:j]); out.append(key + ','.join(args).strip() + ')'); i = end + 1
    s = ''.join(out)
    # hasModifier(new AttributeModifier(id, ...)) -> hasModifier(id)
    while True:
        m = re.search(r'hasModifier\(\s*\(?new AttributeModifier\(', s)
        if not m: break
        end, args = split_args(s, m.end() - 1)
        k = end + 1
        if s[m.end() - len('new AttributeModifier(') - 1] == '(' and not s[m.start():m.end()].rstrip('(').endswith('hasModifier'):
            k += 1  # closing paren of the extra (...)
        s = s[:m.start()] + 'hasModifier(' + args[0].strip() + s[k:]
    return s


def fix_projectiles(s):
    if re.search(r'extends AbstractArrow\b', s):
        s = re.sub(r'super\(type, world, PROJECTILE_ITEM\);', 'super(type, world);', s)
        s = re.sub(r'(public \w+\(EntityType<[^>]+> type, double x, double y, double z, Level world)\) \{\s*super\(type, x, y, z, world, PROJECTILE_ITEM\);',
                   r'\1, @Nullable ItemStack firedFromWeapon) {\n\t\tsuper(type, x, y, z, world, PROJECTILE_ITEM, firedFromWeapon);', s)
        s = re.sub(r'(public \w+\(EntityType<[^>]+> type, LivingEntity entity, Level world)\) \{\s*super\(type, entity, world, PROJECTILE_ITEM\);',
                   r'\1, @Nullable ItemStack firedFromWeapon) {\n\t\tsuper(type, entity, world, PROJECTILE_ITEM, firedFromWeapon);', s)
        s = re.sub(r'(public \w+\(EntityType<[^>]+> type, double x, double y, double z, Level world)\) \{\s*super\(type, x, y, z, world\);',
                   r'\1, @Nullable ItemStack firedFromWeapon) {\n\t\tsuper(type, x, y, z, world, PROJECTILE_ITEM, firedFromWeapon);', s)
        s = re.sub(r'(public \w+\(EntityType<[^>]+> type, LivingEntity entity, Level world)\) \{\s*super\(type, entity, world\);',
                   r'\1, @Nullable ItemStack firedFromWeapon) {\n\t\tsuper(type, entity, world, PROJECTILE_ITEM, firedFromWeapon);', s)
        s = s.replace('protected ItemStack getPickupItem() {\n\t\treturn PROJECTILE_ITEM;', 'protected ItemStack getDefaultPickupItem() {\n\t\treturn PROJECTILE_ITEM;')
    # callers of the projectile constructors: (TYPE.get(), entity, level) -> + null weapon
    s = re.sub(r'(new \w+Entity\(GrowlingManModEntities\.\w+\.get\(\), \w+, \w+(?:\.level\(\))?)\)', r'\1, null)', s)
    s = re.sub(r'(new \w+Entity\(GrowlingManModEntities\.\w+\.get\(\), [^,;()]+, [^,;()]+, [^,;()]+, \w+(?:\.level\(\))?)\)', r'\1, null)', s)
    s = re.sub(r'(?<![\w.])(?!PortUtil\b)(\w+)\.setKnockback\(([^;]+)\);', r'PortUtil.setKnockback(\1, \2);', s)
    # 1.20 item damage calls
    s = re.sub(r'if \((\w+)\.hurt\(([^,;]+), RandomSource\.create\(\), (\w+)\)\) \{\s*\1\.shrink\(1\);\s*\1\.setDamageValue\(0\);\s*\}',
               r'PortUtil.hurtAndBreak(\1, \2, \3);', s)
    s = re.sub(r'(\w+)\.hurtAndBreak\(([^,;]+), RandomSource\.create\(\), (\w+), \(\) -> \{\s*\1\.shrink\(1\);\s*\1\.setDamageValue\(0\);\s*\}\);',
               r'PortUtil.hurtAndBreak(\1, \2, \3);', s)
    s = re.sub(r'(?<![\w.])(this\.)?dropExperience\(\)', lambda m: (m.group(1) or '') + 'dropExperience(null)', s)
    s = re.sub(r'(\.create\(\w+, )null, null, ', r'\1null, ', s)
    s = re.sub(r'(\.renderToBuffer\([^;]*?),\s*1,\s*1,\s*1,\s*1\)', r'\1, -1)', s)
    return s


def fix_event_api(s):
    s = s.replace('import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;', 'import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;')
    s = re.sub(r'\bLivingAttackEvent\b', 'LivingIncomingDamageEvent', s)
    s = s.replace('import net.neoforged.neoforge.event.entity.living.ShieldBlockEvent;', 'import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;')
    s = re.sub(r'\bShieldBlockEvent\b', 'LivingShieldBlockEvent', s)
    s = re.sub(r'if \(event != null && event\.isCancelable\(\)\) \{(\s*)event\.setCanceled\(true\);',
               r'if (event instanceof ICancellableEvent _cancellable) {\1_cancellable.setCanceled(true);', s)
    s = re.sub(r'else if \(event != null && event\.hasResult\(\)\) \{\s*event\.setResult\([^;]*\);\s*\}', '', s)
    # TickEvent.PlayerTickEvent (END phase) -> PlayerTickEvent.Post
    def tick(m):
        body = m.group(2).replace('event.player', 'event.getEntity()')
        body = re.sub(r'\n\t', '\n', body)
        return m.group(1) + 'PlayerTickEvent.Post event) {' + body + '\n\t}'
    s = re.sub(r'(public static void \w+\()TickEvent\.PlayerTickEvent event\) \{\s*if \(event\.phase == TickEvent\.Phase\.END\) \{([\s\S]*?)\n\t\t\}\n\t\}', tick, s)
    s = s.replace('import net.neoforged.neoforge.event.TickEvent;', 'import net.neoforged.neoforge.event.tick.PlayerTickEvent;') if 'TickEvent.' not in s.replace('PlayerTickEvent.', '') else s
    return s


def fix_small_apis(s):
    s = s.replace('.getSkinTextureLocation()', '.getSkin().texture()')
    s = s.replace('.saturationMod(', '.saturationModifier(')
    s = re.sub(r'\bAdvancement (\w+) = (\w+)\.server\.getAdvancements\(\)\.getAdvancement\(', r'AdvancementHolder \1 = \2.server.getAdvancements().get(', s)
    s = re.sub(r'BuiltInRegistries\.(\w+)\.tags\(\)\.getTag\(([^;]*?)\)\.getRandomElement\(RandomSource\.create\(\)\)\.orElseGet\(\(\) -> ([\w.]+)\)',
               r'BuiltInRegistries.\1.getTag(\2).flatMap(_t -> _t.getRandomElement(RandomSource.create())).map(Holder::value).orElseGet(() -> \3)', s)
    s = s.replace('event.getOriginalTarget()', 'event.getOriginalAboutToBeSetTarget()')
    s = re.sub(r'\.setCustomClientFactory\([\w.]+::new\)', '', s)
    s = s.replace('.getItem().isEdible()', '.has(DataComponents.FOOD)')
    return s


def fix_plants(s):
    s = re.sub(r'public float\[\] getBeaconColorMultiplier\((BlockState \w+, LevelReader \w+, BlockPos \w+, BlockPos \w+)\) \{\s*return new float\[\]\{([^,]+), ([^,]+), ([^}]+)\};',
               r'public Integer getBeaconColorMultiplier(\1) {\n\t\treturn FastColor.ARGB32.colorFromFloat(1f, \2, \3, \4);', s)
    s = remove_method(s, r'public PlantType getPlantType\(BlockGetter \w+, BlockPos \w+\)')
    s = s.replace('import net.neoforged.neoforge.common.PlantType;\n', '').replace('import net.neoforged.neoforge.common.IPlantable;\n', '')
    s = re.sub(r'public boolean canSustainPlant\(BlockState (\w+), BlockGetter (\w+), BlockPos (\w+), Direction (\w+), IPlantable \w+\) \{\s*return true;',
               r'public TriState canSustainPlant(BlockState \1, BlockGetter \2, BlockPos \3, Direction \4, BlockState plant) {\n\t\treturn TriState.TRUE;', s)
    s = re.sub(r'public boolean isValidBonemealTarget\(LevelReader (\w+), BlockPos (\w+), BlockState (\w+), boolean \w+\)',
               r'public boolean isValidBonemealTarget(LevelReader \1, BlockPos \2, BlockState \3)', s)
    return s


def fix_vertex_api(s):
    if 'endVertex()' not in s and 'getBuilder()' not in s: return s
    s = re.sub(r'BufferBuilder (\w+) = Tesselator\.getInstance\(\)\.getBuilder\(\);\s*\1\.begin\(', r'BufferBuilder \1 = Tesselator.getInstance().begin(', s)
    def stmt(m):
        t = m.group(0)
        t = re.sub(r'\.vertex\(', '.addVertex(', t); t = re.sub(r'\.uv\(', '.setUv(', t); t = re.sub(r'\.color\(', '.setColor(', t)
        t = re.sub(r'\.uv2\(', '.setLight(', t); t = re.sub(r'\.overlayCoords\(', '.setOverlay(', t); t = re.sub(r'\.normal\(', '.setNormal(', t)
        return t.replace('.endVertex()', '')
    s = re.sub(r'[^;{}\n]*\.endVertex\(\);', stmt, s)
    s = re.sub(r'\b(bufferBuilder|bufferbuilder|builder)\.end\(\)', r'\1.buildOrThrow()', s)
    return s


def fix_item_attribute_event(s):
    s = s.replace('ForgeMod.ENTITY_REACH.get()', 'Attributes.ENTITY_INTERACTION_RANGE').replace('ForgeMod.BLOCK_REACH.get()', 'Attributes.BLOCK_INTERACTION_RANGE')
    s = s.replace('ForgeMod.STEP_HEIGHT_ADDITION.get()', 'Attributes.STEP_HEIGHT').replace('ForgeMod.ENTITY_GRAVITY.get()', 'Attributes.GRAVITY')
    s = re.sub(r'(?<!Neo)ForgeMod\.SWIM_SPEED\.get\(\)', 'NeoForgeMod.SWIM_SPEED', s)
    key = re.compile(r'if \(event instanceof ItemAttributeModifierEvent (\w+) && \1\.getSlotType\(\) == EquipmentSlot\.(\w+)\) \{')
    while True:
        m = key.search(s)
        if not m: break
        e = match_paren(s, m.end() - 1)
        var, slot = m.group(1), m.group(2)
        body = s[m.end():e]
        out = []; i = 0; k = var + '.addModifier('
        while True:
            j = body.find(k, i)
            if j < 0: out.append(body[i:]); break
            end, args = split_args(body, j + len(k) - 1)
            out.append(body[i:j]); out.append(k + ','.join(args) + ', EquipmentSlotGroup.%s)' % slot); i = end + 1
        s = s[:m.start()] + 'if (event instanceof ItemAttributeModifierEvent %s) {' % var + ''.join(out) + s[e:]
    return s


def fix_misc2(s):
    s = re.sub(r'(\.create\(\w+, )null,\s*null,', r'\1null,', s)
    s = re.sub(r'\.getAdvancements\(\)\.getAdvancement\(', '.getAdvancements().get(', s)
    s = re.sub(r'(\w+)\.hurtAndBreak\((\w+), (\w+), \w+ -> \w+\.broadcastBreakEvent\((EquipmentSlot\.\w+)\)\)', r'\1.hurtAndBreak(\2, \3, \4)', s)
    s = re.sub(r'(\w+)\.hurtAndBreak\((\w+), (\w+), \w+ -> \w+\.broadcastBreakEvent\((\w+)\.getUsedItemHand\(\)\)\)', r'\1.hurtAndBreak(\2, \3, LivingEntity.getSlotForHand(\4.getUsedItemHand()))', s)
    s = re.sub(r'(\w+)\.hurtAndBreak\(([^,;]+), [\w.]+\.getRandom\(\), (\w+), \(\) -> \{\s*\1\.shrink\(1\);\s*\1\.setDamageValue\(0\);\s*\}\);', r'PortUtil.hurtAndBreak(\1, \2, \3);', s)
    s = re.sub(r'if \((\w+)\.hurt\(([^,;]+), [\w.]+\.getRandom\(\), (\w+)\)\) \{\s*\1\.shrink\(1\);\s*\1\.setDamageValue\(0\);\s*\}', r'PortUtil.hurtAndBreak(\1, \2, \3);', s)
    s = re.sub(r'this\.getUseDuration\((\w+)\)', r'this.getUseDuration(\1, entity)', s)
    s = re.sub(r'new ClientboundUpdateMobEffectPacket\(([^;]+?), (new MobEffectInstance\([^;]*?\))\)\)', r'new ClientboundUpdateMobEffectPacket(\1, \2, false))', s)
    s = re.sub(r'new ClientboundUpdateMobEffectPacket\((\w+\.getId\(\)), (\w+)\)', r'new ClientboundUpdateMobEffectPacket(\1, \2, false)', s)
    s = s.replace('.alwaysEat()', '.alwaysEdible()').replace('.meat()', '')
    if 'PlayerEvent' in s or 'TickEvent' in s:
        s = re.sub(r'\bevent\.player\b', 'event.getEntity()', s)
    return s


def fix_default_attributes(s):
    m = re.search(r'\n\t@Override\n\tpublic Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers\(EquipmentSlot (\w+)\) \{', s)
    if not m: return s
    e = match_paren(s, m.end() - 1)
    body = s[m.end():e]
    slot = re.search(r'== EquipmentSlot\.(\w+)', body).group(1)
    puts = re.findall(r'builder\.put\(([\w.]+), (new AttributeModifier\([^;]*\))\);', body)
    chain = 'ItemAttributeModifiers.builder()' + ''.join('.add(%s, %s, EquipmentSlotGroup.%s)' % (a, mod, slot) for a, mod in puts) + '.build()'
    s = s[:m.start()] + s[e + 1:]
    s = re.sub(r'(super\(new Item\.Properties\(\)[^;]*?)\);', lambda mm: mm.group(1) + '.attributes(%s));' % chain, s, count=1)
    return s


def fix_brewing(s):
    s = re.sub(r'@EventBusSubscriber\(bus = EventBusSubscriber\.Bus\.MOD\)\n(public class \w+ implements IBrewingRecipe)', r'@EventBusSubscriber\n\1', s)
    s = re.sub(r'public static void init\(FMLCommonSetupEvent event\) \{\s*event\.enqueueWork\(\(\) -> BrewingRecipeRegistry\.addRecipe\((new \w+\(\))\)\);',
               r'public static void init(RegisterBrewingRecipesEvent event) {\n\t\tevent.getBuilder().addRecipe(\1);', s)
    s = re.sub(r'PotionUtils\.getPotion\((\w+)\) == ([\w.]+)', r'\1.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(\2)', s)
    s = re.sub(r'PotionUtils\.setPotion\(new ItemStack\((Items\.\w+)\), ([\w.]+)\)', r'PotionContents.createItemStack(\1, \2)', s)
    s = re.sub(r'PotionUtils\.setPotion\((\w+), ([\w.]+)\);', r'\1.set(DataComponents.POTION_CONTENTS, new PotionContents(\2));', s)
    s = re.sub(r'import net\.minecraft\.world\.item\.alchemy\.PotionUtils;\n', '', s)
    s = re.sub(r'import net\.neoforged\.neoforge\.common\.brewing\.BrewingRecipeRegistry;\n', '', s)
    return s


def fix_blocks(s):
    s = s.replace('new ForgeSoundType(', 'new DeferredSoundType(')
    s = re.sub(r'import net\.neoforged\.neoforge\.common\.util\.ForgeSoundType;', 'import net.neoforged.neoforge.common.util.DeferredSoundType;', s)
    s = re.sub(r'public ItemStack getCloneItemStack\(BlockState (\w+), HitResult (\w+), BlockGetter (\w+), BlockPos (\w+), Player (\w+)\)',
               r'public ItemStack getCloneItemStack(BlockState \1, HitResult \2, LevelReader \3, BlockPos \4, Player \5)', s)
    s = re.sub(r'public BlockPathTypes getBlockPathType\(BlockState (\w+), BlockGetter (\w+), BlockPos (\w+), Mob (\w+)\)',
               r'public PathType getBlockPathType(BlockState \1, BlockGetter \2, BlockPos \3, @Nullable Mob \4)', s)
    s = re.sub(r'\bBlockPathTypes\.', 'PathType.', s)
    s = re.sub(r'import net\.minecraft\.world\.level\.pathfinder\.BlockPathTypes;', 'import net.minecraft.world.level.pathfinder.PathType;', s)
    # FlowerBlock(Supplier<MobEffect>, ticks, props) -> (Holder<MobEffect>, seconds, props)
    s = re.sub(r'super\(\(\) -> (MobEffects\.\w+), (\d+),', lambda m: 'super(%s, %sf,' % (m.group(1), int(m.group(2)) / 20), s)
    s = remove_method(s, r'public int getEffectDuration\(\)')
    s = remove_method(s, r'public MobEffect getSuspiciousEffect\(\)')
    # DoorBlock / TrapDoorBlock / PressurePlate etc.: (props, BlockSetType) -> (BlockSetType, props)
    s = re.sub(r'super\((BlockBehaviour\.Properties\.of\(\)[^;]*?), (BlockSetType\.\w+)\);', r'super(\2, \1);', s)
    s = re.sub(r'super\(\(\) -> ([\w.]+\.defaultBlockState\(\)),', r'super(\1,', s)
    # Block#use(state, level, pos, player, hand, hit) -> useWithoutItem(state, level, pos, player, hit)
    m = re.search(r'public InteractionResult use\(BlockState (\w+), Level (\w+), BlockPos (\w+), Player (\w+), InteractionHand (\w+), BlockHitResult (\w+)\) \{', s)
    if m:
        bs, w, pos, pl, hand, hit = m.groups()
        e = match_paren(s, m.end() - 1); body = s[m.end():e]
        body = body.replace('super.use(%s, %s, %s, %s, %s, %s)' % (bs, w, pos, pl, hand, hit), 'super.useWithoutItem(%s, %s, %s, %s, %s)' % (bs, w, pos, pl, hit))
        if re.search(r'\b%s\b' % hand, body):
            body = '\n\t\tInteractionHand %s = InteractionHand.MAIN_HAND;' % hand + body
        s = s[:m.start()] + 'public InteractionResult useWithoutItem(BlockState %s, Level %s, BlockPos %s, Player %s, BlockHitResult %s) {' % (bs, w, pos, pl, hit) + body + s[e:]
    s = re.sub(r'\.use\(([^;]*?), InteractionHand\.MAIN_HAND, (BlockHitResult\.miss\()', r'.useWithoutItem(\1, \2', s)
    return s


def fix_entities2(s):
    s = re.sub(r'protected void dropCustomDeathLoot\(DamageSource (\w+), int \w+, boolean (\w+)\) \{\s*super\.dropCustomDeathLoot\(\1, \w+, \2\);',
               r'protected void dropCustomDeathLoot(ServerLevel serverLevel, DamageSource \1, boolean \2) {\n\t\tsuper.dropCustomDeathLoot(serverLevel, \1, \2);', s)
    s = re.sub(r'super\.finalizeSpawn\((\w+), (\w+), (\w+), (\w+), \w+\)', r'super.finalizeSpawn(\1, \2, \3, \4)', s)
    s = re.sub(r'new FollowOwnerGoal\(this, ([^,;]+), ([^,;]+), ([^,;]+), (?:true|false)\)', r'new FollowOwnerGoal(this, \1, \2, \3)', s)
    s = re.sub(r'public double getMyRidingOffset\(\) \{\s*return ([^;]+);\s*\}',
               r'public Vec3 getVehicleAttachmentPoint(Entity vehicle) {\n\t\treturn new Vec3(0, -(\1), 0);\n\t}', s)
    m = re.search(r'public static void init\(RegisterSpawnPlacementsEvent event\) \{', s)
    if m:
        e = match_paren(s, m.end() - 1); body = s[m.end():e]
        j = body.find('event.register(')
        if j >= 0 and 'Operation.REPLACE' not in body:
            end, args = split_args(body, j + len('event.register'))
            if len(args) == 4:
                body = body[:end] + ', RegisterSpawnPlacementsEvent.Operation.REPLACE' + body[end:]
                s = s[:m.end()] + body + s[e:]
    return s


def fix_misc3(s):
    s = s.replace('.getEffect().getCategory()', '.getEffect().value().getCategory()')
    s = re.sub(r'(\w+)\.getExperienceReward\(\)', r'PortUtil.experienceReward(\1)', s)
    s = re.sub(r'BuiltInRegistries\.(\w+)\s*\.getValue\(', r'BuiltInRegistries.\1.get(', s)
    s = s.replace('minecraft.getPartialTick()', 'minecraft.getTimer().getGameTimeDeltaPartialTick(true)')
    s = re.sub(r'NetworkHooks\.openScreen\(\(ServerPlayer\) (\w+), ', r'\1.openMenu(', s)
    s = re.sub(r'CompoundTag (\w+) = ([^;]+?)\.getTag\(\);\s*if \(\1 != null\)\s*([^;]+?)\.setTag\(\1\.copy\(\)\);',
               r'\3.applyComponents(\2.getComponentsPatch());', s)
    s = re.sub(r'ItemProperties\.getProperty\((Items\.\w+), ', r'ItemProperties.getProperty(new ItemStack(\1), ', s)
    return s


def fix_armor_tick(s):
    # NeoForge 1.21 dropped IItemExtension#onArmorTick: run it from inventoryTick while worn
    m = re.search(r'\n\t@Override\n\tpublic void onArmorTick\(ItemStack (\w+), Level (\w+), Player (\w+)\) \{', s)
    if not m: return s
    st, w, pl = m.groups()
    e = match_paren(s, m.end() - 1); body = s[m.end():e].rstrip()
    body = body.replace('\n\t\t', '\n\t\t\t')
    new = ('\n\t@Override\n\tpublic void inventoryTick(ItemStack %s, Level %s, Entity _wearer, int slot, boolean selected) {\n'
           '\t\tsuper.inventoryTick(%s, %s, _wearer, slot, selected);\n'
           '\t\tif (_wearer instanceof Player %s && Iterables.contains(%s.getArmorSlots(), %s)) {%s\n\t\t}\n\t}') % (st, w, st, w, pl, pl, st, body)
    return s[:m.start()] + new + s[e + 1:]


def fix_gui(s):
    s = re.sub(r'event\.getWindow\(\)\.getGuiScaledWidth\(\)', 'event.getGuiGraphics().guiWidth()', s)
    s = re.sub(r'event\.getWindow\(\)\.getGuiScaledHeight\(\)', 'event.getGuiGraphics().guiHeight()', s)
    s = re.sub(r'this\.renderBackground\((\w+)\);', r'this.renderBackground(\1, mouseX, mouseY, partialTicks);', s)
    s = re.sub(r'(\}\) \{\s*@Override\s*public void )render\(GuiGraphics (\w+), int (\w+), int (\w+), float (\w+)\) \{([^{}]*?)super\.render\(',
               r'\1renderWidget(GuiGraphics \2, int \3, int \4, float \5) {\6super.renderWidget(', s)
    return s


def fix_network(s):
    s = re.sub(r'= new ImageButton\((?=[^;]*?, ResourceLocation\.parse\([^;]*?\), \d+, \d+, e -> \{)', '= new PortImageButton(', s)
    s = re.sub(r'GrowlingManMod\.PACKET_HANDLER\.sendToServer\(', 'PacketDistributor.sendToServer(', s)
    return s


ENCH = r'(?:GrowlingManModEnchantments\.\w+(?:\.get\(\))?|Enchantments\.\w+)'


def fix_enchantments(s):
    strip = lambda a: re.sub(r'\.get\(\)$', '', a.strip())
    out = []; i = 0; key = 'EnchantmentHelper.getItemEnchantmentLevel('
    while True:
        j = s.find(key, i)
        if j < 0: out.append(s[i:]); break
        end, args = split_args(s, j + len(key) - 1)
        if len(args) == 2 and re.fullmatch(ENCH, args[0].strip()):
            out.append(s[i:j]); out.append('PortUtil.enchantmentLevel(%s, %s)' % (args[1].strip(), strip(args[0]))); i = end + 1
        else:
            out.append(s[i:j + len(key)]); i = j + len(key)
    s = ''.join(out)
    for meth, fmt in (('.getEnchantmentLevel(', 'PortUtil.enchantmentLevel(%s, %s)'), ('.enchant(', 'PortUtil.enchant(%s, %s)')):
        out = []; i = 0
        while True:
            j = s.find(meth, i)
            if j < 0: out.append(s[i:]); break
            end, args = split_args(s, j + len(meth) - 1)
            if re.fullmatch(ENCH, args[0].strip()) and len(args) == (1 if meth == '.getEnchantmentLevel(' else 2):
                r0 = receiver_start(s, j)
                rest = ', '.join([strip(args[0])] + [a.strip() for a in args[1:]])
                # receiver text already copied into out: cut it back
                joined = ''.join(out) + s[i:r0]
                out = [joined, fmt % (s[r0:j], rest)]; i = end + 1
            else:
                out.append(s[i:j + len(meth)]); i = j + len(meth)
        s = ''.join(out)
    return s


INCORRECT = ['INCORRECT_FOR_WOODEN_TOOL', 'INCORRECT_FOR_STONE_TOOL', 'INCORRECT_FOR_IRON_TOOL', 'INCORRECT_FOR_DIAMOND_TOOL', 'INCORRECT_FOR_NETHERITE_TOOL']


def fix_tool_tiers(s):
    m = re.search(r'super\(new Tier\(\) \{', s)
    if not m: return s
    cls = re.search(r'extends (\w+)', s).group(1)
    ob = m.end() - 1; cb = match_paren(s, ob)
    body = s[ob + 1:cb]
    lvl = re.search(r'public int getLevel\(\) \{\s*return (\d+);\s*\}', body)
    if lvl:
        tag = INCORRECT[min(int(lvl.group(1)), 4)]
        body = body.replace(lvl.group(0), 'public TagKey<Block> getIncorrectBlocksForDrops() {\n\t\t\t\treturn BlockTags.%s;\n\t\t\t}' % tag)
    body = re.sub(r'\n(\t+)public ', r'\n\1@Override\n\1public ', body)
    body = re.sub(r'\n\t\t\t', '\n\t\t', body).rstrip('\t')
    # remaining arguments of the super call
    end, args = split_args(s, m.start() + len('super'))
    rest = [a.strip() for a in args[1:]]
    if len(rest) == 3:
        dmg, spd, props = rest
        call = 'super(TOOL_TIER, %s.attributes(%s.createAttributes(TOOL_TIER, %s, %s)))' % (props, cls, dmg, spd)
    else:
        call = 'super(TOOL_TIER, %s)' % ', '.join(rest)
    s = s[:m.start()] + call + s[end + 1:]
    cm = re.search(r'public class \w+ extends \w+(?: implements [\w, <>]+)? \{\n', s)
    s = s[:cm.end()] + '\tprivate static final Tier TOOL_TIER = new Tier() {' + body + '\t};\n\n' + s[cm.end():]
    return s


def fix_mob_effects(s):
    if not re.search(r'extends MobEffect\b', s): return s
    s = s.replace('public boolean isDurationEffectTick(int duration, int amplifier)', 'public boolean shouldApplyEffectTickThisTick(int duration, int amplifier)')
    m = re.search(r'public void applyEffectTick\(LivingEntity (\w+), int (\w+)\) \{', s)
    if m:
        e = match_paren(s, m.end() - 1)
        body = s[m.end():e].replace('return;', 'return true;')
        body = re.sub(r'super\.applyEffectTick\((\w+), (\w+)\);', '', body)
        s = s[:m.start()] + 'public boolean applyEffectTick(LivingEntity %s, int %s) {' % (m.group(1), m.group(2)) + body.rstrip() + '\n\t\treturn super.applyEffectTick(%s, %s);\n\t}' % (m.group(1), m.group(2)) + s[e + 1:]
    s = re.sub(r'public void addAttributeModifiers\(LivingEntity (\w+), AttributeMap \w+, int (\w+)\) \{', r'public void onEffectStarted(LivingEntity \1, int \2) {', s)
    s = re.sub(r'super\.addAttributeModifiers\((\w+), \w+, (\w+)\);', r'super.onEffectStarted(\1, \2);', s)
    m = re.search(r'(@Override\s*)?public void removeAttributeModifiers\(LivingEntity (\w+), AttributeMap \w+, int (\w+)\) \{', s)
    if m:
        s = s[:m.start()] + '@Override\n\tpublic void onEffectRemoved(LivingEntity %s, int %s) {' % (m.group(2), m.group(3)) + s[m.end():]
        s = re.sub(r'\n\s*super\.removeAttributeModifiers\(\w+, \w+, \w+\);', '', s)
        s = re.sub(r'(public class \w+ extends MobEffect)( \{)', r'\1 implements PortUtil.EffectRemovedListener\2', s)
    return s


def convert(s):
    o = s
    for a, b in PKG: s = re.sub(a, b, s)
    s = fix_rl(s)
    s = fix_registry_objects(s)
    s = fix_forge_registries(s)
    s = fix_events(s)
    s = fix_effect_holders(s)
    s = fix_item_tags(s)
    s = fix_player_variables(s)
    s = fix_synched_data(s)
    s = fix_misc(s)
    s = fix_121(s)
    s = fix_entities(s)
    s = fix_attribute_modifiers(s)
    s = fix_projectiles(s)
    s = fix_event_api(s)
    s = fix_small_apis(s)
    s = fix_plants(s)
    s = fix_vertex_api(s)
    s = fix_item_attribute_event(s)
    s = fix_misc2(s)
    s = fix_brewing(s)
    s = fix_blocks(s)
    s = fix_entities2(s)
    s = fix_misc3(s)
    s = fix_armor_tick(s)
    s = fix_gui(s)
    s = fix_default_attributes(s)
    s = fix_network(s)
    s = fix_enchantments(s)
    s = fix_tool_tiers(s)
    s = fix_mob_effects(s)
    if s != o: s = add_imports(s)
    # drop duplicate imports
    seen = set(); lines = []
    for l in s.split('\n'):
        if l.startswith('import '):
            if l in seen: continue
            seen.add(l)
        lines.append(l)
    return '\n'.join(lines)


if __name__ == '__main__':
    only = set(open(sys.argv[2]).read().split()) if len(sys.argv) > 2 else None
    n = 0
    for dp, _, fs in os.walk(ROOT):
        for f in fs:
            if not f.endswith('.java'): continue
            p = os.path.join(dp, f)
            s = open(p, encoding='utf-8').read()
            try:
                t = convert(s)
            except Exception as e:
                print('FAILED', p, e); continue
            if t != s:
                open(p, 'w', encoding='utf-8').write(t); n += 1
    print('changed', n)
