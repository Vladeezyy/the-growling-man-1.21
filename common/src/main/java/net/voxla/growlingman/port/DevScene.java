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
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
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
 * lines up every entity of the mod (AI off), screenshots them, the items in the creative tab and the Lurker book, checks
 * the screen for missing (magenta) textures and every name for a translation, logs {@code [scene] CHECK / DONE}, quits.
 */
@EventBusSubscriber(value = Dist.CLIENT)
public final class DevScene {
	private static final boolean ENABLED = System.getProperty("growling_man.scene") != null;
	private static final String WORLD = "ml_scene";
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
		int t = 40;
		at(t, mc -> server(mc, DevScene::build));
		at(t += 60, mc -> {
			GrowlingManMod.LOGGER.info("[scene] player effects " + mc.player.getActiveEffects() + " fog " + PortUtil.data(mc.player).getDouble("InsanityFog")
					+ " stage " + net.voxla.growlingman.network.GrowlingManModVariables.of(mc.player).InsanityStage + " time " + mc.level.getDayTime());
			shot(mc, "entities");
			checkNoMissingTexture(mc, "entities");
		});
		at(t += 5, mc -> server(mc, p -> p.teleportTo(p.serverLevel(), stage.getX() + 9.5, stage.getY() + 1.6, stage.getZ() - 3.5, 0f, 10f)));
		at(t += 20, mc -> shot(mc, "entities_close"));
		at(t += 5, mc -> server(mc, p -> {
			p.getInventory().clearContent();
			int slot = 0;
			for (Item item : BuiltInRegistries.ITEM)
				if (BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(GrowlingManMod.MODID) && slot < 36)
					p.getInventory().setItem(slot++, new ItemStack(item));
		}));
		at(t += 10, mc -> mc.setScreen(new CreativeModeInventoryScreen(mc.player, mc.player.connection.enabledFeatures(), true)));
		at(t += 15, mc -> {
			shot(mc, "inventory");
			checkNoMissingTexture(mc, "inventory");
		});
		at(t += 5, mc -> mc.setScreen(null));
		at(t += 5, mc -> server(mc, p -> {
			p.getInventory().selected = 0;
			for (int i = 0; i < 36; i++)
				if (BuiltInRegistries.ITEM.getKey(p.getInventory().getItem(i).getItem()).getPath().equals("lurker_book"))
					p.getInventory().setItem(0, p.getInventory().getItem(i).copy());
		}));
		at(t += 10, mc -> mc.gameMode.useItem(mc.player, net.minecraft.world.InteractionHand.MAIN_HAND));
		at(t += 20, mc -> {
			shot(mc, "lurker_book");
			check("the Lurker book opens its screen", mc.screen != null && !(mc.screen instanceof PauseScreen), mc.screen);
		});
		at(t += 5, mc -> mc.setScreen(null));
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

	private static void checkTranslations(Minecraft mc) {
		List<String> missing = new ArrayList<>();
		Language language = Language.getInstance();
		for (Item item : BuiltInRegistries.ITEM)
			if (BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(GrowlingManMod.MODID) && !language.has(item.getDescriptionId()))
				missing.add(item.getDescriptionId());
		for (EntityType<?> type : modEntities())
			if (!language.has(type.getDescriptionId()))
				missing.add(type.getDescriptionId());
		check("translations: every item and entity name in " + mc.options.languageCode, missing.isEmpty(), missing);
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
