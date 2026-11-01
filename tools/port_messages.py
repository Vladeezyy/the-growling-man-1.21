"""Forge SimpleChannel GUI button/slot messages (MCreator) -> NeoForge 1.21 CustomPacketPayload records."""
import re, sys
sys.path.insert(0, __file__.rsplit('/', 1)[0])
from port_java import match_paren


def snake(name):
    return re.sub(r'(?<!^)(?=[A-Z])', '_', name).lower()


HEAD = '''package net.voxla.growlingman.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.BlockPos;

import java.util.HashMap;

'''

for p in sys.argv[1:]:
    s = open(p, encoding='utf-8').read()
    cls = re.search(r'public class (\w+)', s).group(1)
    own = [l for l in s.split('\n') if l.startswith('import net.mcreator.')]
    slot = 'handleSlotAction' in s
    m = re.search(r'\n\tpublic static void (handleSlotAction|handleButtonAction)\(', s)
    e = match_paren(s, s.index('{', m.end()))
    action = s[m.start():e + 1]
    fields = ['slotID', 'x', 'y', 'z', 'changeType', 'meta'] if slot else ['buttonID', 'x', 'y', 'z']
    key = snake(cls.replace('Message', '')).replace('_g_u_i', '_gui')
    out = HEAD + '\n'.join(own) + '\n\n'
    out += '@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)\n'
    out += 'public record %s(%s) implements CustomPacketPayload {\n' % (cls, ', '.join('int ' + f for f in fields))
    out += '\tpublic static final Type<%s> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GrowlingManMod.MODID, "%s"));\n' % (cls, key)
    out += '\tpublic static final StreamCodec<RegistryFriendlyByteBuf, %s> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, %s message) -> {\n' % (cls, cls)
    out += ''.join('\t\tbuffer.writeInt(message.%s);\n' % f for f in fields)
    out += '\t}, (RegistryFriendlyByteBuf buffer) -> new %s(%s));\n\n' % (cls, ', '.join(['buffer.readInt()'] * len(fields)))
    out += '\t@Override\n\tpublic Type<%s> type() {\n\t\treturn TYPE;\n\t}\n\n' % cls
    out += '\tpublic static void handleData(final %s message, final IPayloadContext context) {\n' % cls
    out += '\t\tif (context.flow() == PacketFlow.SERVERBOUND) {\n\t\t\tcontext.enqueueWork(() -> {\n'
    if slot:
        out += '\t\t\t\thandleSlotAction(context.player(), message.slotID, message.changeType, message.meta, message.x, message.y, message.z);\n'
    else:
        out += '\t\t\t\thandleButtonAction(context.player(), message.buttonID, message.x, message.y, message.z);\n'
    out += '\t\t\t}).exceptionally(e -> {\n\t\t\t\tcontext.connection().disconnect(Component.literal(e.getMessage()));\n\t\t\t\treturn null;\n\t\t\t});\n\t\t}\n\t}\n'
    out += action + '\n\n'
    out += '\t@SubscribeEvent\n\tpublic static void registerMessage(FMLCommonSetupEvent event) {\n'
    out += '\t\tGrowlingManMod.addNetworkMessage(%s.TYPE, %s.STREAM_CODEC, %s::handleData);\n\t}\n}\n' % (cls, cls, cls)
    open(p, 'w', encoding='utf-8').write(out)
    print('ported', cls, key)
