#!/usr/bin/env python3
"""NeoForge 1.21.1 -> 1.21.11: the mechanical rules for this mod's (MCreator) Java code. Idempotent, run from the repo
root: python tools/port_1_21_11.py. Imports of classes that only moved package are fixed afterwards by
modding-kb/scripts/fix_moved_imports.py against the 1.21.11 sources."""
import glob
import re

ROOT = 'common/src/main/java/net/voxla/growlingman/'

# word -> word everywhere (imports included)
RENAMES = [
    (r'\bnet\.minecraft\.resources\.ResourceLocation\b', 'net.minecraft.resources.Identifier'),
    (r'\bResourceLocation\b', 'Identifier'),
    (r'\bnet\.minecraft\.world\.entity\.MobSpawnType\b', 'net.minecraft.world.entity.EntitySpawnReason'),
    (r'\bMobSpawnType\b', 'EntitySpawnReason'),
    (r'\bDirectionProperty\b', 'EnumProperty<Direction>'),
    (r'import net\.minecraft\.world\.level\.block\.state\.properties\.EnumProperty<Direction>;',
     'import net.minecraft.world.level.block.state.properties.EnumProperty;'),
    (r'\bisInWaterOrBubble\(\)', 'isInWater()'),
    (r'\.isDay\(\)', '.isBrightOutside()'),
    # NeoForge 21.6+: the bus of an @EventBusSubscriber follows from the event type
    (r'@EventBusSubscriber\(bus = EventBusSubscriber\.Bus\.MOD\)', '@EventBusSubscriber'),
    (r'@EventBusSubscriber\(bus = EventBusSubscriber\.Bus\.MOD, ', '@EventBusSubscriber('),
    (r'\bDirection\.getNearest\(', 'Direction.getApproximateNearest('),
]

# MobEffects fields renamed in 1.21.2
EFFECTS = {
    'MOVEMENT_SLOWDOWN': 'SLOWNESS', 'MOVEMENT_SPEED': 'SPEED', 'DAMAGE_BOOST': 'STRENGTH',
    'DAMAGE_RESISTANCE': 'RESISTANCE', 'DIG_SPEED': 'HASTE', 'DIG_SLOWDOWN': 'MINING_FATIGUE',
    'CONFUSION': 'NAUSEA', 'HEAL': 'INSTANT_HEALTH', 'HARM': 'INSTANT_DAMAGE', 'JUMP': 'JUMP_BOOST',
}

# CompoundTag getters return Optional since 1.21.5: the getXOr(key, default) forms keep the old defaults
NBT_DEFAULTS = {'getDouble': '0', 'getInt': '0', 'getFloat': '0', 'getLong': '0', 'getShort': '(short) 0',
                'getByte': '(byte) 0', 'getBoolean': 'false', 'getString': '""'}


def nbt_getters(s):
    # only calls with one string argument: the command argument getters take (context, "name")
    for name, default in NBT_DEFAULTS.items():
        s = re.sub(r'(getPersistentData\(\)\.)' + name + r'\((\w+)\)', lambda m: m.group(1) + name + 'Or(' + m.group(2) + ', ' + default + ')', s)
        s = re.sub(r'\.' + name + r'\(("(?:[^"\\]|\\.)*"|[A-Za-z_][\w.]*\s*\+\s*"[^"]*"|"[^"]*"\s*\+\s*[A-Za-z_][\w.()]*)\)',
                   lambda m: '.' + name + 'Or(' + m.group(1) + ', ' + default + ')', s)
    return s


def add_import(s, imp):
    if ('import ' + imp + ';') in s:
        return s
    return re.sub(r'^(package [\w.]+;\n)', r'\1\nimport ' + imp + ';', s, count=1, flags=re.M)


def hud(s):
    """MCreator overlays set the GL state by hand around their blits; since 1.21.6 the GUI is drawn later in batches
    and blit takes the render pipeline (GUI_TEXTURED: blended, no depth), so the state calls are dropped."""
    if 'RenderSystem.' not in s or ('RenderGuiEvent' not in s and 'ScreenEvent' not in s):
        return s
    s = re.sub(r'\n\t*RenderSystem\.[^\n]*;', '', s)
    s = re.sub(r'import com\.mojang\.blaze3d\.(systems\.RenderSystem|opengl\.GlStateManager|platform\.GlStateManager);\n', '', s)
    s = re.sub(r'import net\.minecraft\.client\.renderer\.GameRenderer;\n', '', s)
    s = s.replace('getGuiGraphics().blit(Identifier', 'getGuiGraphics().blit(RenderPipelines.GUI_TEXTURED, Identifier')
    if 'RenderPipelines.' in s:
        s = add_import(s, 'net.minecraft.client.renderer.RenderPipelines')
    return s


