package net.voxla.growlingman.procedures;

import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.voxla.growlingman.port.bus.LivingDeathEvent;
import net.voxla.growlingman.port.bus.Event;
import net.voxla.growlingman.port.bus.SubscribeEvent;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.minecraft.core.registries.BuiltInRegistries;
import net.voxla.growlingman.configuration.GrowlingManConfigConfiguration;
import net.voxla.growlingman.network.GrowlingManModVariables;

@EventBusSubscriber
public class AnimalDiesProcedure {
	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		if (event != null && event.getEntity() != null) {
			execute(
				event,
				event.getEntity().level(),
				event.getEntity().getX(),
				event.getEntity().getY(),
				event.getEntity().getZ(),
				event.getEntity(),
				event.getSource().getEntity()
			);
		}
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		execute(null, world, x, y, z, entity, sourceentity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		if (entity != null && sourceentity != null) {
			double soundx = 0.0;
			double soundz = 0.0;
			if (entity instanceof Animal
				&& sourceentity instanceof Player
				&& GrowlingManModVariables.of(sourceentity)
						.IsMarked
					> 0.0
				&& Math.random() > 0.93) {
				if (Math.random() > 0.7) {
					if (Math.random() > 0.7) {
						if (Math.random() > 0.7) {
							if (GrowlingManModVariables.MapVariables.get(world).GMSpawnTimer > 30.0) {
								GrowlingManModVariables.MapVariables.get(world).GMSpawnTimer = 30.0;
								GrowlingManModVariables.MapVariables.get(world).syncData(world);
							}
						} else if (world instanceof ServerLevel _level) {
							_level.getServer()
								.getCommands()
								.performPrefixedCommand(
									new CommandSourceStack(
											CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, _level, LevelBasedPermissionSet.OWNER, "", Component.literal(""), _level.getServer(), null
										)
										.withSuppressedOutput(),
									"/weather thunder 180"
								);
						}
					} else {
						soundx = x + (Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -30, -10) : Mth.nextInt(RandomSource.create(), 10, 30));
						soundz = z + (Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -30, -10) : Mth.nextInt(RandomSource.create(), 10, 30));
						if (world instanceof ServerLevel _level) {
							LightningBolt entityToSpawn = (LightningBolt)EntityType.LIGHTNING_BOLT.create(_level, EntitySpawnReason.COMMAND);
							entityToSpawn.snapTo(
								Vec3.atBottomCenterOf(BlockPos.containing(soundx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)soundx, (int)soundz), soundz))
							);
							_level.addFreshEntity(entityToSpawn);
						}
					}
				} else {
					soundx = x + (Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -30, -10) : Mth.nextInt(RandomSource.create(), 10, 30));
					soundz = z + (Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -30, -10) : Mth.nextInt(RandomSource.create(), 10, 30));
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(
								null,
								BlockPos.containing(
									soundx,
									(y > 65.0 ? world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)soundx, (int)soundz) : y)
										+ entity.getBbHeight()
										- entity.getBbHeight() / 4.0F
										+ 30.0,
									soundz
								),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.lightning_bolt.thunder")),
								SoundSource.WEATHER,
								5.0F,
								1.0F
							);
						} else {
							_level.playLocalSound(
								soundx,
								(y > 65.0 ? world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)soundx, (int)soundz) : y)
									+ entity.getBbHeight()
									- entity.getBbHeight() / 4.0F
									+ 30.0,
								soundz,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.lightning_bolt.thunder")),
								SoundSource.WEATHER,
								5.0F,
								1.0F,
								false
							);
						}
					}
				}
			}

			if (entity instanceof Animal
				&& sourceentity instanceof Player
				&& GrowlingManModVariables.of(sourceentity)
						.IsMarked
					== 0.0
				&& GrowlingManModVariables.of(sourceentity)
						.AnimalsKilled
					< 60.0) {
				if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilled
						> 15.0
					&& Math.random() > 0.93) {
					double _setval = 0.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(sourceentity);
						_vars.AnimalsKilled = _setval;
						_vars.syncPlayerVariables(sourceentity);
					}
					_setval = 1.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(sourceentity);
						_vars.IsMarked = _setval;
						_vars.syncPlayerVariables(sourceentity);
					}
				}

				double _setval = GrowlingManModVariables.of(sourceentity)
						.AnimalsKilled
					+ 1.0;
				{
					GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(sourceentity);
					_vars.AnimalsKilled = _setval;
					_vars.syncPlayerVariables(sourceentity);
				}
				_setval = 7200.0;
				{
					GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(sourceentity);
					_vars.AnimalsKilledResetWait = _setval;
					_vars.syncPlayerVariables(sourceentity);
				}
				if (GrowlingManModVariables.of(sourceentity)
						.AnimalsKilled
					== 15.0) {
					if (!world.isClientSide() && world instanceof Level _levelx) {
						if (!_levelx.isClientSide()) {
							_levelx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								1.0F
							);
						} else {
							_levelx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								1.0F,
								false
							);
						}
					}
				} else if (GrowlingManModVariables.of(sourceentity)
						.AnimalsKilled
					== 25.0) {
					if (!world.isClientSide() && world instanceof Level _levelxx) {
						if (!_levelxx.isClientSide()) {
							_levelxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.9F
							);
						} else {
							_levelxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.9F,
								false
							);
						}
					}
				} else if (GrowlingManModVariables.of(sourceentity)
						.AnimalsKilled
					== 40.0) {
					if (!world.isClientSide() && world instanceof Level _levelxxx) {
						if (!_levelxxx.isClientSide()) {
							_levelxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.8F
							);
						} else {
							_levelxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.8F,
								false
							);
						}
					}
				} else if (GrowlingManModVariables.of(sourceentity)
						.AnimalsKilled
					== 50.0) {
					if (!world.isClientSide() && world instanceof Level _levelxxxx) {
						if (!_levelxxxx.isClientSide()) {
							_levelxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.7F
							);
						} else {
							_levelxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.7F,
								false
							);
						}
					}
				} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilled
						== 55.0
					&& !world.isClientSide()
					&& world instanceof Level _levelxxxxx) {
					if (!_levelxxxxx.isClientSide()) {
						_levelxxxxx.playSound(
							null,
							BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
							SoundSource.PLAYERS,
							1.0F,
							0.5F
						);
					} else {
						_levelxxxxx.playLocalSound(
							x,
							y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
							z,
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
							SoundSource.PLAYERS,
							1.0F,
							0.5F,
							false
						);
					}
				}
			} else if (entity instanceof Animal
				&& sourceentity instanceof Player
				&& GrowlingManModVariables.of(sourceentity)
						.IsMarked
					== 0.0
				&& GrowlingManModVariables.of(sourceentity)
						.AnimalsKilled
					>= 60.0) {
				double _setval = 0.0;
				{
					GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(sourceentity);
					_vars.AnimalsKilled = _setval;
					_vars.syncPlayerVariables(sourceentity);
				}
				_setval = 1.0;
				{
					GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(sourceentity);
					_vars.IsMarked = _setval;
					_vars.syncPlayerVariables(sourceentity);
				}
			}

			if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() > 20.0) {
				if (entity instanceof Animal
					&& sourceentity instanceof Player
					&& GrowlingManModVariables.of(sourceentity)
							.IsMarked
						== 0.0
					&& GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						< 19.0) {
					double _setval = GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						+ 1.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(sourceentity);
						_vars.AnimalsKilledShortTime = _setval;
						_vars.syncPlayerVariables(sourceentity);
					}
					if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 10.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxx) {
							if (!_levelxxxxxx.isClientSide()) {
								_levelxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 12.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxx) {
							if (!_levelxxxxxxx.isClientSide()) {
								_levelxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F
								);
							} else {
								_levelxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 14.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxx) {
							if (!_levelxxxxxxxx.isClientSide()) {
								_levelxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F
								);
							} else {
								_levelxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 16.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxx) {
							if (!_levelxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F
								);
							} else {
								_levelxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 18.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxx) {
						if (!_levelxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F
							);
						} else {
							_levelxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F,
								false
							);
						}
					}
				} else if (entity instanceof Animal
					&& sourceentity instanceof Player
					&& GrowlingManModVariables.of(sourceentity)
							.IsMarked
						== 0.0
					&& GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						>= 19.0) {
					double _setval = 0.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(sourceentity);
						_vars.AnimalsKilledShortTime = _setval;
						_vars.syncPlayerVariables(sourceentity);
					}
					_setval = 1.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(sourceentity);
						_vars.IsMarked = _setval;
						_vars.syncPlayerVariables(sourceentity);
					}
				}
			} else if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() < 3.0) {
				if (entity instanceof Animal
					&& sourceentity instanceof Player
					&& GrowlingManModVariables.of(sourceentity)
							.IsMarked
						== 0.0
					&& GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						< 2.0) {
					double _setval = GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						+ 1.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(sourceentity);
						_vars.AnimalsKilledShortTime = _setval;
						_vars.syncPlayerVariables(sourceentity);
					}
					if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 2.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 3.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.7F
							);
						} else {
							_levelxxxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.7F,
								false
							);
						}
					}
				} else if (entity instanceof Animal
					&& sourceentity instanceof Player
					&& GrowlingManModVariables.of(sourceentity)
							.IsMarked
						== 0.0
					&& GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						>= 2.0) {
					double _setval = 0.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(sourceentity);
						_vars.AnimalsKilledShortTime = _setval;
						_vars.syncPlayerVariables(sourceentity);
					}
					_setval = 1.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(sourceentity);
						_vars.IsMarked = _setval;
						_vars.syncPlayerVariables(sourceentity);
					}
				}
			} else if (entity instanceof Animal
				&& sourceentity instanceof Player
				&& GrowlingManModVariables.of(sourceentity)
						.IsMarked
					== 0.0
				&& GrowlingManModVariables.of(sourceentity)
						.AnimalsKilledShortTime
					< (Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() - 1.0) {
				double _setval = GrowlingManModVariables.of(sourceentity)
						.AnimalsKilledShortTime
					+ 1.0;
				{
					GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(sourceentity);
					_vars.AnimalsKilledShortTime = _setval;
					_vars.syncPlayerVariables(sourceentity);
				}
				if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() == 3.0) {
					if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 2.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								1.0F
							);
						} else {
							_levelxxxxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								1.0F,
								false
							);
						}
					}
				} else if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() == 4.0) {
					if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 2.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 3.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.7F
							);
						} else {
							_levelxxxxxxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.7F,
								false
							);
						}
					}
				} else if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() == 5.0) {
					if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 2.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 3.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 4.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F
							);
						} else {
							_levelxxxxxxxxxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F,
								false
							);
						}
					}
				} else if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() == 6.0) {
					if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 3.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 4.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 5.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F
							);
						} else {
							_levelxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F,
								false
							);
						}
					}
				} else if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() == 7.0) {
					if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 3.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 4.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 5.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 6.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.7F
							);
						} else {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.7F,
								false
							);
						}
					}
				} else if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() == 8.0) {
					if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 4.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 5.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 6.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 7.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.7F
							);
						} else {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.7F,
								false
							);
						}
					}
				} else if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() == 9.0) {
					if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 5.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 6.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 7.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 8.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.7F
							);
						} else {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.7F,
								false
							);
						}
					}
				} else if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() == 10.0) {
					if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 5.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 6.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 7.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 8.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 9.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F
							);
						} else {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F,
								false
							);
						}
					}
				} else if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() == 11.0) {
					if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 5.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 6.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 8.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 9.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 10.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F
							);
						} else {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F,
								false
							);
						}
					}
				} else if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() == 12.0) {
					if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 5.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 7.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 9.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 8.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 11.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F
							);
						} else {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F,
								false
							);
						}
					}
				} else if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() == 13.0) {
					if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 6.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 8.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 10.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 11.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 12.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F
							);
						} else {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F,
								false
							);
						}
					}
				} else if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() == 14.0) {
					if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 7.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 9.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 11.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 12.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 13.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F
							);
						} else {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F,
								false
							);
						}
					}
				} else if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() == 15.0) {
					if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 8.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 10.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 12.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 13.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 14.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F
							);
						} else {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F,
								false
							);
						}
					}
				} else if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() == 16.0) {
					if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 9.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 11.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 13.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 14.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 15.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F
							);
						} else {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F,
								false
							);
						}
					}
				} else if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() == 17.0) {
					if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 10.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 12.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 14.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 15.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 16.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F
							);
						} else {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F,
								false
							);
						}
					}
				} else if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() == 18.0) {
					if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 10.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 12.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 14.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 16.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 17.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F
							);
						} else {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F,
								false
							);
						}
					}
				} else if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() == 19.0) {
					if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 10.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 12.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 14.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 16.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 18.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F
							);
						} else {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F,
								false
							);
						}
					}
				} else if ((Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() == 20.0) {
					if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 10.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 12.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.9F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 14.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.8F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
							.AnimalsKilledShortTime
						== 16.0) {
						if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
									SoundSource.PLAYERS,
									1.0F,
									0.7F,
									false
								);
							}
						}
					} else if (GrowlingManModVariables.of(sourceentity)
								.AnimalsKilledShortTime
							== 18.0
						&& !world.isClientSide()
						&& world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F
							);
						} else {
							_levelxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
								x,
								y + sourceentity.getBbHeight() - sourceentity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.6F,
								false
							);
						}
					}
				}
			} else if (entity instanceof Animal
				&& sourceentity instanceof Player
				&& GrowlingManModVariables.of(sourceentity)
						.IsMarked
					== 0.0
				&& GrowlingManModVariables.of(sourceentity)
						.AnimalsKilledShortTime
					>= (Double)GrowlingManConfigConfiguration.ANIMALS_KILLED_QUICKLY_TO_GET_MARKED.get() - 1.0) {
				double _setval = 0.0;
				{
					GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(sourceentity);
					_vars.AnimalsKilledShortTime = _setval;
					_vars.syncPlayerVariables(sourceentity);
				}
				_setval = 1.0;
				{
					GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(sourceentity);
					_vars.IsMarked = _setval;
					_vars.syncPlayerVariables(sourceentity);
				}
			}
		}
	}
}
