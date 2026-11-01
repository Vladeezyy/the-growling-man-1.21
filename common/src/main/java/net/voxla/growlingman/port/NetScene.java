package net.voxla.growlingman.port;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.Entity;
import net.voxla.growlingman.GrowlingManMod;
import net.voxla.growlingman.entity.GrowlingManEntity;
import net.voxla.growlingman.network.GrowlingManModVariables;
import net.voxla.growlingman.port.bus.ClientTickEvent;
import net.voxla.growlingman.port.bus.Dist;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.voxla.growlingman.port.bus.SubscribeEvent;

/**
 * Multiplayer check (tools/nettest.sh), inert unless {@code growling_man.netscene} is set: the client joins the dev
 * server, plays a survival night marked with the inventory open and then with a Growling Man next to it for about a minute,
 * logs what it sees ({@code [scene] CHECK / DONE}) and quits. The server log is checked for errors by the script.
 */
@EventBusSubscriber(Dist.CLIENT)
public final class NetScene {
	private static final boolean ENABLED = System.getProperty("growling_man.netscene") != null;
	private static final int END = 1500;
	private static int tick, checks, failures, seen;
	private static double marked;

	private static void check(String name, boolean ok, Object detail) {
		checks++;
		if (!ok)
			failures++;
		GrowlingManMod.LOGGER.info("[scene] CHECK " + (ok ? "OK " : "FAIL ") + name + (ok ? "" : " (" + detail + ")"));
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		if (!ENABLED)
			return;
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.getConnection() == null || mc.getSingleplayerServer() != null)
			return;
		mc.options.pauseOnLostFocus = false;
		if (mc.gui.screen() instanceof DeathScreen)
			mc.player.respawn();
		tick++;
		if (tick > 40 && tick <= 300)
			marked = Math.max(marked, GrowlingManModVariables.of(mc.player).IsMarked);
		switch (tick) {
			case 20 -> {
				// NeoForge negotiates the mod's channels when joining; a payload not registered on both sides cannot be sent
				try {
					var has = Class.forName("net.neoforged.neoforge.network.registration.NetworkRegistry").getMethod("hasChannel", net.minecraft.network.Connection.class,
							net.minecraft.network.ConnectionProtocol.class, net.minecraft.resources.Identifier.class);
					java.util.List<String> missing = new java.util.ArrayList<>();
					for (String id : new String[] {"inventory_screen", "key_clear_mind", "player_variables_sync", "saved_data_sync"})
						if (!(Boolean) has.invoke(null, mc.getConnection().getConnection(), null, net.minecraft.resources.Identifier.fromNamespaceAndPath(GrowlingManMod.MODID, id)))
							missing.add(id);
					check("every payload channel negotiated (NeoForge)", missing.isEmpty(), missing);
				} catch (ClassNotFoundException e) {
					// Fabric: no negotiation
				} catch (ReflectiveOperationException e) {
					check("channel check ran", false, e);
				}
			}
			case 40 -> {
				for (String command : new String[] {"gamemode survival", "time set 18000", "gamerule doDaylightCycle false", "growling_man_dev set_mark 1",
						"growling_man_dev set_spawn_timer 30", "growling_man_dev show_dev_overlay true"})
					mc.player.connection.sendCommand(command);
			}
			case 80 -> mc.gui.setScreen(new InventoryScreen(mc.player));
			// the mark of the commands, seen before the night's mobs or the Growling Man end it (as in 1.0.1)
			case 300 -> check("player variables synced (marked)", marked > 0, marked);
			case 400 -> {
				if (mc.gui.screen() instanceof InventoryScreen)
					mc.gui.setScreen(null);
				mc.player.connection.sendCommand("summon growling_man:growling_man ^ ^ ^8");
			}
			case END -> {
				check("still connected after " + END / 20 + " s on the server", mc.getConnection() != null && mc.player != null, "disconnected");
				check("a Growling Man was seen on the client", seen > 0, seen);
				GrowlingManMod.LOGGER.info("[scene] DONE " + (checks - failures) + "/" + checks + (failures == 0 ? " passed" : " (" + failures + " failed)"));
				mc.level.disconnect(net.minecraft.network.chat.Component.empty());
				mc.stop();
			}
			default -> {
			}
		}
		if (tick % 100 == 0 && tick < END) {
			int count = 0;
			for (Entity entity : mc.level.entitiesForRendering())
				if (entity instanceof GrowlingManEntity)
					count++;
			seen += count;
			GrowlingManModVariables.PlayerVariables vars = GrowlingManModVariables.of(mc.player);
			GrowlingManMod.LOGGER.info("[scene] t={} growling men {} health {} marked {} screen {} cym {} mtl {} inv {}", tick, count, mc.player.getHealth(), vars.IsMarked,
					mc.gui.screen() == null ? "-" : mc.gui.screen().getClass().getSimpleName(), vars.cym_overlay_opacity, vars.mtl_overlay_opacity, vars.gm_inv_opacity);
		}
	}

	private NetScene() {
	}
}