def entity(s):
    """1.21.2: hurt is split into the server's hurtServer (with the level) and the client's hurtClient; the MCreator
    damage immunities and is-hurt procedures run on the server. dropExperience takes the server level."""
    if re.search(r'public boolean hurt\(DamageSource (\w+), float (\w+)\)', s):
        s = re.sub(r'public boolean hurt\(DamageSource (\w+), float (\w+)\)', r'public boolean hurtServer(ServerLevel level, DamageSource \1, float \2)', s)
        s = re.sub(r'super\.hurt\((\w+), (\w+)\)', r'super.hurtServer(level, \1, \2)', s)
        s = add_import(s, 'net.minecraft.server.level.ServerLevel')
    s = re.sub(r'public void awardKillScore\(Entity (\w+), int \w+, DamageSource (\w+)\) \{\n\t\tsuper\.awardKillScore\(\w+, \w+, \w+\);',
               r'public void awardKillScore(Entity \1, DamageSource \2) {\n\t\tsuper.awardKillScore(\1, \2);', s)
    s = re.sub(r'InteractionResult\.sidedSuccess\([^;]*?\)(?=;)', 'InteractionResult.SUCCESS', s)
    # goals tick on the server
    s = re.sub(r'(\w+)\.this\.doHurtTarget\((\w+)\)', r'\1.this.doHurtTarget((ServerLevel) \1.this.level(), \2)', s)
    if 'doHurtTarget((ServerLevel)' in s:
        s = add_import(s, 'net.minecraft.server.level.ServerLevel')
    if 'this.dropExperience(null);' in s:
        s = s.replace('this.dropExperience(null);', 'if (this.level() instanceof ServerLevel _serverLevel)\n\t\t\t\tthis.dropExperience(_serverLevel, null);')
        s = add_import(s, 'net.minecraft.server.level.ServerLevel')
    return s


def block_entity(s):
    """1.21.6: block entities save to ValueOutput / load from ValueInput. The shattered glass item handler held 0
    slots; its capability is not registered any more (the transfer API of NeoForge 21.9+ replaced IItemHandler)."""
    if 'extends RandomizableContainerBlockEntity' not in s or 'ValueInput' in s:
        return s
    s = s.replace('public void loadAdditional(CompoundTag compound, HolderLookup.Provider lookupProvider) {\n\t\tsuper.loadAdditional(compound, lookupProvider);',
                  'public void loadAdditional(ValueInput compound) {\n\t\tsuper.loadAdditional(compound);')
    s = s.replace('ContainerHelper.loadAllItems(compound, this.stacks, lookupProvider);', 'ContainerHelper.loadAllItems(compound, this.stacks);')
    s = s.replace('public void saveAdditional(CompoundTag compound, HolderLookup.Provider lookupProvider) {\n\t\tsuper.saveAdditional(compound, lookupProvider);',
                  'public void saveAdditional(ValueOutput compound) {\n\t\tsuper.saveAdditional(compound);')
    s = s.replace('ContainerHelper.saveAllItems(compound, this.stacks, lookupProvider);', 'ContainerHelper.saveAllItems(compound, this.stacks);')
    s = s.replace('\tprivate final SidedInvWrapper handler = new SidedInvWrapper(this, null);\n', '')
    s = re.sub(r'\n\n\tpublic SidedInvWrapper getItemHandler\(\) \{\n\t\treturn handler;\n\t\}', '', s)
    s = s.replace('import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;\n', '')
    s = add_import(s, 'net.minecraft.world.level.storage.ValueInput')
    s = add_import(s, 'net.minecraft.world.level.storage.ValueOutput')
    return s


