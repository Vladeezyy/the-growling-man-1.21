package net.voxla.growlingman.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent.Context;
import net.voxla.growlingman.GrowlingManMod;
import net.voxla.growlingman.procedures.HoldBreathKeyOnKeyPressedProcedure;
import net.voxla.growlingman.procedures.HoldBreathKeyOnKeyReleasedProcedure;

@EventBusSubscriber(bus = Bus.MOD)
public class ClearMindKeyMessage {
   int type;
   int pressedms;

   public ClearMindKeyMessage(int type, int pressedms) {
      this.type = type;
      this.pressedms = pressedms;
   }

   public ClearMindKeyMessage(FriendlyByteBuf buffer) {
      this.type = buffer.readInt();
      this.pressedms = buffer.readInt();
   }

   public static void buffer(ClearMindKeyMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.type);
      buffer.writeInt(message.pressedms);
   }

   public static void handler(ClearMindKeyMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> pressAction(context.getSender(), message.type, message.pressedms));
      context.setPacketHandled(true);
   }

   public static void pressAction(Player entity, int type, int pressedms) {
      Level world = entity.level();
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();
      if (world.hasChunkAt(entity.blockPosition())) {
         if (type == 0) {
            HoldBreathKeyOnKeyPressedProcedure.execute(entity);
         }

         if (type == 1) {
            HoldBreathKeyOnKeyReleasedProcedure.execute(entity);
         }
      }
   }

   @SubscribeEvent
   public static void registerMessage(FMLCommonSetupEvent event) {
      GrowlingManMod.addNetworkMessage(ClearMindKeyMessage.class, ClearMindKeyMessage::buffer, ClearMindKeyMessage::new, ClearMindKeyMessage::handler);
   }
}
