package net.voxla.growlingman.port;

import com.mojang.blaze3d.platform.NativeImage;

import net.voxla.growlingman.port.bus.ClientTickEvent;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.voxla.growlingman.port.bus.SubscribeEvent;
import net.voxla.growlingman.port.bus.Dist;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.Difficulty;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.locale.Language;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;
import net.voxla.growlingman.configuration.GrowlingManConfigConfiguration;
import net.voxla.growlingman.init.GrowlingManModMobEffects;
import net.voxla.growlingman.network.ClearMindKeyMessage;
import net.voxla.growlingman.network.GrowlingManModVariables;
import net.voxla.growlingman.port.platform.Platform;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.Screenshot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.CloudStatus;

import net.voxla.growlingman.GrowlingManMod;
import net.voxla.growlingman.entity.GrowlingManEntity;

import java.util.function.Consumer;
import java.util.List;
import java.util.ArrayList;

/**
 * Port check scene ({@code ./gradlew runScene}), inert unless {@code growling_man.scene} is set: creates a new world,
 * puts the Growling Man in front of the player (AI off), screenshots it, the HUD overlays and the mark in the inventory,
 * sends the clear mind key to the server, checks the screen for missing (magenta) textures and every name for a translation, logs {@code [scene] CHECK / DONE}, quits.
 */
@EventBusSubscriber(value = Dist.CLIENT)
public final class DevScene {
	private static final boolean ENABLED = System.getProperty("growling_man.scene") != null;
	/** {@code -Pscene=poster}: only the README picture (see {@link #poster()}) */
	private static final boolean POSTER = "poster".equals(System.getProperty("growling_man.scene"));
	private static final String WORLD = "gm_scene";
	private record Step(int at, Consumer<Minecraft> action) {
	}
	private static final List<Step> STEPS = new ArrayList<>();
	private static boolean opened;
	private static int tick, checks, failures;
	private static BlockPos stage;
	private static net.minecraft.core.Direction facing;
	private static GrowlingManEntity posed;

	private static void at(int t, Consumer<Minecraft> action) {
		STEPS.add(new Step(t, action));
	}

	private static void check(String name, boolean ok, Object detail) {
		checks++;
		if (!ok)
			failures++;
		GrowlingManMod.LOGGER.info("[scene] CHECK " + (ok ? "OK " : "FAIL ") + name + (ok ? "" : " (" + detail + ")"));
	}

	private static void server(Minecraft mc, Consumer<ServerPlayer> action) {
		var server = mc.getSingleplayerServer();
		server.execute(() -> {
			try {
				action.accept(server.getPlayerList().getPlayer(mc.player.getUUID()));
			} catch (RuntimeException | LinkageError e) {
				check("server step ran", false, e);
				GrowlingManMod.LOGGER.error("[scene] server step failed", e);
			}
		});
	}

	private static void shot(Minecraft mc, String name) {
		Screenshot.grab(mc.gameDirectory, "scene_" + name + ".png", mc.getMainRenderTarget(), c -> GrowlingManMod.LOGGER.info("[scene] " + c.getString()));
	}

