package net.voxla.growlingman.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.voxla.growlingman.network.InventoryScreenMessage;
import net.voxla.growlingman.port.bus.ClientTickEvent;
import net.voxla.growlingman.port.bus.Dist;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.voxla.growlingman.port.bus.SubscribeEvent;
import net.voxla.growlingman.port.platform.Platform;

/** Sends InventoryScreenMessage when the player's inventory screen opens or closes */
@EventBusSubscriber(Dist.CLIENT)
public final class InventoryScreenWatcher {
	private static boolean sent;

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.getConnection() == null) {
			sent = false;
			return;
		}
		boolean open = mc.gui.screen() instanceof InventoryScreen;
		if (open != sent) {
			sent = open;
			Platform.INSTANCE.sendToServer(new InventoryScreenMessage(open));
		}
	}

	private InventoryScreenWatcher() {
	}
}
