package net.voxla.growlingman.procedures;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.voxla.growlingman.port.bus.Event;
import net.voxla.growlingman.port.bus.LevelTickEvent;
import net.voxla.growlingman.port.bus.SubscribeEvent;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.voxla.growlingman.configuration.GrowlingManConfigConfiguration;
import net.voxla.growlingman.network.GrowlingManModVariables;

@EventBusSubscriber
public class GMSpawningTimerProcedure {
	@SubscribeEvent
	public static void onWorldTick(LevelTickEvent.Post event) {
		// 1.0.1 ran this after the tick of every dimension: the spawn and chase timers (global) went down once per loaded
		// dimension, three times as fast as the seconds of the config
		if (event.getLevel().dimension() == Level.OVERWORLD)
			execute(event, event.getLevel());
	}

	public static void execute(LevelAccessor world) {
		execute(null, world);
	}

	private static void execute(@Nullable Event event, LevelAccessor world) {
		if (world instanceof ServerLevel _origLevel) {
			LevelAccessor var6 = _origLevel.getServer().getLevel(Level.OVERWORLD);
			if (var6 != null) {
				if (var6 instanceof Level _lvl0 && _lvl0.isDay() && !GrowlingManModVariables.WorldVariables.get(var6).IsDaytime) {
					GrowlingManModVariables.WorldVariables.get(var6).IsDaytime = true;
					GrowlingManModVariables.WorldVariables.get(var6).syncData(var6);
				} else if (!(var6 instanceof Level _lvl1 && _lvl1.isDay()) && GrowlingManModVariables.WorldVariables.get(var6).IsDaytime) {
					GrowlingManModVariables.WorldVariables.get(var6).IsDaytime = false;
					GrowlingManModVariables.WorldVariables.get(var6).syncData(var6);
				}

				if (var6.dayTime() > 24000L && !GrowlingManModVariables.WorldVariables.get(var6).HasOneDayPassed) {
					GrowlingManModVariables.WorldVariables.get(var6).HasOneDayPassed = true;
					GrowlingManModVariables.WorldVariables.get(var6).syncData(var6);
				}
			}

			world = world;
		}

		if ((Boolean)GrowlingManConfigConfiguration.DONT_SPAWN_GM_ON_FIRST_DAY.get() && GrowlingManModVariables.WorldVariables.get(world).HasOneDayPassed
			|| !(Boolean)GrowlingManConfigConfiguration.DONT_SPAWN_GM_ON_FIRST_DAY.get()) {
			if (GrowlingManModVariables.MapVariables.get(world).GMSpawnTimer > 0.0) {
				if (GrowlingManModVariables.MapVariables.get(world).GMSpawnTimer <= 20.0
					&& GrowlingManModVariables.MapVariables.get(world).GMSpawnTimer > 15.0
					&& !GrowlingManModVariables.WorldVariables.get(world).IsDaytime) {
					GrowlingManModVariables.WorldVariables.get(world).GMSpawnChance = GrowlingManConfigConfiguration.GM_SPAWN_CHANCE.get() == 1.0
						? 1.0
						: Mth.nextInt(RandomSource.create(), 0, (int)((Double)GrowlingManConfigConfiguration.GM_SPAWN_CHANCE.get()).doubleValue());
					GrowlingManModVariables.WorldVariables.get(world).syncData(world);
				} else if (GrowlingManModVariables.MapVariables.get(world).GMSpawnTimer <= 20.0
					&& GrowlingManModVariables.MapVariables.get(world).GMSpawnTimer > 15.0
					&& GrowlingManModVariables.WorldVariables.get(world).IsDaytime) {
					GrowlingManModVariables.WorldVariables.get(world).GMSpawnChance = GrowlingManConfigConfiguration.GM_SPAWN_CHANCE.get() == 1.0
						? 1.0
						: Mth.nextInt(RandomSource.create(), 0, (int)((Double)GrowlingManConfigConfiguration.GM_SPAWN_CHANCE.get() * 2.0));
					GrowlingManModVariables.WorldVariables.get(world).syncData(world);
				}

				GrowlingManModVariables.MapVariables.get(world).GMSpawnTimer--;
				GrowlingManModVariables.MapVariables.get(world).syncData(world);
			}

			if (GrowlingManModVariables.MapVariables.get(world).GMSpawnTimer == 0.0) {
				GrowlingManModVariables.MapVariables.get(world).GMSpawnTimer = Mth.nextInt(
					RandomSource.create(),
					(int)((Double)GrowlingManConfigConfiguration.GM_SPAWNTIME_MIN.get() * 20.0),
					(int)((Double)GrowlingManConfigConfiguration.GM_SPAWNTIME_MAX.get() * 20.0)
				);
				GrowlingManModVariables.MapVariables.get(world).syncData(world);
			}

			if (GrowlingManModVariables.MapVariables.get(world).GMCanKillPlayerTimer < 3600.0) {
				GrowlingManModVariables.MapVariables.get(world).GMCanKillPlayerTimer++;
				GrowlingManModVariables.MapVariables.get(world).syncData(world);
			}
		}
	}
}
