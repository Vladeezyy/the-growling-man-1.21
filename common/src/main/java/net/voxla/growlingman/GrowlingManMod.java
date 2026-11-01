package net.voxla.growlingman;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.voxla.growlingman.port.Tuple;
import net.voxla.growlingman.init.GrowlingManModBlockEntities;
import net.voxla.growlingman.init.GrowlingManModBlocks;
import net.voxla.growlingman.init.GrowlingManModEntities;
import net.voxla.growlingman.init.GrowlingManModItems;
import net.voxla.growlingman.init.GrowlingManModMobEffects;
import net.voxla.growlingman.init.GrowlingManModSounds;
import net.voxla.growlingman.network.ClearMindKeyMessage;
import net.voxla.growlingman.network.InventoryScreenMessage;
import net.voxla.growlingman.network.GrowlingManModVariables;
import net.voxla.growlingman.port.bus.EventIndex;
import net.voxla.growlingman.port.bus.EventPriority;
import net.voxla.growlingman.port.bus.GMBus;
import net.voxla.growlingman.port.bus.ServerTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** The mod's common init; the NeoForge and Fabric entry points call {@link #init()} first */
public class GrowlingManMod {
	public static final Logger LOGGER = LogManager.getLogger(GrowlingManMod.class);
	public static final String MODID = "growling_man";
	private static boolean initialized;
	private static final Collection<Tuple<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public static void init() {
		if (initialized) {
			return;
		}
		initialized = true;
		// the registry classes queue their entries (Registrar) when loaded, in MCreator's order
		GrowlingManModSounds.REGISTRY.getRegistryKey();
		GrowlingManModBlocks.REGISTRY.getRegistryKey();
		GrowlingManModBlockEntities.REGISTRY.getRegistryKey();
		GrowlingManModItems.REGISTRY.getRegistryKey();
		GrowlingManModEntities.REGISTRY.getRegistryKey();
		GrowlingManModMobEffects.REGISTRY.getRegistryKey();
		GrowlingManModVariables.registerPackets();
		ClearMindKeyMessage.register();
		InventoryScreenMessage.register();
		EventIndex.register();
		GMBus.add(ServerTickEvent.Post.class, EventPriority.NORMAL, GrowlingManMod::tick);
	}

	/** Runs {@code action} after {@code tick} server ticks (only when called on the server thread, as in MCreator) */
	public static void queueServerWork(int tick, Runnable action) {
		if (Thread.currentThread().getName().equals("Server thread")) {
			workQueue.add(new Tuple<>(action, tick));
		}
	}

	private static void tick(ServerTickEvent.Post event) {
		List<Tuple<Runnable, Integer>> actions = new ArrayList<>();
		workQueue.forEach(work -> {
			work.setB(work.getB() - 1);
			if (work.getB() == 0) {
				actions.add(work);
			}
		});
		actions.forEach(e -> e.getA().run());
		workQueue.removeAll(actions);
	}
}
