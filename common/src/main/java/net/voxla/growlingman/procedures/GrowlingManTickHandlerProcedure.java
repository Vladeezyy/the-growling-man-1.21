package net.voxla.growlingman.procedures;

import net.voxla.growlingman.port.GMEntities;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.BuiltInRegistries;
import net.voxla.growlingman.configuration.GrowlingManConfigConfiguration;
import net.voxla.growlingman.entity.GrowlingManEntity;
import net.voxla.growlingman.network.GrowlingManModVariables;

public class GrowlingManTickHandlerProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity != null) {
			double normalizedDz = 0.0;
			double normalizedDx = 0.0;
			double Magnitude = 0.0;
			if (entity.isPassenger()) {
				entity.stopRiding();
			}

			if ((entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) != 17
				&& (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) != 18) {
				ManifestEventProcedure.execute(world, x, y, z, entity);
				if ((entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer) : 0) > 0) {
					DeManifestEventProcedure.execute(world, x, y, z, entity);
				}
			}

			GMSneakProcedure.execute(world, x, y, z, entity);
			if ((entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_ChaseHurtTargetTimer) : 0) > 0
				&& (entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_ChaseHurtTargetTimer) : 0)
					< 600
				&& entity instanceof GrowlingManEntity _datEntSetI) {
				_datEntSetI.getEntityData()
					.set(
						GrowlingManEntity.DATA_ChaseHurtTargetTimer,
						(
								entity instanceof GrowlingManEntity _datEntIxxxx
									? (Integer)_datEntIxxxx.getEntityData().get(GrowlingManEntity.DATA_ChaseHurtTargetTimer)
									: 0
							)
							+ 1
					);
			}

			if ((entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_ChaseHurtTargetTimer) : 0)
				>= 600) {
				if (entity instanceof GrowlingManEntity _datEntSetS) {
					_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_ChaseHurtTarget, "");
				}

				if (entity instanceof GrowlingManEntity _datEntSetI) {
					_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_ChaseHurtTargetTimer, 0);
				}
			}

			if (GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 25.0, 25.0, 25.0)).isEmpty()
				&& (
					(entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) == 17
						|| (entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) == 18
				)
				&& !entity.level().isClientSide()) {
				entity.discard();
			}

			if (entity instanceof GrowlingManEntity _datEntL16 && (Boolean)_datEntL16.getEntityData().get(GrowlingManEntity.DATA_Chasing)) {
				if ((
						(entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) == 17
							|| (entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) == 18
					)
					&& !entity.level().isClientSide()) {
					entity.discard();
				}

				if (!(entity instanceof GrowlingManEntity animatablex ? animatablex.getTexture() : "null").equals("growling_man")
					&& entity instanceof GrowlingManEntity animatable) {
					animatable.setTexture("growling_man");
				}

				if (entity instanceof LivingEntity _livEnt22
					&& _livEnt22.hasEffect(MobEffects.SLOWNESS)
					&& (entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(MobEffects.SLOWNESS) ? _livEnt.getEffect(MobEffects.SLOWNESS).getAmplifier() : 0) > 3
					&& (entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer) : 0) == 0
					&& entity instanceof LivingEntity _entity) {
					_entity.removeEffect(MobEffects.SLOWNESS);
				}

				if (!world.isClientSide()) {
					if ((entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_ChaseAmountRandomizer) : 0)
							== 0
						&& entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData()
							.set(
								GrowlingManEntity.DATA_ChaseAmountRandomizer,
								Mth.nextInt(
									RandomSource.create(),
									(int)((Double)GrowlingManConfigConfiguration.GM_CHASETIME_MIN.get()).doubleValue(),
									(int)((Double)GrowlingManConfigConfiguration.GM_CHASETIME_MAX.get()).doubleValue()
								)
							);
					}

					if ((entity instanceof GrowlingManEntity _datEntIxxxxx ? (Integer)_datEntIxxxxx.getEntityData().get(GrowlingManEntity.DATA_ChaseTimer) : 0)
							< 1500
						&& (entity instanceof GrowlingManEntity _datEntIxxxx ? (Integer)_datEntIxxxx.getEntityData().get(GrowlingManEntity.DATA_ChaseSection) : 0)
							< (
								entity instanceof GrowlingManEntity _datEntIxxx
									? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_ChaseAmountRandomizer)
									: 0
							)
						&& (
								entity instanceof GrowlingManEntity _datEntIxx
									? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_ChaseAmountRandomizer)
									: 0
							)
							!= 0
						&& entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData()
							.set(
								GrowlingManEntity.DATA_ChaseTimer,
								(
										entity instanceof GrowlingManEntity _datEntIxxxxxx
											? (Integer)_datEntIxxxxxx.getEntityData().get(GrowlingManEntity.DATA_ChaseTimer)
											: 0
									)
									+ 1
							);
					}

					if ((entity instanceof GrowlingManEntity _datEntIxxxxx ? (Integer)_datEntIxxxxx.getEntityData().get(GrowlingManEntity.DATA_ChaseTimer) : 0)
							>= 1500
						&& (entity instanceof GrowlingManEntity _datEntIxxxx ? (Integer)_datEntIxxxx.getEntityData().get(GrowlingManEntity.DATA_ChaseSection) : 0)
							< (
								entity instanceof GrowlingManEntity _datEntIxxx
									? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_ChaseAmountRandomizer)
									: 0
							)
						&& (
								entity instanceof GrowlingManEntity _datEntIxx
									? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_ChaseAmountRandomizer)
									: 0
							)
							!= 0) {
						if (entity instanceof GrowlingManEntity _datEntSetI) {
							_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_ChaseTimer, 0);
						}

						if (entity instanceof GrowlingManEntity _datEntSetI) {
							_datEntSetI.getEntityData()
								.set(
									GrowlingManEntity.DATA_ChaseSection,
									(
											entity instanceof GrowlingManEntity _datEntIxxxxxx
												? (Integer)_datEntIxxxxxx.getEntityData().get(GrowlingManEntity.DATA_ChaseSection)
												: 0
										)
										+ 1
								);
						}
					}

					if ((entity instanceof GrowlingManEntity _datEntIxxxxx ? (Integer)_datEntIxxxxx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer) : 0)
							== 0
						&& (entity instanceof GrowlingManEntity _datEntIxxxx ? (Integer)_datEntIxxxx.getEntityData().get(GrowlingManEntity.DATA_ChaseSection) : 0)
							>= (
								entity instanceof GrowlingManEntity _datEntIxxx
									? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_ChaseAmountRandomizer)
									: 0
							)
						&& (
								entity instanceof GrowlingManEntity _datEntIxx
									? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_ChaseAmountRandomizer)
									: 0
							)
							!= 0
						&& entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_DeManifestTimer, 1);
					}
				}

				if ((entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_ChaseAmbientTimer) : 0) < 400
					&& entity instanceof GrowlingManEntity _datEntSetI) {
					_datEntSetI.getEntityData()
						.set(
							GrowlingManEntity.DATA_ChaseAmbientTimer,
							(entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_ChaseAmbientTimer) : 0)
								+ 1
						);
				}

				if (world.getLevelData().getGameTime() % 9L == 1L && !world.isClientSide()) {
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(
								null,
								BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.sporadic")),
								SoundSource.HOSTILE,
								2.0F,
								(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0)
							);
						} else {
							_level.playLocalSound(
								x,
								y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.sporadic")),
								SoundSource.HOSTILE,
								2.0F,
								(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0),
								false
							);
						}
					}

					if (world instanceof Level _levelx) {
						if (!_levelx.isClientSide()) {
							_levelx.playSound(
								null,
								BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.sporadic_distant")),
								SoundSource.HOSTILE,
								4.0F,
								(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0)
							);
						} else {
							_levelx.playLocalSound(
								x,
								y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.sporadic_distant")),
								SoundSource.HOSTILE,
								4.0F,
								(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0),
								false
							);
						}
					}
				}

				if (world.getLevelData().getGameTime() % 32L == 1L && !world.isClientSide()) {
					if (world instanceof Level _levelxx) {
						if (!_levelxx.isClientSide()) {
							_levelxx.playSound(
								null,
								BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.erratic")),
								SoundSource.HOSTILE,
								2.0F,
								(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0)
							);
						} else {
							_levelxx.playLocalSound(
								x,
								y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.erratic")),
								SoundSource.HOSTILE,
								2.0F,
								(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0),
								false
							);
						}
					}

					if (world instanceof Level _levelxxx) {
						if (!_levelxxx.isClientSide()) {
							_levelxxx.playSound(
								null,
								BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.erratic_distant")),
								SoundSource.NEUTRAL,
								4.0F,
								(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0)
							);
						} else {
							_levelxxx.playLocalSound(
								x,
								y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.erratic_distant")),
								SoundSource.NEUTRAL,
								4.0F,
								(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0),
								false
							);
						}
					}
				}

				if (world.getLevelData().getGameTime() % 21L == 1L && !world.isClientSide()) {
					if (world instanceof Level _levelxxxx) {
						if (!_levelxxxx.isClientSide()) {
							_levelxxxx.playSound(
								null,
								BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.sporadic_limbs")),
								SoundSource.HOSTILE,
								2.0F,
								(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0)
							);
						} else {
							_levelxxxx.playLocalSound(
								x,
								y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.sporadic_limbs")),
								SoundSource.HOSTILE,
								2.0F,
								(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0),
								false
							);
						}
					}

					if (world instanceof Level _levelxxxxx) {
						if (!_levelxxxxx.isClientSide()) {
							_levelxxxxx.playSound(
								null,
								BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.sporadic_limbs_distant")),
								SoundSource.HOSTILE,
								4.0F,
								(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0)
							);
						} else {
							_levelxxxxx.playLocalSound(
								x,
								y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.sporadic_limbs_distant")),
								SoundSource.HOSTILE,
								4.0F,
								(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0),
								false
							);
						}
					}
				}

				if (world.getLevelData().getGameTime() % 37L == 1L && !world.isClientSide()) {
					if (world instanceof Level _levelxxxxxx) {
						if (!_levelxxxxxx.isClientSide()) {
							_levelxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.growl_main")),
								SoundSource.HOSTILE,
								2.0F,
								(float)Mth.nextDouble(RandomSource.create(), 0.8, 1.0)
							);
						} else {
							_levelxxxxxx.playLocalSound(
								x,
								y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.growl_main")),
								SoundSource.HOSTILE,
								2.0F,
								(float)Mth.nextDouble(RandomSource.create(), 0.8, 1.0),
								false
							);
						}
					}

					if (world instanceof Level _levelxxxxxxx) {
						if (!_levelxxxxxxx.isClientSide()) {
							_levelxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.growl_main_distant")),
								SoundSource.HOSTILE,
								4.0F,
								(float)Mth.nextDouble(RandomSource.create(), 0.8, 1.0)
							);
						} else {
							_levelxxxxxxx.playLocalSound(
								x,
								y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.growl_main_distant")),
								SoundSource.HOSTILE,
								4.0F,
								(float)Mth.nextDouble(RandomSource.create(), 0.8, 1.0),
								false
							);
						}
					}
				}

				if ((entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_ChaseAmbientTimer) : 0)
					>= 400) {
					if (!world.isClientSide() && world instanceof Level _levelxxxxxxxx) {
						if (!_levelxxxxxxxx.isClientSide()) {
							_levelxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + 1.5, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.sporadic")),
								SoundSource.HOSTILE,
								3.0F,
								0.8F
							);
						} else {
							_levelxxxxxxxx.playLocalSound(
								x,
								y + 1.5,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.sporadic")),
								SoundSource.HOSTILE,
								3.0F,
								0.8F,
								false
							);
						}
					}

					if (entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_ChaseAmbientTimer, 0);
					}
				}

				SwimmingAIProcedure.execute(world, x, y, z, entity);
				KnockPlayerOffPillaringProcedure.execute(world, x, y, z, entity);
				if (!entity.isShiftKeyDown()) {
					GMClimbingProcedure.execute(world, x, y, z, entity);
				}

				if ((Boolean)GrowlingManConfigConfiguration.REGENERATE_BLOCKS.get()) {
					GMBreakGlassProcProcedure.execute(world, x, y, z);
				} else if (!(Boolean)GrowlingManConfigConfiguration.REGENERATE_BLOCKS.get()) {
					GMDestroyGlassProcProcedure.execute(world, x, y, z);
				}

				GMBreakDoorsProcedure.execute(world, x, y, z, entity);
				Vec3 _center = new Vec3(x, y, z);

				for (Entity entityiterator : GMEntities.playersAsEntities(world, new AABB(_center, _center).inflate(160.5))
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
											? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()) != null
												&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()).getGameMode() == GameType.CREATIVE
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
											? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()) != null
												&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()).getGameMode() == GameType.SPECTATOR
											: false;
									}
								}
							})
							.checkGamemode(entityiterator)
						&& (entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_ChaseHurtTarget) : "")
							.equals("")
						&& (
							!((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) instanceof Mob)
								|| !(entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null).isAlive()
						)) {
						if (entity instanceof Mob _entity && entityiterator instanceof LivingEntity _ent) {
							_entity.setTarget(_ent);
						}
						break;
					}
				}


				for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(160.5), e -> true)
					.stream()
					.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
					.toList()) {
					if (!(entity instanceof GrowlingManEntity _datEntSxx ? (String)_datEntSxx.getEntityData().get(GrowlingManEntity.DATA_ChaseHurtTarget) : "")
							.equals("")
						&& entityiteratorx.getDisplayName()
							.getString()
							.equals(entity instanceof GrowlingManEntity _datEntSx ? _datEntSx.getEntityData().get(GrowlingManEntity.DATA_ChaseHurtTarget) : "")
						&& !(new Object() {
								public boolean checkGamemode(Entity _ent) {
									if (_ent instanceof ServerPlayer _serverPlayer) {
										return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
									} else {
										return _ent.level().isClientSide() && _ent instanceof Player _player
											? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()) != null
												&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()).getGameMode() == GameType.CREATIVE
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
											? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()) != null
												&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()).getGameMode() == GameType.SPECTATOR
											: false;
									}
								}
							})
							.checkGamemode(entityiteratorx)
						&& (
							!((entity instanceof Mob _mobEntxxx ? _mobEntxxx.getTarget() : null) instanceof Mob)
								|| !(entity instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null).isAlive()
						)) {
						if (entity instanceof Mob _entity && entityiteratorx instanceof LivingEntity _ent) {
							_entity.setTarget(_ent);
						}
						break;
					}
				}

				if ((entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_ChaseTimer) : 0) > 800) {
					if (!GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 9.0, 9.0, 9.0)).isEmpty()
						&& entity instanceof LivingEntity _entity
						&& !_entity.level().isClientSide()) {
						_entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 2, 0, false, false));
					}
				} else if (!GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 30.0, 30.0, 30.0)).isEmpty()
					&& entity instanceof LivingEntity _entity
					&& !_entity.level().isClientSide()) {
					_entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 2, 0, false, false));
				}

				PlayerCanSeeGMProcedure.execute(world, x, y, z, entity);
				if (entity instanceof GrowlingManEntity _datEntL126
					&& (Boolean)_datEntL126.getEntityData().get(GrowlingManEntity.DATA_Enraged)
					&& entity instanceof LivingEntity _entity
					&& !_entity.level().isClientSide()) {
					_entity.addEffect(new MobEffectInstance(MobEffects.SPEED, 3, 3, false, false));
				}

				if (entity instanceof GrowlingManEntity _datEntL128
					&& (Boolean)_datEntL128.getEntityData().get(GrowlingManEntity.DATA_IsBeingLookedAt)
					&& (entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_AggroStared) : 0) < 600
					&& GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 8.0, 8.0, 8.0)).isEmpty()
					&& !(entity instanceof GrowlingManEntity _datEntL131 && (Boolean)_datEntL131.getEntityData().get(GrowlingManEntity.DATA_Enraged))) {
					if (entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData()
							.set(
								GrowlingManEntity.DATA_AggroStared,
								(entity instanceof GrowlingManEntity _datEntIxxxx ? (Integer)_datEntIxxxx.getEntityData().get(GrowlingManEntity.DATA_AggroStared) : 0)
									+ 1
							);
					}

					if ((entity instanceof GrowlingManEntity _datEntIxxxx ? (Integer)_datEntIxxxx.getEntityData().get(GrowlingManEntity.DATA_AggroStared) : 0)
						< 266) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
							_entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 10, 250, false, false));
						}
					} else if ((
								entity instanceof GrowlingManEntity _datEntIxxxxx ? (Integer)_datEntIxxxxx.getEntityData().get(GrowlingManEntity.DATA_AggroStared) : 0
							)
							>= 266
						&& Math.random() > 0.1
						&& !(entity instanceof LivingEntity _livEnt137 && _livEnt137.hasEffect(MobEffects.SLOWNESS))
						&& entity instanceof LivingEntity _entity
						&& !_entity.level().isClientSide()) {
						_entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 20, 250, false, false));
					}

					if (Math.random() > 0.99
						&& Math.random() > 0.99
						&& Math.random() > 0.99
						&& (entity instanceof GrowlingManEntity _datEntIxxxxx ? (Integer)_datEntIxxxxx.getEntityData().get(GrowlingManEntity.DATA_AggroStared) : 0)
							> 300
						&& !(entity instanceof GrowlingManEntity _datEntL140 && (Boolean)_datEntL140.getEntityData().get(GrowlingManEntity.DATA_Enraged))) {
						if (entity instanceof GrowlingManEntity _datEntSetL) {
							_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_Enraged, true);
						}

						if (!world.isClientSide()) {
							if (world instanceof Level _levelxxxxxxxxx) {
								if (!_levelxxxxxxxxx.isClientSide()) {
									_levelxxxxxxxxx.playSound(
										null,
										BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
										(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
										SoundSource.HOSTILE,
										5.0F,
										0.5F
									);
								} else {
									_levelxxxxxxxxx.playLocalSound(
										x,
										y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
										z,
										(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.player.kills_too_much")),
										SoundSource.HOSTILE,
										5.0F,
										0.5F,
										false
									);
								}
							}

							if (world instanceof Level _levelxxxxxxxxxx) {
								if (!_levelxxxxxxxxxx.isClientSide()) {
									_levelxxxxxxxxxx.playSound(
										null,
										BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
										(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("block.bell.resonate")),
										SoundSource.HOSTILE,
										5.0F,
										0.5F
									);
								} else {
									_levelxxxxxxxxxx.playLocalSound(
										x,
										y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
										z,
										(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("block.bell.resonate")),
										SoundSource.HOSTILE,
										5.0F,
										0.5F,
										false
									);
								}
							}

							if (world instanceof Level _levelxxxxxxxxxxx) {
								if (!_levelxxxxxxxxxxx.isClientSide()) {
									_levelxxxxxxxxxxx.playSound(
										null,
										BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
										(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.sporadic")),
										SoundSource.HOSTILE,
										5.0F,
										0.5F
									);
								} else {
									_levelxxxxxxxxxxx.playLocalSound(
										x,
										y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
										z,
										(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.sporadic")),
										SoundSource.HOSTILE,
										5.0F,
										0.5F,
										false
									);
								}
							}
						}
					}
				}

				if ((entity instanceof GrowlingManEntity _datEntIxxxxx ? (Integer)_datEntIxxxxx.getEntityData().get(GrowlingManEntity.DATA_AggroStared) : 0)
						>= 600
					&& (entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer) : 0)
						== 0
					&& entity instanceof GrowlingManEntity _datEntSetI) {
					_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_DeManifestTimer, 1);
				}
			}

			if (!(entity instanceof GrowlingManEntity _datEntL155 && (Boolean)_datEntL155.getEntityData().get(GrowlingManEntity.DATA_Chasing))) {
				if (!(world instanceof ServerLevel _level156 && _level156.isRaided(BlockPos.containing(x, y, z)))) {
					if ((entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) == 0) {
						AI0Procedure.execute(entity);
					} else if ((entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) == 1) {
						AI1Procedure.execute(world, x, y, z, entity);
					} else if ((entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) == 2) {
						AI2Procedure.execute(world, x, y, z, entity);
					} else if ((entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) == 3) {
						AI3Procedure.execute(world, x, y, z, entity);
					} else if ((entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) == 10) {
						RunToRunAwayEventProcedure.execute(world, x, y, z, entity);
					}
				}

				if ((entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) == 11) {
					DarkAIProcedure.execute(world, x, y, z, entity);
				} else if ((entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) == 17) {
					AI17ghostProcedure.execute(world, x, y, z, entity);
				} else if ((entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) == 18) {
					AI18ghost2Procedure.execute(world, x, y, z, entity);
				}

				if (!GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 320.0, 320.0, 320.0)).isEmpty()) {
					PlayerInGMViewProcedure.execute(world, x, y, z, entity);
				}

				PlayerCanSeeGMProcedure.execute(world, x, y, z, entity);
				if ((entity instanceof GrowlingManEntity _datEntIxxxxxx ? (Integer)_datEntIxxxxxx.getEntityData().get(GrowlingManEntity.DATA_AmbientTimer) : 0)
						== 0
					&& (entity instanceof GrowlingManEntity _datEntIxxxxx ? (Integer)_datEntIxxxxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) != 17
					&& (entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) != 18) {
					if (entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_AmbientTimer, Mth.nextInt(RandomSource.create(), 40, 80));
					}

					if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxx) {
						if (!_levelxxxxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + 1.7, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.ambient")),
								SoundSource.HOSTILE,
								1.0F,
								(float)Mth.nextDouble(RandomSource.create(), 0.8, 1.0)
							);
						} else {
							_levelxxxxxxxxxxxx.playLocalSound(
								x,
								y + 1.7,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.ambient")),
								SoundSource.HOSTILE,
								1.0F,
								(float)Mth.nextDouble(RandomSource.create(), 0.8, 1.0),
								false
							);
						}
					}
				}

				if ((entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_AmbientTimer) : 0) > 0
					&& entity instanceof GrowlingManEntity _datEntSetI) {
					_datEntSetI.getEntityData()
						.set(
							GrowlingManEntity.DATA_AmbientTimer,
							(entity instanceof GrowlingManEntity _datEntIxxxxx ? (Integer)_datEntIxxxxx.getEntityData().get(GrowlingManEntity.DATA_AmbientTimer) : 0)
								- 1
						);
				}

				if ((
						world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.WATER
							|| world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.WATER
							|| world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.WATER
							|| world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.WATER
					)
					&& (entity.getDeltaMovement().x() != 0.0 || entity.getDeltaMovement().z() != 0.0)) {
					entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x() * 1.15, entity.getDeltaMovement().y(), entity.getDeltaMovement().z() * 1.15));
				}

				if ((entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_ConcealSelf) : 0) > 0) {
					if (entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData()
							.set(
								GrowlingManEntity.DATA_ConcealSelf,
								(
										entity instanceof GrowlingManEntity _datEntIxxxxx
											? (Integer)_datEntIxxxxx.getEntityData().get(GrowlingManEntity.DATA_ConcealSelf)
											: 0
									)
									- 1
							);
					}

					if (entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_ConcealSelfTimer, 100);
					}

					ConcealBehindBlocksProcedure.execute(world, x, y, z, entity);
				}

				if ((entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_ConcealSelfTimer) : 0) > 0
					&& entity instanceof GrowlingManEntity _datEntSetI) {
					_datEntSetI.getEntityData()
						.set(
							GrowlingManEntity.DATA_ConcealSelfTimer,
							(
									entity instanceof GrowlingManEntity _datEntIxxxxx
										? (Integer)_datEntIxxxxx.getEntityData().get(GrowlingManEntity.DATA_ConcealSelfTimer)
										: 0
								)
								- 1
						);
				}

				Vec3 _center = new Vec3(x, y, z);

				for (Entity entityiteratorxx : GMEntities.playersAsEntities(world, new AABB(_center, _center).inflate(160.5))
					.stream()
					.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
					.toList()) {
					if (entityiteratorxx instanceof Player
						&& (entity instanceof GrowlingManEntity _datEntSxxx ? (String)_datEntSxxx.getEntityData().get(GrowlingManEntity.DATA_WalkTarget) : "")
							.equals("")
						&& !(new Object() {
								public boolean checkGamemode(Entity _ent) {
									if (_ent instanceof ServerPlayer _serverPlayer) {
										return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
									} else {
										return _ent.level().isClientSide() && _ent instanceof Player _player
											? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()) != null
												&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()).getGameMode() == GameType.CREATIVE
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
											? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()) != null
												&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()).getGameMode() == GameType.SPECTATOR
											: false;
									}
								}
							})
							.checkGamemode(entityiteratorxx)) {
						if (entity instanceof GrowlingManEntity _datEntSetS) {
							_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_WalkTarget, entityiteratorxx.getDisplayName().getString());
						}
						break;
					}
				}

				if (GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 322.0, 322.0, 322.0)).isEmpty()
					&& !(entity instanceof GrowlingManEntity _datEntSxxxx ? (String)_datEntSxxxx.getEntityData().get(GrowlingManEntity.DATA_WalkTarget) : "")
						.equals("")
					&& entity instanceof GrowlingManEntity _datEntSetS) {
					_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_WalkTarget, "");
				}

				if ((
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_LookedAtEventRandomizer)
								: 0
						)
						== 0
					&& (
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer)
								: 0
						)
						== 0
					&& (
							entity instanceof GrowlingManEntity _datEntIxxxxxxxx
								? (Integer)_datEntIxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_ConcealSelfTimer)
								: 0
						)
						== 0
					&& (
							entity instanceof GrowlingManEntity _datEntIxxxxxxx
								? (Integer)_datEntIxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_GrabbingPlayer)
								: 0
						)
						== 0
					&& (
						(entity instanceof GrowlingManEntity _datEntIxxxxxx ? (Integer)_datEntIxxxxxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) == 1
							|| (entity instanceof GrowlingManEntity _datEntIxxxxx ? (Integer)_datEntIxxxxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) == 2
							|| (entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) == 3
					)
					&& entity instanceof GrowlingManEntity _datEntL215
					&& (Boolean)_datEntL215.getEntityData().get(GrowlingManEntity.DATA_IsBeingSeen)) {
					label1551:
					if (!world.isClientSide()) {
						Vec3 _centerx = new Vec3(x, y, z);
						List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_centerx, _centerx).inflate(160.5), e -> true)
							.stream()
							.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
							.toList();
						Iterator _datEntIxxxxxxxxxxx = _entfound.iterator();

						Entity entityiteratorxxx;
						do {
							if (!_datEntIxxxxxxxxxxx.hasNext()) {
								break label1551;
							}

							entityiteratorxxx = (Entity)_datEntIxxxxxxxxxxx.next();
							if (entityiteratorxxx instanceof Player
								&& GrowlingManModVariables.of(entityiteratorxxx)
										.IsMarked
									> 0.0
								&& entity instanceof GrowlingManEntity _datEntL218
								&& (Boolean)_datEntL218.getEntityData().get(GrowlingManEntity.DATA_IsBeingLookedAt)) {
								if (entity instanceof GrowlingManEntity _datEntSetI) {
									_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_LookedAtEventRandomizer, Mth.nextInt(RandomSource.create(), 1, 4));
								}

								if (entity instanceof GrowlingManEntity _datEntSetI) {
									_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_RandomLookEventCooldown, 50);
								}
								break label1551;
							}
						} while (
							!(entityiteratorxxx instanceof Player)
								|| !(
									GrowlingManModVariables.of(entityiteratorxxx)
											.IsMarked
										<= 0.0
								)
						);

						if (entity instanceof GrowlingManEntity _datEntSetI) {
							_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_LookedAtEventRandomizer, Mth.nextInt(RandomSource.create(), 1, 2));
						}

						if (entity instanceof GrowlingManEntity _datEntSetI) {
							_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_RandomLookEventCooldown, 50);
						}
					}
				} else if ((
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_LookedAtEventRandomizer)
								: 0
						)
						== 0
					&& (
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer)
								: 0
						)
						== 0
					&& (
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_ConcealSelfTimer)
								: 0
						)
						== 0
					&& (
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_GrabbingPlayer)
								: 0
						)
						== 0
					&& (entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxx ? (Integer)_datEntIxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0)
						== 11
					&& entity instanceof GrowlingManEntity _datEntL232
					&& (Boolean)_datEntL232.getEntityData().get(GrowlingManEntity.DATA_IsBeingLookedAt)
					&& !world.isClientSide()) {
					if (entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_LookedAtEventRandomizer, Mth.nextInt(RandomSource.create(), 1, 2));
					}

					if (entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_RandomLookEventCooldown, 50);
					}
				}

				if ((
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_RandomLookEventCooldown)
								: 0
						)
						> 0
					&& entity instanceof GrowlingManEntity _datEntSetI) {
					_datEntSetI.getEntityData()
						.set(
							GrowlingManEntity.DATA_RandomLookEventCooldown,
							(
									entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxx
										? (Integer)_datEntIxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_RandomLookEventCooldown)
										: 0
								)
								- 1
						);
				}

				if ((
						entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxx
							? (Integer)_datEntIxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_LookedAtEventRandomizer)
							: 0
					)
					> 0) {
					if ((
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_LookedAtEventRandomizer)
								: 0
						)
						== 1) {
						if ((
									entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxx
										? (Integer)_datEntIxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer)
										: 0
								)
								== 0
							&& !(entity instanceof GrowlingManEntity _datEntL243 && (Boolean)_datEntL243.getEntityData().get(GrowlingManEntity.DATA_Chasing))
							&& (
									entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxx
										? (Integer)_datEntIxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer)
										: 0
								)
								== 0
							&& entity instanceof GrowlingManEntity _datEntSetI) {
							_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_DeManifestTimer, 1);
						}
					} else if ((
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_LookedAtEventRandomizer)
								: 0
						)
						== 2) {
						if ((
									entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxx
										? (Integer)_datEntIxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_ConcealSelf)
										: 0
								)
								== 0
							&& entity instanceof GrowlingManEntity _datEntSetI) {
							_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_ConcealSelf, 60);
						}

						if (entity instanceof GrowlingManEntity _datEntSetI) {
							_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_LookedAtEventRandomizer, 0);
						}
					} else if ((
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_LookedAtEventRandomizer)
								: 0
						)
						== 3) {
						if ((
									entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxx
										? (Integer)_datEntIxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer)
										: 0
								)
								== 0
							&& (
									entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxx
										? (Integer)_datEntIxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_AI)
										: 0
								)
								!= 10
							&& entity instanceof GrowlingManEntity _datEntSetI) {
							_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_AI, 10);
						}
					} else if ((
								entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxx
									? (Integer)_datEntIxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_LookedAtEventRandomizer)
									: 0
							)
							== 4
						&& (
								entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxx
									? (Integer)_datEntIxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer)
									: 0
							)
							== 0
						&& !(entity instanceof GrowlingManEntity _datEntL256 && (Boolean)_datEntL256.getEntityData().get(GrowlingManEntity.DATA_Chasing))
						&& entity instanceof GrowlingManEntity _datEntSetL) {
						_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_Chasing, true);
					}
				}

				if (entity instanceof GrowlingManEntity _datEntL258
					&& (Boolean)_datEntL258.getEntityData().get(GrowlingManEntity.DATA_Climbing)
					&& entity instanceof GrowlingManEntity _datEntSetL) {
					_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_Climbing, false);
				}

				if (entity instanceof GrowlingManEntity _datEntL260
					&& (Boolean)_datEntL260.getEntityData().get(GrowlingManEntity.DATA_HorizontalClimbing)
					&& entity instanceof GrowlingManEntity _datEntSetL) {
					_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_HorizontalClimbing, false);
				}

				if (!GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 600.0, 600.0, 600.0)).isEmpty()
					&& entity.getDeltaMovement().x() == 0.0
					&& entity.getDeltaMovement().z() == 0.0
					&& (
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_GrabbingPlayer)
								: 0
						)
						== 0
					&& !world.isClientSide()
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
			}

			if ((
					(
								entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxx
									? (Integer)_datEntIxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_GrabbingPlayer)
									: 0
							)
							> 0
						|| (
								entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxx
									? (Integer)_datEntIxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer)
									: 0
							)
							> 0
				)
				&& entity instanceof GrowlingManEntity _datEntSetI) {
				_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_GrabPlayerCooldown, 40);
			}

			if ((
						entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxx
							? (Integer)_datEntIxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_GrabPlayerCooldown)
							: 0
					)
					> 0
				&& entity instanceof GrowlingManEntity _datEntSetI) {
				_datEntSetI.getEntityData()
					.set(
						GrowlingManEntity.DATA_GrabPlayerCooldown,
						(
								entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxx
									? (Integer)_datEntIxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_GrabPlayerCooldown)
									: 0
							)
							- 1
					);
			}

			if ((
						entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxx
							? (Integer)_datEntIxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_Attacking)
							: 0
					)
					> 0
				&& entity instanceof GrowlingManEntity _datEntSetI) {
				_datEntSetI.getEntityData()
					.set(
						GrowlingManEntity.DATA_Attacking,
						(
								entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxx
									? (Integer)_datEntIxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_Attacking)
									: 0
							)
							- 1
					);
			}

			if ((entity instanceof Mob _mobEntxxxx ? _mobEntxxxx.getTarget() : null) != null
				&& (
						entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxx
							? (Integer)_datEntIxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_Attacking)
							: 0
					)
					< 6
				&& entity instanceof GrowlingManEntity _datEntSetI) {
				_datEntSetI.getEntityData()
					.set(
						GrowlingManEntity.DATA_Attacking,
						(
								entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxx
									? (Integer)_datEntIxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_Attacking)
									: 0
							)
							+ 10
					);
			}

			if ((
						entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxx
							? (Integer)_datEntIxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_LifeSpanTimer)
							: 0
					)
					< 72000
				&& entity instanceof GrowlingManEntity _datEntSetI) {
				_datEntSetI.getEntityData()
					.set(
						GrowlingManEntity.DATA_LifeSpanTimer,
						(
								entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxx
									? (Integer)_datEntIxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_LifeSpanTimer)
									: 0
							)
							+ 1
					);
			}

			Vec3 _center = new Vec3(x, y, z);

			for (Entity entityiteratorxxxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(250.0), e -> true)
				.stream()
				.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
				.toList()) {
				if (entityiteratorxxxx instanceof GrowlingManEntity
					&& entityiteratorxxxx != entity
					&& (
							entityiteratorxxxx instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_LifeSpanTimer)
								: 0
						)
						< (
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_LifeSpanTimer)
								: 0
						)
					&& !(entity instanceof GrowlingManEntity _datEntL297 && (Boolean)_datEntL297.getEntityData().get(GrowlingManEntity.DATA_Chasing))
					&& (
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_AI)
								: 0
						)
						!= 17
					&& (
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_AI)
								: 0
						)
						!= 18
					&& (
							entityiteratorxxxx instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_AI)
								: 0
						)
						!= 17
					&& (
							entityiteratorxxxx instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_AI)
								: 0
						)
						!= 18
					&& (
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_LifeSpanTimer)
								: 0
						)
						> 2
					&& (
							entityiteratorxxxx instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_LifeSpanTimer)
								: 0
						)
						> 2) {
					if ((
								entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxxxxxxx
									? (Integer)_datEntIxxxxxxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer)
									: 0
							)
							== 0
						&& entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_DeManifestTimer, 1);
					}
					break;
				}

				if (entityiteratorxxxx instanceof GrowlingManEntity
					&& entityiteratorxxxx != entity
					&& !(entity instanceof GrowlingManEntity _datEntL308 && (Boolean)_datEntL308.getEntityData().get(GrowlingManEntity.DATA_Chasing))
					&& entityiteratorxxxx instanceof GrowlingManEntity _datEntL309
					&& (Boolean)_datEntL309.getEntityData().get(GrowlingManEntity.DATA_Chasing)
					&& (
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_AI)
								: 0
						)
						!= 17
					&& (
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_AI)
								: 0
						)
						!= 18
					&& (
							entityiteratorxxxx instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_AI)
								: 0
						)
						!= 17
					&& (
							entityiteratorxxxx instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_AI)
								: 0
						)
						!= 18
					&& (
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_LifeSpanTimer)
								: 0
						)
						> 2
					&& (
							entityiteratorxxxx instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_LifeSpanTimer)
								: 0
						)
						> 2) {
					if ((
								entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxxxxxxxxxxxxx
									? (Integer)_datEntIxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer)
									: 0
							)
							== 0
						&& entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_DeManifestTimer, 1);
					}
					break;
				}
			}

			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
				_entity.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 255, false, false));
			}

			if (GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 600.0, 600.0, 600.0)).isEmpty()) {
				if ((
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_DespawnOverTimeTimer)
								: 0
						)
						< 2400
					&& entity instanceof GrowlingManEntity _datEntSetI) {
					_datEntSetI.getEntityData()
						.set(
							GrowlingManEntity.DATA_DespawnOverTimeTimer,
							(
									entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
										? (Integer)_datEntIxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_DespawnOverTimeTimer)
										: 0
								)
								+ 1
						);
				}

				if ((
							entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxxxxxxxxxxxxx
								? (Integer)_datEntIxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_DespawnOverTimeTimer)
								: 0
						)
						>= 2400
					&& !entity.level().isClientSide()) {
					entity.discard();
				}
			}


			for (Entity entityiteratorxxxx : GMEntities.playersAsEntities(world, new AABB(_center, _center).inflate(75.0))
				.stream()
				.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
				.toList()) {
				if (entityiteratorxxxx instanceof Player) {
					Magnitude = Math.sqrt(
						(entityiteratorxxxx.getX() - entity.getX()) * (entityiteratorxxxx.getX() - entity.getX())
							+ (entityiteratorxxxx.getZ() - entity.getZ()) * (entityiteratorxxxx.getZ() - entity.getZ())
					);
					normalizedDx = (entityiteratorxxxx.getX() - entity.getX()) / Magnitude;
					normalizedDz = (entityiteratorxxxx.getZ() - entity.getZ()) / Magnitude;
					if (normalizedDx >= 0.707) {
						if (entity instanceof GrowlingManEntity _datEntSetL) {
							_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayerEast, true);
						}
					} else if (entity instanceof GrowlingManEntity _datEntSetL) {
						_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayerEast, false);
					}

					if (normalizedDx <= -0.707) {
						if (entity instanceof GrowlingManEntity _datEntSetL) {
							_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayerWest, true);
						}
					} else if (entity instanceof GrowlingManEntity _datEntSetL) {
						_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayerWest, false);
					}

					if (normalizedDz >= 0.707) {
						if (entity instanceof GrowlingManEntity _datEntSetL) {
							_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayerSouth, true);
						}
					} else if (entity instanceof GrowlingManEntity _datEntSetL) {
						_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayerSouth, false);
					}

					if (normalizedDz <= -0.707) {
						if (entity instanceof GrowlingManEntity _datEntSetL) {
							_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayerNorth, true);
						}
					} else if (entity instanceof GrowlingManEntity _datEntSetL) {
						_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayerNorth, false);
					}
					break;
				}
			}

			if (GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 151.0, 151.0, 151.0)).isEmpty()) {
				if (entity instanceof GrowlingManEntity _datEntL349 && (Boolean)_datEntL349.getEntityData().get(GrowlingManEntity.DATA_PlayerEast)) {
					if (entity instanceof GrowlingManEntity _datEntSetL) {
						_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayerEast, false);
					}
				} else if (entity instanceof GrowlingManEntity _datEntL351 && (Boolean)_datEntL351.getEntityData().get(GrowlingManEntity.DATA_PlayerWest)) {
					if (entity instanceof GrowlingManEntity _datEntSetL) {
						_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayerWest, false);
					}
				} else if (entity instanceof GrowlingManEntity _datEntL353 && (Boolean)_datEntL353.getEntityData().get(GrowlingManEntity.DATA_PlayerSouth)) {
					if (entity instanceof GrowlingManEntity _datEntSetL) {
						_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayerSouth, false);
					}
				} else if (entity instanceof GrowlingManEntity _datEntL355
					&& (Boolean)_datEntL355.getEntityData().get(GrowlingManEntity.DATA_PlayerNorth)
					&& entity instanceof GrowlingManEntity _datEntSetL) {
					_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayerNorth, false);
				}
			}

			if ((
					entity.getDeltaMovement().x() > 0.06
						|| entity.getDeltaMovement().x() < -0.06
						|| entity.getDeltaMovement().z() > 0.06
						|| entity.getDeltaMovement().z() < -0.06
				)
				&& world.getBlockState(BlockPos.containing(x, y - 1.0, z)).canOcclude()
				&& world instanceof ServerLevel _levelxxxxxxxxxxxxx) {
				_levelxxxxxxxxxxxxx.getServer()
					.getCommands()
					.performPrefixedCommand(
						new CommandSourceStack(
								CommandSource.NULL,
								new Vec3(x, y, z),
								Vec2.ZERO,
								_levelxxxxxxxxxxxxx,
								net.minecraft.server.permissions.LevelBasedPermissionSet.OWNER,
								"",
								Component.literal(""),
								_levelxxxxxxxxxxxxx.getServer(),
								null
							)
							.withSuppressedOutput(),
						"/particle block "
							+ BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock()).toString()
							+ " ~ ~ ~ 0.2 0 0.2 1 1 normal"
					);
			}

			if ((
						entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxxxxxxxxxxxxx
							? (Integer)_datEntIxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_AI)
							: 0
					)
					== 10
				&& entity.maxUpStep() != 1.2
				&& !entity.isInWater()) {
				if (entity instanceof LivingEntity _stepEntity) {
					_stepEntity.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.2);
				}
			} else if (entity.maxUpStep() != 0.6) {
				if (entity instanceof LivingEntity _stepEntity) {
					_stepEntity.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(0.6);
				}
			}

			if (world instanceof ServerLevel _level371
				&& _level371.isRaided(BlockPos.containing(x, y, z))
				&& (
						entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
							? (Integer)_datEntIxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_AI)
							: 0
					)
					!= 11
				&& (
						entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
							? (Integer)_datEntIxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_AI)
							: 0
					)
					!= 17
				&& (
						entity instanceof GrowlingManEntity _datEntIxxxxxxxxxxxxxxxxxxxxxxxxxxxx
							? (Integer)_datEntIxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEntityData().get(GrowlingManEntity.DATA_AI)
							: 0
					)
					!= 18) {
				if (world.getLevelData().getGameTime() % 21L == 1L && !world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxx) {
					if (!_levelxxxxxxxxxxxxx.isClientSide()) {
						_levelxxxxxxxxxxxxx.playSound(
							null,
							BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.sporadic_limbs")),
							SoundSource.HOSTILE,
							1.0F,
							(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0)
						);
					} else {
						_levelxxxxxxxxxxxxx.playLocalSound(
							x,
							y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
							z,
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.sporadic_limbs")),
							SoundSource.HOSTILE,
							1.0F,
							(float)Mth.nextDouble(RandomSource.create(), 0.9, 1.0),
							false
						);
					}
				}

				if (world.getLevelData().getGameTime() % 37L == 1L && !world.isClientSide() && world instanceof Level _levelxxxxxxxxxxxxxx) {
					if (!_levelxxxxxxxxxxxxxx.isClientSide()) {
						_levelxxxxxxxxxxxxxx.playSound(
							null,
							BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.growl_main")),
							SoundSource.HOSTILE,
							1.0F,
							(float)Mth.nextDouble(RandomSource.create(), 0.8, 1.0)
						);
					} else {
						_levelxxxxxxxxxxxxxx.playLocalSound(
							x,
							y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
							z,
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.growl_main")),
							SoundSource.HOSTILE,
							1.0F,
							(float)Mth.nextDouble(RandomSource.create(), 0.8, 1.0),
							false
						);
					}
				}
			}
		}
	}
}
