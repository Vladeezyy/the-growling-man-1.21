package net.voxla.growlingman.init;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.voxla.growlingman.port.bus.Dist;
import net.voxla.growlingman.port.bus.ClientTickEvent;
import net.voxla.growlingman.port.bus.RegisterKeyMappingsEvent;
import net.voxla.growlingman.port.platform.Platform;
import net.voxla.growlingman.port.bus.SubscribeEvent;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.voxla.growlingman.GrowlingManMod;
import net.voxla.growlingman.network.ClearMindKeyMessage;

@EventBusSubscriber(value = Dist.CLIENT)
public class GrowlingManModKeyMappings {
	public static final KeyMapping CLEAR_MIND_KEY = new KeyMapping("key.growling_man.clear_mind_key", com.mojang.blaze3d.platform.InputConstants.KEY_X, KeyMapping.Category.MISC) {
		private boolean isDownOld = false;

		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (this.isDownOld != isDown && isDown) {
				Platform.INSTANCE.sendToServer(new ClearMindKeyMessage(0, 0));
				ClearMindKeyMessage.pressAction(Minecraft.getInstance().player, 0, 0);
				GrowlingManModKeyMappings.CLEAR_MIND_KEY_LASTPRESS = System.currentTimeMillis();
			} else if (this.isDownOld != isDown && !isDown) {
				int dt = (int)(System.currentTimeMillis() - GrowlingManModKeyMappings.CLEAR_MIND_KEY_LASTPRESS);
				Platform.INSTANCE.sendToServer(new ClearMindKeyMessage(1, dt));
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
		public static void onClientTick(ClientTickEvent.Post event) {
			if (Minecraft.getInstance().gui.screen() == null) {
				GrowlingManModKeyMappings.CLEAR_MIND_KEY.consumeClick();
			}
		}
	}
}