def blocks(s):
    """1.21.2: a block gets its properties (with its id) from the registration; several Block methods lost their
    level / pos parameters."""
    if not re.search(r'public class \w+ extends (Block|DoorBlock)\b', s):
        return s
    s = re.sub(r'public (\w+Block)\(\) \{\n(\t\tsuper\([^;]*?)BlockBehaviour\.Properties\.of\(\)',
               r'public \1(BlockBehaviour.Properties properties) {\n\2properties', s)
    s = s.replace('.noCollission()', '.noCollision()')
    s = s.replace('public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos)', 'public boolean propagatesSkylightDown(BlockState state)')
    s = s.replace('public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos)', 'public int getLightBlock(BlockState state)')
    s = s.replace('public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader world, BlockPos pos, Player player)',
                  'public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData, Player player)')
    s = s.replace('public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {\n\t\tboolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);',
                  'public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, ItemStack toolStack, boolean willHarvest, FluidState fluid) {\n\t\tboolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, toolStack, willHarvest, fluid);')
    s = s.replace('public void wasExploded(Level world, BlockPos pos, Explosion e)', 'public void wasExploded(ServerLevel world, BlockPos pos, Explosion e)')
    if 'wasExploded(ServerLevel' in s:
        s = add_import(s, 'net.minecraft.server.level.ServerLevel')
    return s


def effects(s):
    """1.21.2: effects tick on the server only, with its level (the MCreator procedures already split client and
    server work themselves). NeoForge 21.6+: effect texts are drawn on any container screen."""
    if 'extends MobEffect' not in s:
        return s
    s = s.replace('public boolean applyEffectTick(LivingEntity entity, int amplifier)', 'public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier)')
    s = s.replace('super.applyEffectTick(entity, amplifier)', 'super.applyEffectTick(level, entity, amplifier)')
    s = s.replace('EffectRenderingInventoryScreen<?>', 'AbstractContainerScreen<?>')
    s = s.replace('import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;', 'import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;')
    if 'ServerLevel level' in s:
        s = add_import(s, 'net.minecraft.server.level.ServerLevel')
    return s


def procedures(s):
    # block entity data copied over setBlock (MCreator "set block, keep NBT")
    if '_be.loadWithComponents(_bnbt, world.registryAccess());' in s:
        s = s.replace('_be.loadWithComponents(_bnbt, world.registryAccess());',
                      '_be.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, world.registryAccess(), _bnbt));')
        s = add_import(s, 'net.minecraft.world.level.storage.TagValueInput')
        s = add_import(s, 'net.minecraft.util.ProblemReporter')
    # DirectionProperty is an EnumProperty<Direction> since 1.21.2; a pattern with the type argument does not compile
    s = s.replace('instanceof EnumProperty<Direction> _dp', 'instanceof EnumProperty _dp')
    return s


def port(s):
    for a, b in RENAMES:
        s = re.sub(a, b, s)
    s = block_entity(s)
    s = blocks(s)
    s = effects(s)
    s = procedures(s)
    # 1.21.2: Registry.get(id) returns Optional<Reference<T>>; getValue(id) is the old lookup
    s = re.sub(r'\b(BuiltInRegistries\.[A-Z_]+)\.get\((Identifier\.)', r'\1.getValue(\2', s)
    s = hud(s)
    s = entity(s)
    s = re.sub(r'import org\.checkerframework\.checker\.units\.qual\.\w+;\n', '', s)
    for a, b in EFFECTS.items():
        s = re.sub(r'\bMobEffects\.' + a + r'\b', 'MobEffects.' + b, s)
    s = nbt_getters(s)
    # CommandSourceStack(..., level, 4, "", ...) : the permission level is a PermissionSet
    if 'new CommandSourceStack(' in s:
        s = re.sub(r'(new CommandSourceStack\([^;]*?, )4(, "")', r'\1LevelBasedPermissionSet.OWNER\2', s)
        if 'LevelBasedPermissionSet.OWNER' in s:
            s = add_import(s, 'net.minecraft.server.permissions.LevelBasedPermissionSet')
    # commands: permission levels are permission sets since 1.21.11
    if '.requires(s -> s.hasPermission(4))' in s:
        s = s.replace('.requires(s -> s.hasPermission(4))', '.requires(Commands.hasPermission(Commands.LEVEL_OWNERS))')
    if 'EnumProperty<Direction>' in s:
        s = add_import(s, 'net.minecraft.world.level.block.state.properties.EnumProperty')
        s = add_import(s, 'net.minecraft.core.Direction')
    if 'EntitySpawnReason' in s:
        s = add_import(s, 'net.minecraft.world.entity.EntitySpawnReason')
    return s


def main():
    changed = 0
    for path in glob.glob(ROOT + '**/*.java', recursive=True):
        s = open(path, encoding='utf-8').read()
        o = port(s)
        if o != s:
            open(path, 'w', encoding='utf-8', newline='\n').write(o)
            changed += 1
    print('changed', changed)


if __name__ == '__main__':
    main()
