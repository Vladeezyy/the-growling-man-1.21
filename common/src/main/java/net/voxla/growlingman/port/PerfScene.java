package net.voxla.growlingman.port;

import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.voxla.growlingman.GrowlingManMod;
import net.voxla.growlingman.entity.GrowlingManEntity;
import net.voxla.growlingman.init.GrowlingManModEntities;
import net.voxla.growlingman.network.GrowlingManModVariables;
import net.voxla.growlingman.port.bus.ClientTickEvent;
import net.voxla.growlingman.port.bus.Dist;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.voxla.growlingman.port.bus.GMBus;
import net.voxla.growlingman.port.bus.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * Load check ({@code ./gradlew runPerf}, tools/perf.sh), inert unless {@code growling_man.perf} is set: a new survival
 * world at night, the player marked, a Growling Man hunting nearby and 40 cows around. Windows of 200 ticks alternate
 * the mod's listeners on and off (GMBus.muted, the baseline in the same world); each window logs the server tick time
 * (MSPT), the FPS, the sync calls per second and the time of the mod's listeners per event and thread
 * ({@code [perf] ...}). Then the same without the Growling Man. Quits at the end.
 */
@EventBusSubscriber(value = Dist.CLIENT)
public final class PerfScene {
	private static final boolean ENABLED = System.getProperty("growling_man.perf") != null;
	private static final int WINDOW = 200;
	private record Step(int at, Consumer<Minecraft> action) {
	}
	private static final List<Step> STEPS = new ArrayList<>();
	private static boolean opened;
	private static int tick, windowStart = -1, serverTicks, fpsSamples, syncsAtStart;
	private static long mspt, fps;
	private static String window;
	private static final Map<String, double[]> RESULTS = new TreeMap<>();

	private static void at(int t, Consumer<Minecraft> action) {
		STEPS.add(new Step(t, action));
	}

	private static void server(Minecraft mc, Consumer<ServerPlayer> action) {
		var server = mc.getSingleplayerServer();
		server.execute(() -> {
			try {
				action.accept(server.getPlayerList().getPlayer(mc.player.getUUID()));
			} catch (RuntimeException e) {
				GrowlingManMod.LOGGER.error("[perf] server step failed", e);
			}
		});
	}

	private static void begin(String name, boolean mod) {
		GMBus.muted = !mod;
		GMBus.profiling = mod;
		GMBus.PROFILE.clear();
		window = name + (mod ? " / mod" : " / base");
		windowStart = tick;
		mspt = fps = 0;
		serverTicks = fpsSamples = 0;
		syncsAtStart = GrowlingManModVariables.syncs;
	}

	private static void end() {
		double seconds = WINDOW / 20.0;
		double ms = mspt / (double) Math.max(1, serverTicks) / 1e6;
		double f = fps / (double) Math.max(1, fpsSamples);
		double syncs = (GrowlingManModVariables.syncs - syncsAtStart) / seconds;
		StringBuilder listeners = new StringBuilder();
		double serverUs = 0, clientUs = 0;
		for (var e : new TreeMap<>(GMBus.PROFILE).entrySet()) {
			double us = e.getValue()[0] / 1e3 / WINDOW;
			if (e.getKey().startsWith("server"))
				serverUs += us;
			else
				clientUs += us;
			listeners.append(String.format(" | %s %.1f us/tick", e.getKey(), us));
		}
		GrowlingManMod.LOGGER.info(String.format("[perf] %s: MSPT %.2f, FPS %.0f, syncs %.1f/s, listeners server %.1f us/tick client %.1f us/tick%s", window, ms, f, syncs,
				serverUs, clientUs, listeners));
		double[] r = RESULTS.computeIfAbsent(window, k -> new double[6]);
		r[0] += ms;
		r[1] += f;
		r[2] += syncs;
		r[3] += serverUs;
		r[4] += clientUs;
		r[5]++;
		windowStart = -1;
		GMBus.muted = false;
		GMBus.profiling = false;
	}

	private static void windows(int from, String name) {
		int t = from;
		for (int i = 0; i < 4; i++) {
			boolean mod = i % 2 == 0;
			at(t, mc -> begin(name, mod));
			at(t + WINDOW, mc -> end());
			t += WINDOW + 20;
		}
	}

