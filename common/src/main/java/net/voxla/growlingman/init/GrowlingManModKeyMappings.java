package net.voxla.growlingman.init;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.voxla.growlingman.port.bus.Dist;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.voxla.growlingman.port.bus.SubscribeEvent;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.voxla.growlingman.GrowlingManMod;
import net.voxla.growlingman.network.ClearMindKeyMessage;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class GrowlingManModKeyMappings {
	public static final KeyMapping CLEAR_MIND_KEY = new KeyMapping("key.growling_man.clear_mind_key", 88, "key.categories.misc") {
		private boolean isDownOld = false;

		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (this.isDownOld != isDown && isDown) {
				PacketDistributor.sendToServer(new ClearMindKeyMessage(0, 0));
				ClearMindKeyMessage.pressAction(Minecraft.getInstance().player, 0, 0);
				GrowlingManModKeyMappings.CLEAR_MIND_KEY_LASTPRESS = System.currentTimeMillis();
			} else if (this.isDownOld != isDown && !isDown) {
				int dt = (int)(System.currentTimeMillis() - GrowlingManModKeyMappings.CLEAR_MIND_KEY_LASTPRESS);
				PacketDistributor.sendToServer(new ClearMindKeyMessage(1, dt));
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
		public static void onClientTick(TickEvent.ClientTickEvent event) {
			if (Minecraft.getInstance().screen == null) {
				GrowlingManModKeyMappings.CLEAR_MIND_KEY.consumeClick();
			}
		}
	}
}
