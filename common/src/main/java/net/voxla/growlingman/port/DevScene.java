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
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntitySpawnReason;
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
	private static final String WORLD = "gm_scene";
	private record Step(int at, Consumer<Minecraft> action) {
	}
	private static final List<Step> STEPS = new ArrayList<>();
	private static boolean opened;
	private static int tick, checks, failures;
	private static BlockPos stage;

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
		Screenshot.grab(mc.gameDirectory, "scene_" + name + ".png", mc.gameRenderer.mainRenderTarget(), 1, c -> GrowlingManMod.LOGGER.info("[scene] " + c.getString()));
	}

	private static List<EntityType<?>> modEntities() {
		List<EntityType<?>> list = new ArrayList<>();
		for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE)
			if (BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace().equals(GrowlingManMod.MODID))
				list.add(type);
		return list;
	}

	static {
		int t = 40;
		at(t, mc -> server(mc, DevScene::build));
		at(t += 60, mc -> {
			shot(mc, "entities");
			checkNoMissingTexture(mc, "entities");
		});
		at(t += 5, mc -> server(mc, p -> p.teleportTo(p.level(), stage.getX() + 0.5, stage.getY() + 1.6, stage.getZ() - 3.5, java.util.Set.of(), 0f, 10f, true)));
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
		at(t += 10, mc -> mc.gui.setScreen(new InventoryScreen(mc.player)));
		at(t += 15, mc -> {
			shot(mc, "inventory_mark");
			checkNoMissingTexture(mc, "inventory_mark");
		});
		at(t += 5, mc -> mc.gui.setScreen(null));
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
		if (!opened && mc.gui.screen() instanceof net.minecraft.client.gui.screens.AccessibilityOnboardingScreen) {
			mc.options.onboardAccessibility = false;
			mc.options.save();
			mc.gui.setScreen(new TitleScreen());
			return;
		}
		if (!opened && mc.gui.overlay() != null)
			return;
		if (!opened && mc.gui.screen() instanceof TitleScreen) {
			opened = true;
			mc.getWindow().setWindowed(1600, 900);
			mc.options.guiScale().set(3);
			mc.options.cloudStatus().set(CloudStatus.OFF);
			mc.options.pauseOnLostFocus = false;
			mc.resizeGui();
			LevelSettings settings = new LevelSettings(WORLD, GameType.CREATIVE, new LevelSettings.DifficultySettings(Difficulty.NORMAL, false, false), true, WorldDataConfiguration.DEFAULT);
			mc.createWorldOpenFlows().createFreshLevel(WORLD, settings, new WorldOptions(20261017L, false, false), WorldPresets::createNormalWorldDimensions, new TitleScreen());
			return;
		}
		if (mc.player == null || mc.getSingleplayerServer() == null)
			return;
		mc.options.pauseOnLostFocus = false;
		mc.getTutorial().setStep(net.minecraft.client.tutorial.TutorialSteps.NONE);
		mc.gui.toastManager().clear();
		if (mc.gui.screen() instanceof PauseScreen)
			mc.gui.setScreen(null);
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
		ServerLevel level = player.level();
		BlockPos spawn = level.getRespawnData().pos();
		stage = new BlockPos(spawn.getX(), 150, spawn.getZ());
		level.getServer().getCommands().performPrefixedCommand(level.getServer().createCommandSourceStack().withSuppressedOutput(), "time set 6000");
		level.getGameRules().set(GameRules.ADVANCE_TIME, false, level.getServer());
		level.getGameRules().set(GameRules.ADVANCE_WEATHER, false, level.getServer());
		level.getGameRules().set(GameRules.SPAWN_MOBS, false, level.getServer());
		for (BlockPos pos : BlockPos.betweenClosed(stage.offset(-4, -1, -8), stage.offset(24, 6, 16)))
			level.setBlock(pos, pos.getY() == stage.getY() - 1 ? Blocks.GRASS_BLOCK.defaultBlockState() : Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
		List<EntityType<?>> types = modEntities();
		int spawned = 0;
		List<String> failed = new ArrayList<>();
		for (int i = 0; i < types.size(); i++) {
			try {
				Entity entity = types.get(i).create(level, EntitySpawnReason.COMMAND);
				if (entity == null) {
					failed.add(BuiltInRegistries.ENTITY_TYPE.getKey(types.get(i)).getPath());
					continue;
				}
				entity.snapTo(stage.getX() + (i % 10) * 2.0 + 0.5, stage.getY(), stage.getZ() + (i / 10) * 3.0 + 2.5, 180f, 0f);
				entity.setYHeadRot(180f);
				if (entity instanceof Mob mob) {
					mob.finalizeSpawn(level, level.getCurrentDifficultyAt(stage), EntitySpawnReason.COMMAND, null);
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
		player.teleportTo(level, stage.getX() + 9.5, stage.getY() + 6, stage.getZ() - 6.5, java.util.Set.of(), 0f, 30f, true);
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
		// 1.21.5+: the screenshot is read back from the GPU asynchronously
		Screenshot.takeScreenshot(mc.gameRenderer.mainRenderTarget(), image -> {
			int magenta = 0;
			try (image) {
				for (int y = 0; y < image.getHeight(); y += 2)
					for (int x = 0; x < image.getWidth(); x += 2) {
						int pixel = image.getPixel(x, y);
						int r = pixel >> 16 & 0xFF, g = pixel >> 8 & 0xFF, b = pixel & 0xFF;
						if (r > 200 && b > 200 && g < 40)
							magenta++;
					}
			}
			check(name + ": no missing texture on screen", magenta < 15, magenta + " magenta pixels");
		});
	}

	private DevScene() {
	}
}