	static {
		int t = 40;
		at(t, mc -> server(mc, p -> {
			ServerLevel level = p.level();
			level.getGameRules().set(GameRules.ADVANCE_TIME, false, level.getServer());
			level.getGameRules().set(GameRules.ADVANCE_WEATHER, false, level.getServer());
			level.getGameRules().set(GameRules.SPAWN_MOBS, false, level.getServer());
			level.setDayTime(18000);
			p.setGameMode(GameType.SURVIVAL);
			p.setInvulnerable(true);
			BlockPos spawn = level.getRespawnData().pos();
			int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, spawn.getX(), spawn.getZ());
			p.teleportTo(level, spawn.getX() + 0.5, y + 1, spawn.getZ() + 0.5, java.util.Set.of(), 0, 10, true);
			GrowlingManModVariables.PlayerVariables vars = GrowlingManModVariables.of(p);
			vars.IsMarked = 1;
			vars.syncPlayerVariables(p);
			for (int i = 0; i < 40; i++) {
				Entity cow = EntityType.COW.create(level, EntitySpawnReason.COMMAND);
				int x = spawn.getX() + (i % 8) * 3 - 12, z = spawn.getZ() + (i / 8) * 3 + 6;
				cow.snapTo(x + 0.5, level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z), z + 0.5);
				level.addFreshEntity(cow);
			}
			GrowlingManEntity man = GrowlingManModEntities.GROWLING_MAN.get().create(level, EntitySpawnReason.COMMAND);
			man.snapTo(spawn.getX() + 12.5, y + 1, spawn.getZ() + 12.5);
			man.finalizeSpawn(level, level.getCurrentDifficultyAt(spawn), EntitySpawnReason.COMMAND, null);
			level.addFreshEntity(man);
		}));
		// chunks load, the entities settle
		t += 600;
		windows(t, "marked, a Growling Man, 40 cows");
		t += 4 * (WINDOW + 20);
		at(t, mc -> server(mc, p -> {
			for (ServerLevel level : p.level().getServer().getAllLevels())
				for (Entity e : level.getAllEntities())
					if (e instanceof GrowlingManEntity)
						e.discard();
		}));
		windows(t += 40, "marked, no Growling Man, 40 cows");
		t += 4 * (WINDOW + 20);
		at(t, mc -> {
			for (var e : RESULTS.entrySet()) {
				double[] r = e.getValue();
				GrowlingManMod.LOGGER.info(String.format("[perf] AVG %s: MSPT %.2f, FPS %.0f, syncs %.1f/s, listeners server %.1f us/tick client %.1f us/tick", e.getKey(), r[0] / r[5],
						r[1] / r[5], r[2] / r[5], r[3] / r[5], r[4] / r[5]));
			}
			GrowlingManMod.LOGGER.info("[perf] DONE");
			mc.level.disconnect(net.minecraft.network.chat.Component.empty());
			mc.disconnect(new TitleScreen(), false);
			mc.stop();
		});
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		if (!ENABLED)
			return;
		Minecraft mc = Minecraft.getInstance();
		if (!opened && mc.screen instanceof net.minecraft.client.gui.screens.AccessibilityOnboardingScreen) {
			mc.options.onboardAccessibility = false;
			mc.options.save();
			mc.setScreen(new TitleScreen());
			return;
		}
		if (!opened && mc.getOverlay() != null)
			return;
		if (!opened && mc.screen instanceof TitleScreen) {
			opened = true;
			mc.options.cloudStatus().set(CloudStatus.OFF);
			mc.options.pauseOnLostFocus = false;
			mc.options.framerateLimit().set(260);
			mc.options.enableVsync().set(false);
			LevelSettings settings = new LevelSettings("gm_perf", GameType.SURVIVAL, false, Difficulty.NORMAL, true, new GameRules(net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS), WorldDataConfiguration.DEFAULT);
			mc.createWorldOpenFlows().createFreshLevel("gm_perf", settings, new WorldOptions(20261101L, true, false), WorldPresets::createNormalWorldDimensions, new TitleScreen());
			return;
		}
		if (mc.player == null || mc.getSingleplayerServer() == null)
			return;
		mc.options.pauseOnLostFocus = false;
		if (mc.screen instanceof PauseScreen)
			mc.setScreen(null);
		tick++;
		if (windowStart >= 0 && tick > windowStart) {
			long[] times = mc.getSingleplayerServer().getTickTimesNanos();
			// one sample per client tick: the latest server tick
			mspt += times[(mc.getSingleplayerServer().getTickCount() + times.length - 1) % times.length];
			serverTicks++;
			fps += mc.getFps();
			fpsSamples++;
		}
		for (Step step : STEPS)
			if (step.at() == tick)
				try {
					step.action().accept(mc);
				} catch (RuntimeException e) {
					GrowlingManMod.LOGGER.error("[perf] step at tick {} failed", tick, e);
				}
	}

	private PerfScene() {
	}
}
