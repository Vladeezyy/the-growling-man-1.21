package net.voxla.growlingman.procedures;

import net.minecraft.client.Minecraft;
import net.minecraft.advancements.AdvancementHolder;

import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.voxla.growlingman.network.InventoryScreenMessage;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.voxla.growlingman.port.bus.PlayerTickEvent;
import net.voxla.growlingman.port.bus.Event;
import net.voxla.growlingman.port.bus.SubscribeEvent;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.minecraft.core.registries.BuiltInRegistries;
import net.voxla.growlingman.configuration.GrowlingManConfigConfiguration;
import net.voxla.growlingman.entity.GrowlingManEntity;
import net.voxla.growlingman.init.GrowlingManModEntities;
import net.voxla.growlingman.init.GrowlingManModMobEffects;
import net.voxla.growlingman.network.GrowlingManModVariables;

@EventBusSubscriber
public class GMCanSpawnProcedure {
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity != null) {
			double tsoundsrandomizer = 0.0;
			double blockDetectY = 0.0;
			double blocksoundrandomizer = 0.0;
			double spawnx = 0.0;
			double soundx = 0.0;
			double spawnz = 0.0;
			double soundz = 0.0;
			double YRepeater = 0.0;
			if ((world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD))
					== Level.OVERWORLD
				&& !(new Object() {
						public boolean checkGamemode(Entity _ent) {
							if (_ent instanceof ServerPlayer _serverPlayer) {
								return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
							} else {
								return _ent.level().isClientSide() && _ent instanceof Player _player
									? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
										&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE
									: false;
							}
						}
					})
					.checkGamemode(entity)
				&& !(new Object() {
						public boolean checkGamemode(Entity _ent) {
							if (_ent instanceof ServerPlayer _serverPlayer) {
								return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
							} else {
								return _ent.level().isClientSide() && _ent instanceof Player _player
									? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
										&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR
									: false;
							}
						}
					})
					.checkGamemode(entity)) {
				if ((
						GrowlingManModVariables.of(entity)
									.IsMarked
								> 0.0
							|| GrowlingManModVariables.of(entity)
										.IsMarked
									== 0.0
								&& Math.random() > 0.7
					)
					&& GrowlingManModVariables.MapVariables.get(world).GMSpawnTimer >= 1.0
					&& GrowlingManModVariables.MapVariables.get(world).GMSpawnTimer <= 10.0
					&& GrowlingManModVariables.WorldVariables.get(world).GMSpawnChance == 1.0
					&& world.getEntitiesOfClass(GrowlingManEntity.class, AABB.ofSize(new Vec3(x, y, z), 600.0, 600.0, 600.0), e -> true).isEmpty()
					&& (
						(Boolean)GrowlingManConfigConfiguration.DONT_SPAWN_GM_ON_FIRST_DAY.get() && GrowlingManModVariables.WorldVariables.get(world).HasOneDayPassed
							|| !(Boolean)GrowlingManConfigConfiguration.DONT_SPAWN_GM_ON_FIRST_DAY.get()
					)) {
					if (y < world.getHeight(Types.OCEAN_FLOOR, (int)x, (int)z) - 55) {
						if (world.getEntitiesOfClass(GrowlingManEntity.class, AABB.ofSize(new Vec3(x, y, z), 600.0, 600.0, 600.0), e -> true).isEmpty()) {
							spawnx = Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -28, -10) : Mth.nextInt(RandomSource.create(), 10, 28);
							spawnz = Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -28, -10) : Mth.nextInt(RandomSource.create(), 10, 28);
						}

						YRepeater = -4.0;

						label1092:
						for (int index0 = 0; index0 < 16; index0++) {
							if (world.getBlockState(BlockPos.containing(x + spawnx, y + YRepeater - 1.0, z + spawnz)).canOcclude()
								&& !world.getBlockState(BlockPos.containing(x + spawnx, y + YRepeater + 0.0, z + spawnz)).canOcclude()
								&& !world.getBlockState(BlockPos.containing(x + spawnx, y + YRepeater + 1.0, z + spawnz)).canOcclude()) {
								if (world instanceof ServerLevel _level) {
									Entity entityToSpawn = ((EntityType)GrowlingManModEntities.GROWLING_MAN.get())
										.spawn(_level, BlockPos.containing(x + spawnx, y + YRepeater, z + spawnz), MobSpawnType.MOB_SUMMONED);
									if (entityToSpawn != null) {
										entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
									}
								}

								Vec3 _center = new Vec3(x + spawnx, y + YRepeater, z + spawnz);

								for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.0), e -> true)
									.stream()
									.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
									.toList()) {
									if (entityiterator instanceof GrowlingManEntity) {
										if (entityiterator instanceof GrowlingManEntity _datEntSetI) {
											_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_AI, 11);
										}
										break label1092;
									}
								}
								break;
							}

							YRepeater++;
						}
					} else if (world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z))) {
						if (world.getEntitiesOfClass(GrowlingManEntity.class, AABB.ofSize(new Vec3(x, y, z), 600.0, 600.0, 600.0), e -> true).isEmpty()) {
							spawnx = Math.random() > 0.6
								? Mth.nextInt(RandomSource.create(), (int)(0.0 - (Double)GrowlingManConfigConfiguration.SPAWN_DISTANCE.get()), -10)
								: Mth.nextInt(RandomSource.create(), 10, (int)(0.0 + (Double)GrowlingManConfigConfiguration.SPAWN_DISTANCE.get()));
							spawnz = Math.random() > 0.6
								? Mth.nextInt(RandomSource.create(), (int)(0.0 - (Double)GrowlingManConfigConfiguration.SPAWN_DISTANCE.get()), -10)
								: Mth.nextInt(RandomSource.create(), 10, (int)(0.0 + (Double)GrowlingManConfigConfiguration.SPAWN_DISTANCE.get()));
						}

						if (!world.getBlockState(
									BlockPos.containing(x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) - 1, z + spawnz)
								)
								.canOcclude()
							&& !(
								world.getBlockState(
										BlockPos.containing(
											x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) - 1, z + spawnz
										)
									)
									.getBlock() instanceof LiquidBlock
							)) {
							YRepeater = 0.0;

							label1138:
							for (int index1 = 0; index1 < 32; index1++) {
								if (world.getBlockState(
										BlockPos.containing(
											x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + YRepeater - 1.0, z + spawnz
										)
									)
									.canOcclude()) {
									if (!world.getBlockState(
												BlockPos.containing(
													x + spawnx,
													world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + YRepeater + 0.0,
													z + spawnz
												)
											)
											.canOcclude()
										&& !world.getBlockState(
												BlockPos.containing(
													x + spawnx,
													world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + YRepeater + 1.0,
													z + spawnz
												)
											)
											.canOcclude()
										&& !world.getBlockState(
												BlockPos.containing(
													x + spawnx,
													world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + YRepeater + 2.0,
													z + spawnz
												)
											)
											.canOcclude()) {
										if (world instanceof ServerLevel _levelx) {
											Entity entityToSpawn = ((EntityType)GrowlingManModEntities.GROWLING_MAN.get())
												.spawn(
													_levelx,
													BlockPos.containing(
														x + spawnx,
														world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + YRepeater,
														z + spawnz
													),
													MobSpawnType.MOB_SUMMONED
												);
											if (entityToSpawn != null) {
												entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
											}
										}

										Vec3 _center = new Vec3(
											x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + YRepeater, z + spawnz
										);

										for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.0), e -> true)
											.stream()
											.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
											.toList()) {
											if (entityiteratorx instanceof GrowlingManEntity) {
												if (entityiteratorx instanceof GrowlingManEntity _datEntSetI) {
													_datEntSetI.getEntityData()
														.set(
															GrowlingManEntity.DATA_AI,
															GrowlingManModVariables.of(entity)
																			.IsMarked
																		> 0.0
																	&& Math.random() > 0.7
																? 10
																: Mth.nextInt(RandomSource.create(), 1, 3)
														);
												}
												break label1138;
											}
										}
										break;
									}
								} else {
									YRepeater--;
								}
							}
						} else if (!world.getBlockState(
									BlockPos.containing(x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + 0, z + spawnz)
								)
								.canOcclude()
							&& !world.getBlockState(
									BlockPos.containing(x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + 1, z + spawnz)
								)
								.canOcclude()
							&& !world.getBlockState(
									BlockPos.containing(x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + 2, z + spawnz)
								)
								.canOcclude()
							&& world.getBlockState(
									BlockPos.containing(x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) - 1, z + spawnz)
								)
								.canOcclude()) {
							if (world instanceof ServerLevel _levelxx) {
								Entity entityToSpawn = ((EntityType)GrowlingManModEntities.GROWLING_MAN.get())
									.spawn(
										_levelxx,
										BlockPos.containing(x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)), z + spawnz),
										MobSpawnType.MOB_SUMMONED
									);
								if (entityToSpawn != null) {
									entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
								}
							}

							Vec3 _center = new Vec3(x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)), z + spawnz);

							for (Entity entityiteratorxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.0), e -> true)
								.stream()
								.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
								.toList()) {
								if (entityiteratorxx instanceof GrowlingManEntity) {
									if (entityiteratorxx instanceof GrowlingManEntity _datEntSetI) {
										_datEntSetI.getEntityData()
											.set(
												GrowlingManEntity.DATA_AI,
												GrowlingManModVariables.of(entity)
																.IsMarked
															> 0.0
														&& Math.random() > 0.7
													? 10
													: Mth.nextInt(RandomSource.create(), 1, 3)
											);
									}
									break;
								}
							}
						}
					} else {
						if (world.getEntitiesOfClass(GrowlingManEntity.class, AABB.ofSize(new Vec3(x, y, z), 600.0, 600.0, 600.0), e -> true).isEmpty()) {
							if ((Double)GrowlingManConfigConfiguration.SPAWN_DISTANCE.get() <= 30.0) {
								spawnx = Math.random() > 0.6
									? Mth.nextInt(RandomSource.create(), (int)(0.0 - (Double)GrowlingManConfigConfiguration.SPAWN_DISTANCE.get()), -10)
									: Mth.nextInt(RandomSource.create(), 10, (int)(0.0 + (Double)GrowlingManConfigConfiguration.SPAWN_DISTANCE.get()));
								spawnz = Math.random() > 0.6
									? Mth.nextInt(RandomSource.create(), (int)(0.0 - (Double)GrowlingManConfigConfiguration.SPAWN_DISTANCE.get()), -10)
									: Mth.nextInt(RandomSource.create(), 10, (int)(0.0 + (Double)GrowlingManConfigConfiguration.SPAWN_DISTANCE.get()));
							} else if (Math.round((Double)GrowlingManConfigConfiguration.SPAWN_DISTANCE.get() / 2.0) >= 15L) {
								spawnx = Math.random() > 0.6
									? Mth.nextInt(
										RandomSource.create(), (int)(0L - Math.round((Double)GrowlingManConfigConfiguration.SPAWN_DISTANCE.get() / 2.0)), -10
									)
									: Mth.nextInt(
										RandomSource.create(), 10, (int)(0L + Math.round((Double)GrowlingManConfigConfiguration.SPAWN_DISTANCE.get() / 2.0))
									);
								spawnz = Math.random() > 0.6
									? Mth.nextInt(
										RandomSource.create(), (int)(0L - Math.round((Double)GrowlingManConfigConfiguration.SPAWN_DISTANCE.get() / 2.0)), -10
									)
									: Mth.nextInt(
										RandomSource.create(), 10, (int)(0L + Math.round((Double)GrowlingManConfigConfiguration.SPAWN_DISTANCE.get() / 2.0))
									);
							} else {
								spawnx = Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -15, -10) : Mth.nextInt(RandomSource.create(), 10, 15);
								spawnz = Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -15, -10) : Mth.nextInt(RandomSource.create(), 10, 15);
							}
						}

						YRepeater = 3.0;

						label1172:
						for (int index2 = 0; index2 < 15; index2++) {
							if (world.getBlockState(BlockPos.containing(x + spawnx, y + YRepeater - 1.0, z + spawnz)).canOcclude()
								&& !world.getBlockState(BlockPos.containing(x + spawnx, y + YRepeater + 0.0, z + spawnz)).canOcclude()
								&& !world.getBlockState(BlockPos.containing(x + spawnx, y + YRepeater + 1.0, z + spawnz)).canOcclude()) {
								if (world instanceof ServerLevel _levelxxx) {
									Entity entityToSpawn = ((EntityType)GrowlingManModEntities.GROWLING_MAN.get())
										.spawn(_levelxxx, BlockPos.containing(x + spawnx, y + YRepeater, z + spawnz), MobSpawnType.MOB_SUMMONED);
									if (entityToSpawn != null) {
										entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
									}
								}

								Vec3 _center = new Vec3(x + spawnx, y + YRepeater, z + spawnz);

								for (Entity entityiteratorxxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.0), e -> true)
									.stream()
									.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
									.toList()) {
									if (entityiteratorxxx instanceof GrowlingManEntity) {
										if (entityiteratorxxx instanceof GrowlingManEntity _datEntSetI) {
											_datEntSetI.getEntityData()
												.set(
													GrowlingManEntity.DATA_AI,
													GrowlingManModVariables.of(entity)
																	.IsMarked
																> 0.0
															&& Math.random() > 0.7
														? 10
														: Mth.nextInt(RandomSource.create(), 1, 3)
												);
										}
										break label1172;
									}
								}
								break;
							}

							YRepeater--;
						}
					}
				}

				if (world.isClientSide() && !world.getEntitiesOfClass(GrowlingManEntity.class, AABB.ofSize(new Vec3(x, y, z), 321.0, 321.0, 321.0), e -> true).isEmpty()) {
					Vec3 _center = new Vec3(x, y, z);

					for (Entity entityiteratorxxxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(160.5), e -> true)
						.stream()
						.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
						.toList()) {
						if (entityiteratorxxxx instanceof GrowlingManEntity
							&& entityiteratorxxxx instanceof GrowlingManEntity _datEntL100
							&& (Boolean)_datEntL100.getEntityData().get(GrowlingManEntity.DATA_Chasing)
							&& Math.random() > 0.9) {
							soundx = x + (Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -30, -10) : Mth.nextInt(RandomSource.create(), 10, 30));
							soundz = z + (Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -30, -10) : Mth.nextInt(RandomSource.create(), 10, 30));
							if (world instanceof Level _levelxxxx) {
								if (!_levelxxxx.isClientSide()) {
									_levelxxxx.playSound(
										null,
										BlockPos.containing(
											soundx,
											(y > 65.0 ? world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)soundx, (int)soundz) : y)
												+ entity.getBbHeight()
												- entity.getBbHeight() / 4.0F,
											soundz
										),
										(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.growling_man.ambient_distant")),
										SoundSource.HOSTILE,
										5.0F,
										1.0F
									);
								} else {
									_levelxxxx.playLocalSound(
										soundx,
										(y > 65.0 ? world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)soundx, (int)soundz) : y)
											+ entity.getBbHeight()
											- entity.getBbHeight() / 4.0F,
										soundz,
										(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.growling_man.ambient_distant")),
										SoundSource.HOSTILE,
										5.0F,
										1.0F,
										false
									);
								}
							}
						}
					}
				}

				if (GrowlingManModVariables.of(entity)
						.GMEventsTimer
					<= 0.0) {
					double _setval = 12000.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.GMEventsTimer = _setval;
						_vars.syncPlayerVariables(entity);
					}
				}

				if (GrowlingManModVariables.of(entity)
						.GMEventsTimer
					> 0.0) {
					double _setval = GrowlingManModVariables.of(entity)
							.GMEventsTimer
						- 1.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.GMEventsTimer = _setval;
						_vars.syncPlayerVariables(entity);
					}
					if ((
							GrowlingManModVariables.of(entity)
										.GMEventsTimer
									== 3000.0
								|| GrowlingManModVariables.of(entity)
										.GMEventsTimer
									== 6000.0
								|| GrowlingManModVariables.of(entity)
										.GMEventsTimer
									== 8000.0
								|| GrowlingManModVariables.of(entity)
										.GMEventsTimer
									== 11000.0
						)
						&& Math.random() > 0.8) {
						if (Math.random() > 0.6) {
							if (!world.isClientSide()) {
								soundx = x
									+ (Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -30, -10) : Mth.nextInt(RandomSource.create(), 10, 30));
								soundz = z
									+ (Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -30, -10) : Mth.nextInt(RandomSource.create(), 10, 30));
								if (world instanceof Level _levelxxxxx) {
									if (!_levelxxxxx.isClientSide()) {
										_levelxxxxx.playSound(
											null,
											BlockPos.containing(
												soundx,
												(
														y > world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)x, (int)z) - 65
															? world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)soundx, (int)soundz)
															: y
													)
													+ entity.getBbHeight()
													- entity.getBbHeight() / 4.0F,
												soundz
											),
											(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:ambient.distant")),
											SoundSource.HOSTILE,
											5.0F,
											1.0F
										);
									} else {
										_levelxxxxx.playLocalSound(
											soundx,
											(
													y > world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)x, (int)z) - 65
														? world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)soundx, (int)soundz)
														: y
												)
												+ entity.getBbHeight()
												- entity.getBbHeight() / 4.0F,
											soundz,
											(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:ambient.distant")),
											SoundSource.HOSTILE,
											5.0F,
											1.0F,
											false
										);
									}
								}
							}
						} else if (!world.isClientSide()) {
							soundx = x + (Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -30, -10) : Mth.nextInt(RandomSource.create(), 10, 30));
							soundz = z + (Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -30, -10) : Mth.nextInt(RandomSource.create(), 10, 30));
							if (world instanceof Level _levelxxxxxx) {
								if (!_levelxxxxxx.isClientSide()) {
									_levelxxxxxx.playSound(
										null,
										BlockPos.containing(
											soundx,
											(
													y > world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)x, (int)z) - 65
														? world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)soundx, (int)soundz)
														: y
												)
												+ entity.getBbHeight()
												- entity.getBbHeight() / 4.0F,
											soundz
										),
										(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.growling_man.ambient_distant")),
										SoundSource.HOSTILE,
										5.0F,
										1.0F
									);
								} else {
									_levelxxxxxx.playLocalSound(
										soundx,
										(
												y > world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)x, (int)z) - 65
													? world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)soundx, (int)soundz)
													: y
											)
											+ entity.getBbHeight()
											- entity.getBbHeight() / 4.0F,
										soundz,
										(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.growling_man.ambient_distant")),
										SoundSource.HOSTILE,
										5.0F,
										1.0F,
										false
									);
								}
							}
						}
					}
				}

				if (GrowlingManModVariables.of(entity)
							.IsMarked
						> 0.0
					&& world.getLevelData().getGameTime()
							% Math.round(
								3600.0
									- GrowlingManModVariables.of(entity)
											.IsMarked
										/ 45.0
							)
						== 1L
					&& world.isClientSide()
					&& Math.random() > 0.9) {
					soundx = x + (Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -30, -10) : Mth.nextInt(RandomSource.create(), 10, 30));
					soundz = z + (Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -30, -10) : Mth.nextInt(RandomSource.create(), 10, 30));
					if (world instanceof Level _levelxxxxxxx) {
						if (!_levelxxxxxxx.isClientSide()) {
							_levelxxxxxxx.playSound(
								null,
								BlockPos.containing(
									soundx,
									(
											y > world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)x, (int)z) - 65
												? world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)soundx, (int)soundz)
												: y
										)
										+ entity.getBbHeight()
										- entity.getBbHeight() / 4.0F,
									soundz
								),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.growling_man.near")),
								SoundSource.HOSTILE,
								5.0F,
								1.0F
							);
						} else {
							_levelxxxxxxx.playLocalSound(
								soundx,
								(
										y > world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)x, (int)z) - 65
											? world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)soundx, (int)soundz)
											: y
									)
									+ entity.getBbHeight()
									- entity.getBbHeight() / 4.0F,
								soundz,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.growling_man.near")),
								SoundSource.HOSTILE,
								5.0F,
								1.0F,
								false
							);
						}
					}
				}
			}

			if (GrowlingManModVariables.of(entity)
					.ScreenShakeTimer
				> 0.0) {
				entity.setYRot(entity.getYRot() + Mth.nextInt(RandomSource.create(), -1, 1));
				entity.setXRot(entity.getXRot() + Mth.nextInt(RandomSource.create(), -1, 1));
				entity.setYBodyRot(entity.getYRot());
				entity.setYHeadRot(entity.getYRot());
				entity.yRotO = entity.getYRot();
				entity.xRotO = entity.getXRot();
				if (entity instanceof LivingEntity _entity) {
					_entity.yBodyRotO = _entity.getYRot();
					_entity.yHeadRotO = _entity.getYRot();
				}

				double _setval = GrowlingManModVariables.of(entity)
						.ScreenShakeTimer
					- 1.0;
				{
					GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
					_vars.ScreenShakeTimer = _setval;
					_vars.syncPlayerVariables(entity);
				}
			}

			if (!(new Object() {
						public boolean checkGamemode(Entity _ent) {
							if (_ent instanceof ServerPlayer _serverPlayer) {
								return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
							} else {
								return _ent.level().isClientSide() && _ent instanceof Player _player
									? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
										&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE
									: false;
							}
						}
					})
					.checkGamemode(entity)
				&& !(new Object() {
						public boolean checkGamemode(Entity _ent) {
							if (_ent instanceof ServerPlayer _serverPlayer) {
								return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
							} else {
								return _ent.level().isClientSide() && _ent instanceof Player _player
									? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
										&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR
									: false;
							}
						}
					})
					.checkGamemode(entity)) {
				if (GrowlingManModVariables.of(entity)
						.IsHoldingBreath
					&& GrowlingManModVariables.of(entity)
							.HoldingBreathTimer
						< 150.0
					&& !(entity instanceof LivingEntity _livEnt148 && _livEnt148.hasEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder()))) {
					entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x() / 1.4, entity.getDeltaMovement().y(), entity.getDeltaMovement().z() / 1.4));
					double _setval = GrowlingManModVariables.of(entity)
							.HoldingBreathTimer
						+ 1.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.HoldingBreathTimer = _setval;
						_vars.syncPlayerVariables(entity);
					}
				}

				if (GrowlingManModVariables.of(entity)
						.IsHoldingBreath
					&& GrowlingManModVariables.of(entity)
							.HoldingBreathTimer
						>= 150.0) {
					double _setval = 0.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.HoldingBreathTimer = _setval;
						_vars.syncPlayerVariables(entity);
					}
					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
						_entity.addEffect(new MobEffectInstance(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder(), 300, 0, false, false));
					}

					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
						_entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 300, 0, false, false));
					}
				}
			}

			if (!GrowlingManModVariables.of(entity)
					.IsHoldingBreath
				&& GrowlingManModVariables.of(entity)
						.HoldingBreathTimer
					> 0.0) {
				if (GrowlingManModVariables.of(entity)
						.HoldingBreathTimer
					>= 15.0) {
					double _setvalx = GrowlingManModVariables.of(entity)
							.HoldingBreathTimer
						- 5.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.HoldingBreathTimer = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				} else if (GrowlingManModVariables.of(entity)
							.HoldingBreathTimer
						< 15.0
					&& GrowlingManModVariables.of(entity)
							.HoldingBreathTimer
						> 0.0) {
					double _setvalx = GrowlingManModVariables.of(entity)
							.HoldingBreathTimer
						- 1.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.HoldingBreathTimer = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				}
			}

			if (GrowlingManModVariables.of(entity)
						.IsMarked
					> 0.0
				&& GrowlingManModVariables.of(entity)
						.IsMarked
					< 144000.0) {
				if (GrowlingManModVariables.of(entity)
							.IsMarked
						== 2.0
					&& !world.isClientSide()) {
					if (world instanceof Level _levelxxxxxxxx) {
						if (!_levelxxxxxxxx.isClientSide()) {
							_levelxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.marked")),
								SoundSource.PLAYERS,
								1.0F,
								0.8F
							);
						} else {
							_levelxxxxxxxx.playLocalSound(
								x,
								y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.marked")),
								SoundSource.PLAYERS,
								1.0F,
								0.8F,
								false
							);
						}
					}

					if (world instanceof Level _levelxxxxxxxxx) {
						if (!_levelxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.2F
							);
						} else {
							_levelxxxxxxxxx.playLocalSound(
								x,
								y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.kills_too_much")),
								SoundSource.PLAYERS,
								1.0F,
								0.2F,
								false
							);
						}
					}
				}

				double _setvalx = GrowlingManModVariables.of(entity)
						.IsMarked
					+ 1.0;
				{
					GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
					_vars.IsMarked = _setvalx;
					_vars.syncPlayerVariables(entity);
				}
				if (!(
						entity instanceof ServerPlayer _plr162
							&& _plr162.level() instanceof ServerLevel
							&& _plr162.getAdvancements().getOrStartProgress(_plr162.server.getAdvancements().get(ResourceLocation.parse("growling_man:marked_for_death"))).isDone()
					)
					&& entity instanceof ServerPlayer _player) {
					AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("growling_man:marked_for_death"));
					AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
					if (!_ap.isDone()) {
						for (String criteria : _ap.getRemainingCriteria()) {
							_player.getAdvancements().award(_adv, criteria);
						}
					}
				}
			}

			if (!world.isClientSide()) {
				if (GrowlingManModVariables.of(entity)
						.HoldingBreathTimer
					> 0.0) {
					double _setvalx = GrowlingManModVariables.of(entity)
						.HoldingBreathTimer;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.cym_overlay_opacity = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				} else if (GrowlingManModVariables.of(entity)
						.cym_overlay_opacity
					> 25.0) {
					double _setvalx = GrowlingManModVariables.of(entity)
							.cym_overlay_opacity
						- 25.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.cym_overlay_opacity = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				} else if (GrowlingManModVariables.of(entity)
							.cym_overlay_opacity
						> 0.0
					&& GrowlingManModVariables.of(entity)
							.cym_overlay_opacity
						< 25.0) {
					double _setvalx = GrowlingManModVariables.of(entity)
							.cym_overlay_opacity
						- 1.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.cym_overlay_opacity = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				} else if (GrowlingManModVariables.of(entity)
						.cym_overlay_opacity
					< 0.0) {
					double _setvalx = 0.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.cym_overlay_opacity = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				}

				if (entity instanceof LivingEntity _livEnt165
					&& _livEnt165.hasEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder())
					&& (
							entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder())
								? _livEnt.getEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder()).getDuration()
								: 0
						)
						> 150
					&& GrowlingManModVariables.of(entity)
							.mtl_overlay_opacity
						< 150.0) {
					double _setvalx = GrowlingManModVariables.of(entity)
							.mtl_overlay_opacity
						+ 25.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.mtl_overlay_opacity = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				} else if (entity instanceof LivingEntity _livEnt167
					&& _livEnt167.hasEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder())
					&& (
							entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder())
								? _livEnt.getEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder()).getDuration()
								: 0
						)
						<= 150
					&& GrowlingManModVariables.of(entity)
							.mtl_overlay_opacity
						> 0.0) {
					double _setvalx = GrowlingManModVariables.of(entity)
							.mtl_overlay_opacity
						- 1.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.mtl_overlay_opacity = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				} else if (GrowlingManModVariables.of(entity)
							.mtl_overlay_opacity
						!= 0.0
					&& !(entity instanceof LivingEntity _livEnt169 && _livEnt169.hasEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder()))) {
					double _setvalx = 0.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.mtl_overlay_opacity = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				}

				if (Math.random() > 0.6) {
					if (InventoryScreenMessage.isOpen(entity)
						&& (
							GrowlingManModVariables.of(entity)
											.IsMarked
										== 0.0
									&& Math.random() > 0.9
									&& GrowlingManModVariables.of(entity)
											.gm_inv_ap
										== 0.0
								|| GrowlingManModVariables.of(entity)
											.IsMarked
										> 0.0
									&& Math.random() > 0.7
									&& GrowlingManModVariables.of(entity)
											.gm_inv_ap
										== 0.0
						)
						&& world.isEmptyBlock(BlockPos.containing(x + entity.getLookAngle().x * 9.0, y + 0.0, z + entity.getLookAngle().z * 9.0))
						&& world.isEmptyBlock(BlockPos.containing(x + entity.getLookAngle().x * 9.0, y + 1.0, z + entity.getLookAngle().z * 9.0))
						&& GrowlingManModVariables.of(entity)
								.InvNumb
							== 1.0) {
						double _setvalx = 10.0;
						{
							GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
							_vars.gm_inv_ap = _setvalx;
							_vars.syncPlayerVariables(entity);
						}
						if (world instanceof ServerLevel _levelxxxxxxxxxx) {
							Entity entityToSpawn = ((EntityType)GrowlingManModEntities.GROWLING_MAN.get())
								.spawn(
									_levelxxxxxxxxxx,
									BlockPos.containing(x + entity.getLookAngle().x * 9.0, y, z + entity.getLookAngle().z * 9.0),
									MobSpawnType.MOB_SUMMONED
								);
							if (entityToSpawn != null) {
								entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
							}
						}

						Vec3 _center = new Vec3(x + entity.getLookAngle().x * 9.0, y, z + entity.getLookAngle().z * 9.0);

						for (Entity entityiteratorxxxxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.0), e -> true)
							.stream()
							.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
							.toList()) {
							if (entityiteratorxxxxx instanceof GrowlingManEntity) {
								if (entityiteratorxxxxx instanceof GrowlingManEntity _datEntSetS) {
									_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_GhostTarget, entity.getDisplayName().getString());
								}

								if (entityiteratorxxxxx instanceof GrowlingManEntity _datEntSetI) {
									_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_Transparency, 360);
								}

								if (entityiteratorxxxxx instanceof GrowlingManEntity _datEntSetI) {
									_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_AI, 18);
								}

								entityiteratorxxxxx.lookAt(Anchor.EYES, new Vec3(x, y, z));
								break;
							}
						}
					}
				} else if (InventoryScreenMessage.isOpen(entity)
					&& (
						GrowlingManModVariables.of(entity)
										.IsMarked
									== 0.0
								&& Math.random() > 0.9
								&& GrowlingManModVariables.of(entity)
										.gm_inv_opacity
									== 0.0
							|| GrowlingManModVariables.of(entity)
										.IsMarked
									> 0.0
								&& Math.random() > 0.7
								&& GrowlingManModVariables.of(entity)
										.gm_inv_opacity
									== 0.0
					)
					&& world.isEmptyBlock(
						BlockPos.containing(
							x - entity.getLookAngle().z * 2.0 + entity.getLookAngle().x * 4.0,
							y + 0.0,
							z + entity.getLookAngle().x * 2.0 + entity.getLookAngle().z * 4.0
						)
					)
					&& world.isEmptyBlock(
						BlockPos.containing(
							x - entity.getLookAngle().z * 2.0 + entity.getLookAngle().x * 4.0,
							y + 1.0,
							z + entity.getLookAngle().x * 2.0 + entity.getLookAngle().z * 4.0
						)
					)
					&& GrowlingManModVariables.of(entity)
							.InvNumb
						== 1.0) {
					double _setvalx = 1.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.gm_inv_opacity = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
					if (world instanceof ServerLevel _levelxxxxxxxxxxx) {
						Entity entityToSpawn = ((EntityType)GrowlingManModEntities.GROWLING_MAN.get())
							.spawn(
								_levelxxxxxxxxxxx,
								BlockPos.containing(
									x - entity.getLookAngle().z * 2.0 + entity.getLookAngle().x * 4.0,
									y,
									z + entity.getLookAngle().x * 2.0 + entity.getLookAngle().z * 4.0
								),
								MobSpawnType.MOB_SUMMONED
							);
						if (entityToSpawn != null) {
							entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
						}
					}

					Vec3 _center = new Vec3(
						x - entity.getLookAngle().z * 2.0 + entity.getLookAngle().x * 4.0,
						y,
						z + entity.getLookAngle().x * 2.0 + entity.getLookAngle().z * 4.0
					);

					for (Entity entityiteratorxxxxxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.0), e -> true)
						.stream()
						.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
						.toList()) {
						if (entityiteratorxxxxxx instanceof GrowlingManEntity) {
							if (entityiteratorxxxxxx instanceof GrowlingManEntity _datEntSetS) {
								_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_GhostTarget, entity.getDisplayName().getString());
							}

							if (entityiteratorxxxxxx instanceof GrowlingManEntity _datEntSetI) {
								_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_AI, 17);
							}

							entityiteratorxxxxxx.lookAt(Anchor.EYES, new Vec3(x, y, z));
							break;
						}
					}
				}

				if (InventoryScreenMessage.isOpen(entity)
					&& GrowlingManModVariables.of(entity)
							.InvNumb
						< 3.0) {
					double _setvalx = GrowlingManModVariables.of(entity)
							.InvNumb
						+ 1.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.InvNumb = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				} else if (!(InventoryScreenMessage.isOpen(entity))
					&& GrowlingManModVariables.of(entity)
							.InvNumb
						> 0.0) {
					double _setvalx = 0.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.InvNumb = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				}

				if (!(InventoryScreenMessage.isOpen(entity))
					&& GrowlingManModVariables.of(entity)
							.gm_inv_ap
						> 0.0) {
					double _setvalx = GrowlingManModVariables.of(entity)
							.gm_inv_ap
						- 1.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.gm_inv_ap = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				}

				if (InventoryScreenMessage.isOpen(entity)
					&& GrowlingManModVariables.of(entity)
							.gm_inv_opacity
						< 600.0
					&& GrowlingManModVariables.of(entity)
							.gm_inv_opacity
						> 0.0) {
					double _setvalx = GrowlingManModVariables.of(entity)
							.gm_inv_opacity
						+ 1.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.gm_inv_opacity = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				} else if (!(InventoryScreenMessage.isOpen(entity))
					&& GrowlingManModVariables.of(entity)
							.gm_inv_opacity
						!= 0.0) {
					double _setvalx = 0.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.gm_inv_opacity = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				}

				if (InventoryScreenMessage.isOpen(entity)
					&& GrowlingManModVariables.of(entity)
							.gm_inv_opacity
						>= 300.0) {
					if (entity instanceof Player _playerx) {
						_playerx.closeContainer();
					}

					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
						_entity.addEffect(new MobEffectInstance(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder(), 70, 0, false, false));
					}

					double _setvalx = 0.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.gm_inv_opacity = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
					if ((world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD))
							== Level.OVERWORLD
						&& !(new Object() {
								public boolean checkGamemode(Entity _ent) {
									if (_ent instanceof ServerPlayer _serverPlayer) {
										return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
									} else {
										return _ent.level().isClientSide() && _ent instanceof Player _player
											? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
												&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE
											: false;
									}
								}
							})
							.checkGamemode(entity)
						&& !(new Object() {
								public boolean checkGamemode(Entity _ent) {
									if (_ent instanceof ServerPlayer _serverPlayer) {
										return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
									} else {
										return _ent.level().isClientSide() && _ent instanceof Player _player
											? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
												&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR
											: false;
									}
								}
							})
							.checkGamemode(entity)) {
						if (world.getEntitiesOfClass(GrowlingManEntity.class, AABB.ofSize(new Vec3(x, y, z), 600.0, 600.0, 600.0), e -> true).isEmpty()) {
							if (world.getEntitiesOfClass(GrowlingManEntity.class, AABB.ofSize(new Vec3(x, y, z), 600.0, 600.0, 600.0), e -> true).isEmpty()) {
								spawnx = Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -30, -10) : Mth.nextInt(RandomSource.create(), 10, 30);
								spawnz = Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -30, -10) : Mth.nextInt(RandomSource.create(), 10, 30);
							}

							if (!world.getBlockState(
										BlockPos.containing(
											x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) - 1, z + spawnz
										)
									)
									.canOcclude()
								&& !(
									world.getBlockState(
											BlockPos.containing(
												x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) - 1, z + spawnz
											)
										)
										.getBlock() instanceof LiquidBlock
								)) {
								YRepeater = 0.0;

								label907:
								for (int index3 = 0; index3 < 32; index3++) {
									if (world.getBlockState(
											BlockPos.containing(
												x + spawnx,
												world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + YRepeater - 1.0,
												z + spawnz
											)
										)
										.canOcclude()) {
										if (!world.getBlockState(
													BlockPos.containing(
														x + spawnx,
														world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + YRepeater + 0.0,
														z + spawnz
													)
												)
												.canOcclude()
											&& !world.getBlockState(
													BlockPos.containing(
														x + spawnx,
														world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + YRepeater + 1.0,
														z + spawnz
													)
												)
												.canOcclude()
											&& !world.getBlockState(
													BlockPos.containing(
														x + spawnx,
														world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + YRepeater + 2.0,
														z + spawnz
													)
												)
												.canOcclude()) {
											if (world instanceof ServerLevel _levelxxxxxxxxxxxx) {
												Entity entityToSpawn = ((EntityType)GrowlingManModEntities.GROWLING_MAN.get())
													.spawn(
														_levelxxxxxxxxxxxx,
														BlockPos.containing(
															x + spawnx,
															world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + YRepeater,
															z + spawnz
														),
														MobSpawnType.MOB_SUMMONED
													);
												if (entityToSpawn != null) {
													entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
												}
											}

											Vec3 _center = new Vec3(
												x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + YRepeater, z + spawnz
											);

											for (Entity entityiteratorxxxxxxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.0), e -> true)
												.stream()
												.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
												.toList()) {
												if (entityiteratorxxxxxxx instanceof GrowlingManEntity) {
													if (entityiteratorxxxxxxx instanceof GrowlingManEntity _datEntSetI) {
														_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_AI, Mth.nextInt(RandomSource.create(), 1, 3));
													}
													break label907;
												}
											}
											break;
										}
									} else {
										YRepeater--;
									}
								}
							} else if (!world.getBlockState(
										BlockPos.containing(
											x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + 0, z + spawnz
										)
									)
									.canOcclude()
								&& !world.getBlockState(
										BlockPos.containing(
											x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + 1, z + spawnz
										)
									)
									.canOcclude()
								&& !world.getBlockState(
										BlockPos.containing(
											x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + 2, z + spawnz
										)
									)
									.canOcclude()
								&& world.getBlockState(
										BlockPos.containing(
											x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) - 1, z + spawnz
										)
									)
									.canOcclude()) {
								if (world instanceof ServerLevel _levelxxxxxxxxxxxxx) {
									Entity entityToSpawn = ((EntityType)GrowlingManModEntities.GROWLING_MAN.get())
										.spawn(
											_levelxxxxxxxxxxxxx,
											BlockPos.containing(
												x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)), z + spawnz
											),
											MobSpawnType.MOB_SUMMONED
										);
									if (entityToSpawn != null) {
										entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
									}
								}

								Vec3 _center = new Vec3(x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)), z + spawnz);

								for (Entity entityiteratorxxxxxxxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.0), e -> true)
									.stream()
									.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
									.toList()) {
									if (entityiteratorxxxxxxxx instanceof GrowlingManEntity) {
										if (entityiteratorxxxxxxxx instanceof GrowlingManEntity _datEntSetI) {
											_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_AI, Mth.nextInt(RandomSource.create(), 1, 3));
										}
										break;
									}
								}
							}
						}

						if (GrowlingManModVariables.of(entity)
								.GMEventsTimer
							<= 0.0) {
							double _setvalxx = 12000.0;
							{
								GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
								_vars.GMEventsTimer = _setvalxx;
								_vars.syncPlayerVariables(entity);
							}
						}

						if (GrowlingManModVariables.of(entity)
								.GMEventsTimer
							> 0.0) {
							double _setvalxx = GrowlingManModVariables.of(entity)
									.GMEventsTimer
								- 1.0;
							{
								GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
								_vars.GMEventsTimer = _setvalxx;
								_vars.syncPlayerVariables(entity);
							}
						}
					}
				}

				if (GrowlingManModVariables.of(entity)
						.AnimalsKilledResetWait
					> 0.0) {
					double _setvalx = GrowlingManModVariables.of(entity)
							.AnimalsKilledResetWait
						- 1.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.AnimalsKilledResetWait = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				}

				if (GrowlingManModVariables.of(entity)
						.BadActionTimer
					> 0.0) {
					double _setvalx = GrowlingManModVariables.of(entity)
							.BadActionTimer
						- 1.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.BadActionTimer = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				}

				if (GrowlingManModVariables.of(entity)
							.BadActionTimer
						== 0.0
					&& (
						GrowlingManModVariables.of(entity)
									.IsMarked
								== 1.0
							|| GrowlingManModVariables.of(entity)
									.IsMarked
								== 2.0
					)) {
					double _setvalx = 96000.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.BadActionTimer = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				}

				if (GrowlingManModVariables.of(entity)
							.BadActionTimer
						== 0.0
					&& GrowlingManModVariables.of(entity)
							.IsMarked
						> 2.0) {
					double _setvalx = 0.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.IsMarked = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
					if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie_villager.converted")),
								SoundSource.PLAYERS,
								0.7F,
								0.8F
							);
						} else {
							_levelxxxxxxxxxxxxxx.playLocalSound(
								x,
								y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie_villager.converted")),
								SoundSource.PLAYERS,
								0.7F,
								0.8F,
								false
							);
						}
					}
				}

				if (GrowlingManModVariables.of(entity)
							.AnimalsKilledResetWait
						== 0.0
					&& GrowlingManModVariables.of(entity)
							.AnimalsKilled
						> 0.0) {
					double _setvalx = 0.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.AnimalsKilled = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				}

				if (GrowlingManModVariables.of(entity)
							.AnimalsKilledResetWait
						<= 6200.0
					&& GrowlingManModVariables.of(entity)
							.AnimalsKilledShortTime
						> 0.0) {
					double _setvalx = 0.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.AnimalsKilledShortTime = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				}

				if (world instanceof ServerLevel _level265 && _level265.isRaided(BlockPos.containing(x, y, z))) {
					if (GrowlingManModVariables.of(entity)
							.IsMarked
						== 0.0) {
						double _setvalx = 1.0;
						{
							GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
							_vars.IsMarked = _setvalx;
							_vars.syncPlayerVariables(entity);
						}
					}

					if (GrowlingManModVariables.of(entity)
								.RaidTime
							< 24000.0
						&& world.getEntitiesOfClass(GrowlingManEntity.class, AABB.ofSize(new Vec3(x, y, z), 200.0, 200.0, 200.0), e -> true).isEmpty()) {
						double _setvalx = GrowlingManModVariables.of(entity)
								.RaidTime
							+ 1.0;
						{
							GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
							_vars.RaidTime = _setvalx;
							_vars.syncPlayerVariables(entity);
						}
					}

					if (GrowlingManModVariables.of(entity)
							.RaidTime
						>= 24000.0) {
						double _setvalx = 0.0;
						{
							GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
							_vars.RaidTime = _setvalx;
							_vars.syncPlayerVariables(entity);
						}
						if ((world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD))
								== Level.OVERWORLD
							&& !(new Object() {
									public boolean checkGamemode(Entity _ent) {
										if (_ent instanceof ServerPlayer _serverPlayer) {
											return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
										} else {
											return _ent.level().isClientSide() && _ent instanceof Player _player
												? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
													&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE
												: false;
										}
									}
								})
								.checkGamemode(entity)
							&& !(new Object() {
									public boolean checkGamemode(Entity _ent) {
										if (_ent instanceof ServerPlayer _serverPlayer) {
											return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
										} else {
											return _ent.level().isClientSide() && _ent instanceof Player _player
												? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
													&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR
												: false;
										}
									}
								})
								.checkGamemode(entity)) {
							if (world.getEntitiesOfClass(GrowlingManEntity.class, AABB.ofSize(new Vec3(x, y, z), 600.0, 600.0, 600.0), e -> true).isEmpty()) {
								if (world.getEntitiesOfClass(GrowlingManEntity.class, AABB.ofSize(new Vec3(x, y, z), 600.0, 600.0, 600.0), e -> true).isEmpty()) {
									spawnx = Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -30, -10) : Mth.nextInt(RandomSource.create(), 10, 30);
									spawnz = Math.random() > 0.6 ? Mth.nextInt(RandomSource.create(), -30, -10) : Mth.nextInt(RandomSource.create(), 10, 30);
								}

								if (!world.getBlockState(
											BlockPos.containing(
												x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) - 1, z + spawnz
											)
										)
										.canOcclude()
									&& !(
										world.getBlockState(
												BlockPos.containing(
													x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) - 1, z + spawnz
												)
											)
											.getBlock() instanceof LiquidBlock
									)) {
									YRepeater = 0.0;

									label842:
									for (int index4 = 0; index4 < 32; index4++) {
										if (world.getBlockState(
												BlockPos.containing(
													x + spawnx,
													world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + YRepeater - 1.0,
													z + spawnz
												)
											)
											.canOcclude()) {
											if (!world.getBlockState(
														BlockPos.containing(
															x + spawnx,
															world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + YRepeater + 0.0,
															z + spawnz
														)
													)
													.canOcclude()
												&& !world.getBlockState(
														BlockPos.containing(
															x + spawnx,
															world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + YRepeater + 1.0,
															z + spawnz
														)
													)
													.canOcclude()
												&& !world.getBlockState(
														BlockPos.containing(
															x + spawnx,
															world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + YRepeater + 2.0,
															z + spawnz
														)
													)
													.canOcclude()) {
												if (world instanceof ServerLevel _levelxxxxxxxxxxxxxxx) {
													Entity entityToSpawn = ((EntityType)GrowlingManModEntities.GROWLING_MAN.get())
														.spawn(
															_levelxxxxxxxxxxxxxxx,
															BlockPos.containing(
																x + spawnx,
																world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + YRepeater,
																z + spawnz
															),
															MobSpawnType.MOB_SUMMONED
														);
													if (entityToSpawn != null) {
														entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
													}
												}

												Vec3 _center = new Vec3(
													x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + YRepeater, z + spawnz
												);

												for (Entity entityiteratorxxxxxxxxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.0), e -> true)
													.stream()
													.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
													.toList()) {
													if (entityiteratorxxxxxxxxx instanceof GrowlingManEntity) {
														if (entityiteratorxxxxxxxxx instanceof GrowlingManEntity _datEntSetI) {
															_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_AI, Mth.nextInt(RandomSource.create(), 1, 3));
														}
														break label842;
													}
												}
												break;
											}
										} else {
											YRepeater--;
										}
									}
								} else if (!world.getBlockState(
											BlockPos.containing(
												x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + 0, z + spawnz
											)
										)
										.canOcclude()
									&& !world.getBlockState(
											BlockPos.containing(
												x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + 1, z + spawnz
											)
										)
										.canOcclude()
									&& !world.getBlockState(
											BlockPos.containing(
												x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) + 2, z + spawnz
											)
										)
										.canOcclude()
									&& world.getBlockState(
											BlockPos.containing(
												x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)) - 1, z + spawnz
											)
										)
										.canOcclude()) {
									if (world instanceof ServerLevel _levelxxxxxxxxxxxxxxxx) {
										Entity entityToSpawn = ((EntityType)GrowlingManModEntities.GROWLING_MAN.get())
											.spawn(
												_levelxxxxxxxxxxxxxxxx,
												BlockPos.containing(
													x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)), z + spawnz
												),
												MobSpawnType.MOB_SUMMONED
											);
										if (entityToSpawn != null) {
											entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
										}
									}

									Vec3 _center = new Vec3(x + spawnx, world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + spawnx), (int)(z + spawnz)), z + spawnz);

									for (Entity entityiteratorxxxxxxxxxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.0), e -> true)
										.stream()
										.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
										.toList()) {
										if (entityiteratorxxxxxxxxxx instanceof GrowlingManEntity) {
											if (entityiteratorxxxxxxxxxx instanceof GrowlingManEntity _datEntSetI) {
												_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_AI, Mth.nextInt(RandomSource.create(), 1, 3));
											}
											break;
										}
									}
								}
							}

							if (GrowlingManModVariables.of(entity)
									.GMEventsTimer
								<= 0.0) {
								double _setvalxx = 12000.0;
								{
									GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
									_vars.GMEventsTimer = _setvalxx;
									_vars.syncPlayerVariables(entity);
								}
							}

							if (GrowlingManModVariables.of(entity)
									.GMEventsTimer
								> 0.0) {
								double _setvalxx = GrowlingManModVariables.of(entity)
										.GMEventsTimer
									- 1.0;
								{
									GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
									_vars.GMEventsTimer = _setvalxx;
									_vars.syncPlayerVariables(entity);
								}
							}
						}
					}
				} else if (!(world instanceof ServerLevel _level313 && _level313.isRaided(BlockPos.containing(x, y, z)))
					&& GrowlingManModVariables.of(entity)
							.RaidTime
						> 0.0) {
					double _setvalx = GrowlingManModVariables.of(entity)
							.RaidTime
						- 1.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.RaidTime = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				}

				if (world.getLevelData().getGameTime() % 24000L == 23999L
					&& GrowlingManModVariables.of(entity)
							.IsMarked
						== 0.0
					&& Math.random() > 0.87) {
					double _setvalx = 1.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.IsMarked = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				}

				if (world.getLevelData().getGameTime() % 24000L == 23999L
					&& GrowlingManModVariables.of(entity)
							.BadActionTimer
						!= 0.0
					&& Math.random() > 0.9) {
					double _setvalx = 0.0;
					{
						GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
						_vars.BadActionTimer = _setvalx;
						_vars.syncPlayerVariables(entity);
					}
				}
			}

			if (world.isClientSide()) {
				if (GrowlingManModVariables.of(entity)
							.HoldingBreathTimer
						== 149.0
					&& world instanceof Level _levelxxxxxxxxxxxxxxxxx) {
					if (!_levelxxxxxxxxxxxxxxxxx.isClientSide()) {
						_levelxxxxxxxxxxxxxxxxx.playSound(
							null,
							BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.gm_clear_mind_fail")),
							SoundSource.PLAYERS,
							1.0F,
							(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0)
						);
					} else {
						_levelxxxxxxxxxxxxxxxxx.playLocalSound(
							x,
							y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
							z,
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.gm_clear_mind_fail")),
							SoundSource.PLAYERS,
							1.0F,
							(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0),
							false
						);
					}
				}

				if (GrowlingManModVariables.of(entity)
						.IsHoldingBreath
					&& GrowlingManModVariables.of(entity)
								.HoldingBreathTimer
							% 21.0
						== 1.0
					&& world instanceof Level _levelxxxxxxxxxxxxxxxxxx) {
					if (!_levelxxxxxxxxxxxxxxxxxx.isClientSide()) {
						_levelxxxxxxxxxxxxxxxxxx.playSound(
							null,
							BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.gm_mental_breakdown")),
							SoundSource.PLAYERS,
							(float)(
								GrowlingManModVariables.of(entity)
										.HoldingBreathTimer
									/ 150.0
							),
							1.0F
						);
					} else {
						_levelxxxxxxxxxxxxxxxxxx.playLocalSound(
							x,
							y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
							z,
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.gm_mental_breakdown")),
							SoundSource.PLAYERS,
							(float)(
								GrowlingManModVariables.of(entity)
										.HoldingBreathTimer
									/ 150.0
							),
							1.0F,
							false
						);
					}
				}

				if (GrowlingManModVariables.of(entity)
						.gm_inv_opacity
					> 0.0) {
					if (world.getLevelData().getGameTime() % 8L == 1L
						&& GrowlingManModVariables.of(entity)
								.gm_inv_opacity
							> 250.0) {
						if (world instanceof Level _levelxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.gm_heartbeat_fast")),
									SoundSource.PLAYERS,
									(float)(
										GrowlingManModVariables.of(entity)
												.gm_inv_opacity
											/ 2000.0
									),
									1.0F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.gm_heartbeat_fast")),
									SoundSource.PLAYERS,
									(float)(
										GrowlingManModVariables.of(entity)
												.gm_inv_opacity
											/ 2000.0
									),
									1.0F,
									false
								);
							}
						}
					} else if (world.getLevelData().getGameTime() % 13L == 1L
						&& GrowlingManModVariables.of(entity)
								.gm_inv_opacity
							> 150.0
						&& GrowlingManModVariables.of(entity)
								.gm_inv_opacity
							<= 250.0) {
						if (world instanceof Level _levelxxxxxxxxxxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.heartbeat_med")),
									SoundSource.PLAYERS,
									(float)(
										GrowlingManModVariables.of(entity)
												.gm_inv_opacity
											/ 2000.0
									),
									1.0F
								);
							} else {
								_levelxxxxxxxxxxxxxxxxxxxx.playLocalSound(
									x,
									y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.heartbeat_med")),
									SoundSource.PLAYERS,
									(float)(
										GrowlingManModVariables.of(entity)
												.gm_inv_opacity
											/ 2000.0
									),
									1.0F,
									false
								);
							}
						}
					} else if (world.getLevelData().getGameTime() % 20L == 1L
						&& GrowlingManModVariables.of(entity)
								.gm_inv_opacity
							<= 150.0
						&& world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.gm_heartbeat_slow")),
								SoundSource.PLAYERS,
								(float)(
									GrowlingManModVariables.of(entity)
											.gm_inv_opacity
										/ 2000.0
								),
								1.0F
							);
						} else {
							_levelxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
								x,
								y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.gm_heartbeat_slow")),
								SoundSource.PLAYERS,
								(float)(
									GrowlingManModVariables.of(entity)
											.gm_inv_opacity
										/ 2000.0
								),
								1.0F,
								false
							);
						}
					}
				}
			}
		}
	}
}
