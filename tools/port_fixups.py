"""Growling Man specific rewrites after port_java.py / port_multiloader.py (idempotent): decompiled 1.20.1 shapes the
shared rules don't know.
"""
import re
import sys
from pathlib import Path

ROOT = Path(sys.argv[1] if len(sys.argv) > 1 else 'common/src/main/java/net/voxla/growlingman')


def rules(s):
    # copying block state properties: Vineflower's iterator loop -> for-each with the 1.21 generic setValue
    s = re.sub(r'UnmodifiableIterator (\w+) = (\w+)\.getValues\(\)\.entrySet\(\)\.iterator\(\);\n\s*\n?(\t+)while \(\1\.hasNext\(\)\) \{\n\t+'
               r'Entry<Property<\?>, Comparable<\?>> entry = \(Entry<Property<\?>, Comparable<\?>>\)\1\.next\(\);',
               r'for (Entry<Property<?>, Comparable<?>> entry : \2.getValues().entrySet()) {', s)
    s = s.replace('.setValue(_property, entry.getValue())', '.setValue(_property, (Comparable)entry.getValue())')
    s = s.replace('import com.google.common.collect.UnmodifiableIterator;\n', '')
    # block entity data across setBlock: 1.21 needs the registries
    s = re.sub(r'\.saveWithFullMetadata\(\)', '.saveWithFullMetadata(world.registryAccess())', s)
    s = re.sub(r'(\w+)\.load\(_bnbt\)', r'\1.loadWithComponents(_bnbt, world.registryAccess())', s)
    # step height is an attribute (1.20.5)
    s = s.replace('entity.getStepHeight()', 'entity.maxUpStep()')
    s = re.sub(r'entity\.getAttribute\(Attributes\.STEP_HEIGHT\)\.setBaseValue\(([\d.]+)F\);',
               r'if (entity instanceof LivingEntity _stepEntity) {\n\t\t\t\t\t_stepEntity.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(\1);\n\t\t\t\t}', s)
    # cancelling the event (MCreator 1.21: ICancellableEvent)
    s = re.sub(r'&& event != null\s*&& event\.isCancelable\(\)\) \{\s*event\.setCanceled\(true\);',
               '&& event instanceof ICancellableEvent _cancellable) {\n\t\t\t\t_cancellable.setCanceled(true);', s)
    if 'ICancellableEvent _cancellable' in s and 'import net.voxla.growlingman.port.bus.ICancellableEvent;' not in s:
        s = s.replace('import net.voxla.growlingman.port.bus.Event;', 'import net.voxla.growlingman.port.bus.Event;\nimport net.voxla.growlingman.port.bus.ICancellableEvent;', 1)
    # commands: Forge's getUnsidedLevel
    s = s.replace('((CommandSourceStack)arguments.getSource()).getUnsidedLevel()', 'arguments.getSource().getLevel()')
    # the client bus is the only game bus here
    s = re.sub(r'@EventBusSubscriber\(value = Dist\.CLIENT, bus = EventBusSubscriber\.Bus\.FORGE\)', '@EventBusSubscriber(Dist.CLIENT)', s)
    # LivingTickEvent -> EntityTickEvent.Pre (NeoForge 1.20.5)
    s = s.replace('import net.voxla.growlingman.port.bus.LivingEvent;', 'import net.voxla.growlingman.port.bus.EntityTickEvent;')
    s = s.replace('LivingEvent.LivingTickEvent event', 'EntityTickEvent.Pre event')
    return s


def entity(s):
    s = s.replace('import net.minecraft.world.entity.MobType;\n', '')
    s = s.replace('import net.minecraftforge.network.NetworkHooks;\n', '').replace('import net.minecraftforge.network.PlayMessages;\n', '')
    s = s.replace('public EntityDimensions getDimensions(Pose', 'public EntityDimensions getDefaultDimensions(Pose')
    s = s.replace('super.getDimensions(p_33597_)', 'super.getDefaultDimensions(p_33597_)')
    return s


changed = 0
for f in ROOT.rglob('*.java'):
    s = f.read_text()
    n = rules(s)
    if f.name == 'GrowlingManEntity.java':
        n = entity(n)
    if n != s:
        f.write_text(n)
        changed += 1
print('fixups: changed', changed)
