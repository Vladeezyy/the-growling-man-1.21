package net.voxla.growlingman.init;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.voxla.growlingman.GrowlingManMod;
import net.voxla.growlingman.network.ClearMindKeyMessage;

@EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)
public class GrowlingManModKeyMappings {
   public static final KeyMapping CLEAR_MIND_KEY = new KeyMapping("key.growling_man.clear_mind_key", 88, "key.categories.misc") {
      private boolean isDownOld = false;

      public void setDown(boolean isDown) {
         super.setDown(isDown);
         if (this.isDownOld != isDown && isDown) {
            GrowlingManMod.PACKET_HANDLER.sendToServer(new ClearMindKeyMessage(0, 0));
            ClearMindKeyMessage.pressAction(Minecraft.getInstance().player, 0, 0);
            GrowlingManModKeyMappings.CLEAR_MIND_KEY_LASTPRESS = System.currentTimeMillis();
         } else if (this.isDownOld != isDown && !isDown) {
            int dt = (int)(System.currentTimeMillis() - GrowlingManModKeyMappings.CLEAR_MIND_KEY_LASTPRESS);
            GrowlingManMod.PACKET_HANDLER.sendToServer(new ClearMindKeyMessage(1, dt));
            ClearMindKeyMessage.pressAction(Minecraft.getInstance().player, 1, dt);
         }

         this.isDownOld = isDown;
      }
   };
   private static long CLEAR_MIND_KEY_LASTPRESS = 0L;

   @SubscribeEvent
   public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
      event.register(CLEAR_MIND_KEY);
   }

   @EventBusSubscriber(Dist.CLIENT)
   public static class KeyEventListener {
      @SubscribeEvent
      public static void onClientTick(ClientTickEvent event) {
         if (Minecraft.getInstance().screen == null) {
            GrowlingManModKeyMappings.CLEAR_MIND_KEY.consumeClick();
         }
      }
   }
}
