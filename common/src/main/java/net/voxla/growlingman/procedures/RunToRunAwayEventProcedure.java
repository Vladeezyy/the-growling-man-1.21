package net.voxla.growlingman.procedures;

import net.voxla.growlingman.port.GMEntities;
import java.util.Comparator;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.BuiltInRegistries;
import net.voxla.growlingman.entity.GrowlingManEntity;
import net.voxla.growlingman.init.GrowlingManModMobEffects;
import net.voxla.growlingman.network.GrowlingManModVariables;

public class RunToRunAwayEventProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity != null) {
			if ((entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent) : 0) < 3) {
				Vec3 _center = new Vec3(x, y, z);

				for (Entity entityiterator : GMEntities.playersAsEntities(world, new AABB(_center, _center).inflate(162.5))
					.stream()
					.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
					.toList()) {
					if (entityiterator instanceof Player
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
							.checkGamemode(entityiterator)
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
							.checkGamemode(entityiterator)) {
						if (GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 24.0, 24.0, 24.0)).isEmpty() && entity instanceof Mob _entity) {
							_entity.getNavigation().moveTo(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ(), 3.0);
						}

						if (!GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 25.0, 25.0, 25.0)).isEmpty()
							&& (entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent) : 0)
								< 3
							&& entity instanceof GrowlingManEntity _datEntSetI) {
							_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_RunAtRunAwayEvent, 4);
						}
						break;
					}
				}
			} else if ((entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent) : 0) > 3
				&& (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent) : 0) < 150) {
				if (entity instanceof GrowlingManEntity _datEntSetI) {
					_datEntSetI.getEntityData()
						.set(
							GrowlingManEntity.DATA_RunAtRunAwayEvent,
							(entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent) : 0)
								+ 1
						);
				}

				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
					_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 3, 250, false, false));
				}

				if (world.getLevelData().getGameTime() % 32L == 1L && !world.isClientSide() && world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(
							null,
							BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.growling_man.erratic")),
							SoundSource.HOSTILE,
							1.0F,
							(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0)
						);
					} else {
						_level.playLocalSound(
							x,
							y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
							z,
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.growling_man.erratic")),
							SoundSource.HOSTILE,
							1.0F,
							(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0),
							false
						);
					}
				}

				if (world.getLevelData().getGameTime() % 9L == 1L && !world.isClientSide() && world instanceof Level _levelx) {
					if (!_levelx.isClientSide()) {
						_levelx.playSound(
							null,
							BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.growling_man.sporadic")),
							SoundSource.HOSTILE,
							1.0F,
							(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0)
						);
					} else {
						_levelx.playLocalSound(
							x,
							y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
							z,
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.growling_man.sporadic")),
							SoundSource.HOSTILE,
							1.0F,
							(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0),
							false
						);
					}
				}

				if (world.getLevelData().getGameTime() % 21L == 1L && !world.isClientSide() && world instanceof Level _levelxx) {
					if (!_levelxx.isClientSide()) {
						_levelxx.playSound(
							null,
							BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.growling_man.sporadic_limbs")),
							SoundSource.HOSTILE,
							1.0F,
							(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0)
						);
					} else {
						_levelxx.playLocalSound(
							x,
							y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
							z,
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.growling_man.sporadic_limbs")),
							SoundSource.HOSTILE,
							1.0F,
							(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0),
							false
						);
					}
				}

				if (world.getLevelData().getGameTime() % 37L == 1L && !world.isClientSide() && world instanceof Level _levelxxx) {
					if (!_levelxxx.isClientSide()) {
						_levelxxx.playSound(
							null,
							BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.growling_man.growl_main")),
							SoundSource.HOSTILE,
							1.0F,
							(float)Mth.nextDouble(RandomSource.create(), 0.8, 1.0)
						);
					} else {
						_levelxxx.playLocalSound(
							x,
							y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
							z,
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.growling_man.growl_main")),
							SoundSource.HOSTILE,
							1.0F,
							(float)Mth.nextDouble(RandomSource.create(), 0.8, 1.0),
							false
						);
					}
				}

				if ((entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent) : 0)
						> 23
					&& (entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent) : 0)
						< 150) {
					Vec3 _center = new Vec3(x, y, z);

					for (Entity entityiteratorx : GMEntities.playersAsEntities(world, new AABB(_center, _center).inflate(15.0))
						.stream()
						.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
						.toList()) {
						if (entityiteratorx instanceof Player
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
								.checkGamemode(entityiteratorx)
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
								.checkGamemode(entityiteratorx)
							&& GrowlingManModVariables.of(entityiteratorx)
									.HoldingBreathTimer
								< 2.0
							&& entity instanceof GrowlingManEntity _datEntL47
							&& (Boolean)_datEntL47.getEntityData().get(GrowlingManEntity.DATA_CanSeePlayer)) {
							if ((
										entity instanceof GrowlingManEntity _datEntIxxxx
											? (Integer)_datEntIxxxx.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent)
											: 0
									)
									!= 400
								&& entity instanceof GrowlingManEntity _datEntSetI) {
								_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_RunAtRunAwayEvent, 400);
							}
							break;
						}
					}
				}

				if (!world.isClientSide()
					&& GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 600.0, 600.0, 600.0)).stream().sorted((new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}).compareDistOf(x, y, z)).findFirst().orElse(null) != null) {
					entity.lookAt(
						Anchor.EYES,
						new Vec3(
							GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 600.0, 600.0, 600.0)).stream().sorted((new Object() {
								Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
									return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
								}
							}).compareDistOf(x, y, z)).findFirst().orElse(null).getX(),
							GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 600.0, 600.0, 600.0)).stream().sorted((new Object() {
								Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
									return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
								}
							}).compareDistOf(x, y, z)).findFirst().orElse(null).getY() + 1.7,
							GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 600.0, 600.0, 600.0)).stream().sorted((new Object() {
								Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
									return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
								}
							}).compareDistOf(x, y, z)).findFirst().orElse(null).getZ()
						)
					);
				}
			} else if ((entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent) : 0)
					>= 150
				&& (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent) : 0) < 300) {
				Vec3 _center = new Vec3(x, y, z);

				for (Entity entityiteratorxx : GMEntities.playersAsEntities(world, new AABB(_center, _center).inflate(40.0))
					.stream()
					.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
					.toList()) {
					if (entityiteratorxx instanceof Player
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
							.checkGamemode(entityiteratorxx)
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
							.checkGamemode(entityiteratorxx)
						&& entity instanceof LivingEntity _livEnt66
						&& _livEnt66.hasEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder())
						&& entity instanceof GrowlingManEntity _datEntL67
						&& (Boolean)_datEntL67.getEntityData().get(GrowlingManEntity.DATA_CanSeePlayer)) {
						if ((entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent) : 0)
							!= 400) {
							if (entity instanceof Mob _entity) {
								_entity.getNavigation().stop();
							}

							if (entity instanceof GrowlingManEntity _datEntSetI) {
								_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_RunAtRunAwayEvent, 400);
							}
						}
						break;
					}
				}

				if (entity instanceof GrowlingManEntity _datEntL72 && (Boolean)_datEntL72.getEntityData().get(GrowlingManEntity.DATA_PlayerEast)) {
					if ((entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_RunToX) : 0) == 0
						&& entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_RunToX, (int)(x - 120.0));
					}

					if ((entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_RunToZ) : 0) == 0
						&& entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_RunToZ, (int)(z + Mth.nextInt(RandomSource.create(), -30, 30)));
					}
				} else if (entity instanceof GrowlingManEntity _datEntL78 && (Boolean)_datEntL78.getEntityData().get(GrowlingManEntity.DATA_PlayerWest)) {
					if ((entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_RunToX) : 0) == 0
						&& entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_RunToX, (int)(x + 120.0));
					}

					if ((entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_RunToZ) : 0) == 0
						&& entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_RunToZ, (int)(z + Mth.nextInt(RandomSource.create(), -30, 30)));
					}
				} else if (entity instanceof GrowlingManEntity _datEntL84 && (Boolean)_datEntL84.getEntityData().get(GrowlingManEntity.DATA_PlayerNorth)) {
					if ((entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_RunToX) : 0) == 0
						&& entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_RunToX, (int)(x + Mth.nextInt(RandomSource.create(), -30, 30)));
					}

					if ((entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_RunToZ) : 0) == 0
						&& entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_RunToZ, (int)(z + 120.0));
					}
				} else if (entity instanceof GrowlingManEntity _datEntL90 && (Boolean)_datEntL90.getEntityData().get(GrowlingManEntity.DATA_PlayerSouth)) {
					if ((entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_RunToX) : 0) == 0
						&& entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_RunToX, (int)(x + Mth.nextInt(RandomSource.create(), -30, 30)));
					}

					if ((entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_RunToZ) : 0) == 0
						&& entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_RunToZ, (int)(z - 120.0));
					}
				}

				if (entity instanceof Mob _entity) {
					_entity.getNavigation()
						.moveTo(
							entity instanceof GrowlingManEntity _datEntIxxxxx
								? ((Integer)_datEntIxxxxx.getEntityData().get(GrowlingManEntity.DATA_RunToX)).intValue()
								: 0.0,
							world.getHeight(
								Types.MOTION_BLOCKING_NO_LEAVES,
								entity instanceof GrowlingManEntity _datEntIxxxx ? (Integer)_datEntIxxxx.getEntityData().get(GrowlingManEntity.DATA_RunToX) : 0,
								entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_RunToZ) : 0
							),
							entity instanceof GrowlingManEntity _datEntIxx
								? ((Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_RunToZ)).intValue()
								: 0.0,
							3.0
						);
				}

				if (entity instanceof GrowlingManEntity _datEntSetI) {
					_datEntSetI.getEntityData()
						.set(
							GrowlingManEntity.DATA_RunAtRunAwayEvent,
							(entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent) : 0)
								+ 1
						);
				}
			} else if ((entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent) : 0)
					>= 300
				&& (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent) : 0) < 340) {
				if ((entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer) : 0) == 0
					&& entity instanceof GrowlingManEntity _datEntSetI) {
					_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_DeManifestTimer, 1);
				}
			} else if ((entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent) : 0)
					>= 400
				&& (entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent) : 0) < 550) {
				Vec3 _center = new Vec3(x, y, z);

				for (Entity entityiteratorxxx : GMEntities.playersAsEntities(world, new AABB(_center, _center).inflate(40.0))
					.stream()
					.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
					.toList()) {
					if (entityiteratorxxx instanceof Player
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
							.checkGamemode(entityiteratorxxx)
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
							.checkGamemode(entityiteratorxxx)
						&& GrowlingManModVariables.of(entityiteratorxxx)
								.HoldingBreathTimer
							< 2.0
						&& entity instanceof GrowlingManEntity _datEntL113
						&& (Boolean)_datEntL113.getEntityData().get(GrowlingManEntity.DATA_CanSeePlayer)) {
						if (entity instanceof Mob _entity && entityiteratorxxx instanceof LivingEntity _ent) {
							_entity.setTarget(_ent);
						}
						break;
					}
				}

				if (entity instanceof GrowlingManEntity _datEntSetI) {
					_datEntSetI.getEntityData()
						.set(
							GrowlingManEntity.DATA_RunAtRunAwayEvent,
							(
									entity instanceof GrowlingManEntity _datEntIxxxx
										? (Integer)_datEntIxxxx.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent)
										: 0
								)
								+ 1
						);
				}
			} else if ((entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent) : 0)
					>= 550
				&& (entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer) : 0) == 0
				&& entity instanceof GrowlingManEntity _datEntSetI) {
				_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_DeManifestTimer, 1);
			}
		}
	}
}