	private static List<EntityType<?>> modEntities() {
		List<EntityType<?>> list = new ArrayList<>();
		for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE)
			if (BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace().equals(GrowlingManMod.MODID))
				list.add(type);
		return list;
	}

	static {
		if (POSTER)
			poster();
		else
			checks();
	}

	private static void checks() {
		int t = 40;
		at(t, mc -> server(mc, DevScene::build));
		at(t += 60, mc -> {
			shot(mc, "entities");
			checkNoMissingTexture(mc, "entities");
		});
		at(t += 5, mc -> server(mc, p -> p.teleportTo(p.serverLevel(), stage.getX() + 0.5, stage.getY() + 1.6, stage.getZ() - 3.5, 0f, 10f)));
		at(t += 20, mc -> shot(mc, "entities_close"));
		// the HUD overlays: marked, "clear your mind", mental breakdown
		at(t += 5, mc -> server(mc, p -> {
			GrowlingManModVariables.PlayerVariables vars = GrowlingManModVariables.of(p);
			vars.IsMarked = 1;
			vars.cym_overlay_opacity = 200;
			vars.mtl_overlay_opacity = 200;
			vars.syncPlayerVariables(p);
			p.addEffect(new MobEffectInstance(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder(), 400, 0));
		}));
		at(t += 20, mc -> {
			check("player variables reach the client", GrowlingManModVariables.of(mc.player).IsMarked > 0 && GrowlingManModVariables.of(mc.player).cym_overlay_opacity > 0,
					GrowlingManModVariables.of(mc.player).IsMarked);
			shot(mc, "overlays");
			checkNoMissingTexture(mc, "overlays");
		});
		// the mark in the inventory of a marked player
		at(t += 5, mc -> server(mc, p -> p.setGameMode(GameType.SURVIVAL)));
		at(t += 10, mc -> mc.setScreen(new InventoryScreen(mc.player)));
		at(t += 15, mc -> {
			shot(mc, "inventory_mark");
			checkNoMissingTexture(mc, "inventory_mark");
		});
		at(t += 5, mc -> mc.setScreen(null));
		at(t += 2, mc -> server(mc, p -> p.setGameMode(GameType.CREATIVE)));
		// the clear mind key: client -> server message
		at(t += 5, mc -> Platform.INSTANCE.sendToServer(new ClearMindKeyMessage(0, 0)));
		at(t += 10, mc -> server(mc, p -> check("the clear mind key reaches the server (holding breath)", GrowlingManModVariables.of(p).IsHoldingBreath, "not holding")));
		at(t += 5, mc -> Platform.INSTANCE.sendToServer(new ClearMindKeyMessage(1, 500)));
		at(t += 10, mc -> server(mc, p -> check("releasing the key reaches the server", !GrowlingManModVariables.of(p).IsHoldingBreath, "still holding")));
		at(t += 5, mc -> {
			java.nio.file.Path config = Platform.INSTANCE.configDir().resolve(GrowlingManConfigConfiguration.FILE_NAME);
			check("the config file is written with the defaults", java.nio.file.Files.exists(config) && GrowlingManConfigConfiguration.GM_SPAWNTIME_MIN.get() == 60.0, config);
		});
		at(t += 5, DevScene::checkTranslations);
		at(t += 10, mc -> {
			GrowlingManMod.LOGGER.info("[scene] DONE " + (checks - failures) + "/" + checks + (failures == 0 ? " passed" : " (" + failures + " failed)"));
			mc.level.disconnect();
			mc.disconnect(new TitleScreen());
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
			org.lwjgl.glfw.GLFW.glfwSetWindowSize(mc.getWindow().getWindow(), 1600, 900);
			mc.options.guiScale().set(3);
			mc.options.cloudStatus().set(CloudStatus.OFF);
			mc.options.pauseOnLostFocus = false;
			mc.resizeDisplay();
			LevelSettings settings = new LevelSettings(WORLD, GameType.CREATIVE, false, Difficulty.NORMAL, true, new GameRules(), WorldDataConfiguration.DEFAULT);
			mc.createWorldOpenFlows().createFreshLevel(WORLD, settings, new WorldOptions(20261017L, false, false), WorldPresets::createNormalWorldDimensions, new TitleScreen());
			return;
		}
		if (mc.player == null || mc.getSingleplayerServer() == null)
			return;
		mc.options.pauseOnLostFocus = false;
		mc.getTutorial().setStep(net.minecraft.client.tutorial.TutorialSteps.NONE);
		mc.getToasts().clear();
		if (mc.screen instanceof PauseScreen)
			mc.setScreen(null);
		tick++;
		for (Step step : STEPS) {
			if (step.at() == tick) {
				try {
					step.action().accept(mc);
				} catch (RuntimeException | LinkageError e) {
					check("step at tick " + tick + " ran", false, e);
					GrowlingManMod.LOGGER.error("[scene] step failed", e);
				}
			}
		}
	}

	/** A grass platform in the sky at noon, every entity of the mod in rows facing the player. */
	private static void build(ServerPlayer player) {
		ServerLevel level = player.serverLevel();
		BlockPos spawn = level.getSharedSpawnPos();
		stage = new BlockPos(spawn.getX(), 150, spawn.getZ());
		level.setDayTime(6000);
		level.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, level.getServer());
		level.getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(false, level.getServer());
		level.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false, level.getServer());
		for (BlockPos pos : BlockPos.betweenClosed(stage.offset(-4, -1, -8), stage.offset(24, 6, 16)))
			level.setBlock(pos, pos.getY() == stage.getY() - 1 ? Blocks.GRASS_BLOCK.defaultBlockState() : Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
		List<EntityType<?>> types = modEntities();
		int spawned = 0;
		List<String> failed = new ArrayList<>();
		for (int i = 0; i < types.size(); i++) {
			try {
				Entity entity = types.get(i).create(level);
				if (entity == null) {
					failed.add(BuiltInRegistries.ENTITY_TYPE.getKey(types.get(i)).getPath());
					continue;
				}
				entity.moveTo(stage.getX() + (i % 10) * 2.0 + 0.5, stage.getY(), stage.getZ() + (i / 10) * 3.0 + 2.5, 180f, 0f);
				entity.setYHeadRot(180f);
				if (entity instanceof Mob mob) {
					mob.finalizeSpawn(level, level.getCurrentDifficultyAt(stage), MobSpawnType.COMMAND, null);
					mob.setNoAi(true);
					mob.setPersistenceRequired();
				}
				level.addFreshEntity(entity);
				spawned++;
			} catch (RuntimeException e) {
				failed.add(BuiltInRegistries.ENTITY_TYPE.getKey(types.get(i)).getPath() + ": " + e);
			}
		}
		check("every entity type spawns (" + types.size() + ")", failed.isEmpty(), failed);
		GrowlingManMod.LOGGER.info("[scene] spawned {} entities", spawned);
		player.getAbilities().flying = true;
		player.onUpdateAbilities();
		player.teleportTo(level, stage.getX() + 9.5, stage.getY() + 6, stage.getZ() - 6.5, 0f, 30f);
	}

	/**
	 * The README picture: the Growling Man between the trees of a dark forest at midnight, without the HUD or the hand.
	 * Shoots every pose to scene_poster_*.png (pick one by eye), then quits.
	 */
	private static void poster() {
		int t = 40;
		at(t, mc -> {
			mc.options.hideGui = true;
			mc.options.renderDistance().set(8);
			server(mc, DevScene::forest);
		});
		t += 200;
		// name, animation (null: the idle one), distance, rain
		Object[][] poses = {{"far", null, 6.0, false}, {"close", "stareshakear", 2.6, false}, {"lunge", "lunge", 3.5, false},
				{"rain", "stareshakear", 2.6, true}};
		for (Object[] pose : poses) {
			at(t, mc -> server(mc, p -> pose(p, (String)pose[1], (double)pose[2], (boolean)pose[3])));
			at(t += 60, mc -> shot(mc, "poster_" + pose[0]));
			t += 5;
		}
		at(t + 10, mc -> {
			GrowlingManMod.LOGGER.info("[scene] DONE poster");
			mc.level.disconnect();
			mc.disconnect(new TitleScreen());
			mc.stop();
		});
	}

	/** The closest dark forest: a spot with nothing in the way for 7 blocks ahead, midnight with a full moon, the player in survival. */
	private static void forest(ServerPlayer player) {
		ServerLevel level = player.serverLevel();
		BlockPos spawn = level.getSharedSpawnPos();
		var found = level.findClosestBiome3d(b -> b.is(net.minecraft.world.level.biome.Biomes.DARK_FOREST), spawn, 6400, 32, 64);
		BlockPos center = found == null ? spawn : found.getFirst();
		level.setDayTime(18000);
		level.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, level.getServer());
		level.getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(false, level.getServer());
		level.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false, level.getServer());
		level.setWeatherParameters(6000, 0, false, false);
		search:
		for (int r = 0; r < 160; r += 4)
			for (int dx = -r; dx <= r; dx += 4)
				for (int dz = -r; dz <= r; dz += 4) {
					if (Math.max(Math.abs(dx), Math.abs(dz)) != r)
						continue;
					BlockPos base = ground(level, center.getX() + dx, center.getZ() + dz);
					for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.Plane.HORIZONTAL)
						if (clear(level, base, dir)) {
							stage = base;
							facing = dir;
							break search;
						}
				}
		if (stage == null) {
			stage = ground(level, center.getX(), center.getZ());
			facing = net.minecraft.core.Direction.NORTH;
		}
		GrowlingManMod.LOGGER.info("[scene] poster at {} facing {} (dark forest found: {})", stage, facing, found != null);
		player.setGameMode(GameType.SURVIVAL);
		player.getAbilities().invulnerable = true;
		player.onUpdateAbilities();
		posed = (GrowlingManEntity)net.voxla.growlingman.init.GrowlingManModEntities.GROWLING_MAN.get().create(level);
		posed.setNoAi(true);
		posed.setPersistenceRequired();
		posed.moveTo(stage.getX() + 0.5, stage.getY(), stage.getZ() + 6.5, 180f, 0f);
		level.addFreshEntity(posed);
		player.teleportTo(level, stage.getX() + 0.5, stage.getY(), stage.getZ() + 0.5, facing.toYRot(), 0f);
	}

	private static BlockPos ground(ServerLevel level, int x, int z) {
		level.getChunk(x >> 4, z >> 4); // generated first: the heightmap of a missing chunk is the bottom of the world
		return new BlockPos(x, level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z);
	}

	/** Solid flat ground and free air (2 blocks wide, 4 high) for 7 blocks ahead, dark forest behind him as far as the fog */
	private static boolean clear(ServerLevel level, BlockPos base, net.minecraft.core.Direction dir) {
		for (int i = 0; i <= 64; i += 8)
			for (int side = -24; side <= 24; side += 24)
				if (!level.getBiome(base.relative(dir, i).relative(dir.getClockWise(), side)).is(net.minecraft.world.level.biome.Biomes.DARK_FOREST))
					return false;
		for (int i = 0; i <= 7; i++)
			for (int side = 0; side <= 1; side++) {
				BlockPos column = base.relative(dir, i).relative(dir.getClockWise(), side);
				if (ground(level, column.getX(), column.getZ()).getY() != base.getY() || !level.getBlockState(column.below()).isSolid())
					return false;
				for (int y = 0; y < 4; y++)
					if (!level.getBlockState(column.above(y)).getCollisionShape(level, column.above(y)).isEmpty()
							|| level.getBlockState(column.above(y)).is(net.minecraft.tags.BlockTags.LEAVES))
						return false;
			}
		return true;
	}

	private static void pose(ServerPlayer player, String animation, double distance, boolean rain) {
		ServerLevel level = player.serverLevel();
		level.setWeatherParameters(rain ? 0 : 6000, rain ? 6000 : 0, rain, false);
		double x = stage.getX() + 0.5 + facing.getStepX() * distance, z = stage.getZ() + 0.5 + facing.getStepZ() * distance;
		posed.moveTo(x, stage.getY(), z, facing.getOpposite().toYRot(), 0f);
		posed.setYHeadRot(facing.getOpposite().toYRot());
		posed.setYBodyRot(facing.getOpposite().toYRot());
		posed.getEntityData().set(GrowlingManEntity.DATA_PlayEventAnimation, animation != null);
		posed.getEntityData().set(GrowlingManEntity.DATA_EventAnimation, animation == null ? "" : "animation.growling_man." + animation);
		// look at the face: higher the closer it is
		float pitch = (float)-Math.toDegrees(Math.atan2(2.6 - 1.62, distance)) + 4f;
		player.teleportTo(level, stage.getX() + 0.5, stage.getY(), stage.getZ() + 0.5, facing.toYRot(), pitch);
	}

	private static void checkTranslations(Minecraft mc) {
		List<String> missing = new ArrayList<>();
		Language language = Language.getInstance();
		for (Item item : BuiltInRegistries.ITEM)
			if (BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(GrowlingManMod.MODID) && !language.has(item.getDescriptionId()))
				missing.add(item.getDescriptionId());
		for (EntityType<?> type : modEntities())
			if (!language.has(type.getDescriptionId()))
				missing.add(type.getDescriptionId());
		for (String key : List.of(GrowlingManModMobEffects.MENTAL_BREAKDOWN.get().getDescriptionId(), "key.growling_man.clear_mind_key"))
			if (!language.has(key))
				missing.add(key);
		check("translations: every item, entity, effect and key name in " + mc.options.languageCode, missing.isEmpty(), missing);
	}

	private static void checkNoMissingTexture(Minecraft mc, String name) {
		int magenta = 0;
		try (NativeImage image = Screenshot.takeScreenshot(mc.getMainRenderTarget())) {
			for (int pixel : image.getPixelsRGBA()) {
				int r = pixel & 0xFF, g = pixel >> 8 & 0xFF, b = pixel >> 16 & 0xFF;
				if (r > 200 && b > 200 && g < 40)
					magenta++;
			}
		}
		check(name + ": no missing texture on screen", magenta < 50, magenta + " magenta pixels");
	}

	private DevScene() {
	}
}
