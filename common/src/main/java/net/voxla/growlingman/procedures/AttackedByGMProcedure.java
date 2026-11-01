package net.voxla.growlingman.procedures;

import net.voxla.growlingman.port.PortUtil;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.voxla.growlingman.port.bus.LivingIncomingDamageEvent;
import net.voxla.growlingman.port.bus.Event;
import net.voxla.growlingman.port.bus.ICancellableEvent;
import net.voxla.growlingman.port.bus.SubscribeEvent;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.voxla.growlingman.entity.GrowlingManEntity;

@EventBusSubscriber
public class AttackedByGMProcedure {
	@SubscribeEvent
	public static void onEntityAttacked(LivingIncomingDamageEvent event) {
		if (event != null && event.getEntity() != null) {
			execute(event, event.getEntity().level(), event.getEntity(), event.getSource().getEntity());
		}
	}

	public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
		execute(null, world, entity, sourceentity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity, Entity sourceentity) {
		if (entity != null && sourceentity != null) {
			if (sourceentity instanceof Player
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
					.checkGamemode(sourceentity)
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
					.checkGamemode(sourceentity)
				&& entity instanceof GrowlingManEntity
				&& (entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_GrabbingPlayer) : 0) == 0
				&& (entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer) : 0) == 0
				&& (entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) != 17
				&& (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) != 18) {
				if (entity instanceof GrowlingManEntity _datEntSetS) {
					_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_ChaseHurtTarget, sourceentity.getDisplayName().getString());
				}

				if (entity instanceof GrowlingManEntity _datEntSetI) {
					_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_ChaseHurtTargetTimer, 1);
				}

				if (entity instanceof GrowlingManEntity _datEntSetL) {
					_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_Chasing, true);
				}
			}

			if (sourceentity instanceof Mob
				&& entity instanceof GrowlingManEntity
				&& (entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) != 17
				&& (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) != 18
				&& entity instanceof Mob _entity
				&& sourceentity instanceof LivingEntity _ent) {
				_entity.setTarget(_ent);
			}

			if (sourceentity instanceof GrowlingManEntity
				&& (
					(entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_GrabbingPlayer) : 0) > 0
						|| (entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer) : 0)
							> 0
						|| (entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) == 17
						|| (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) == 18
				)
				&& event instanceof ICancellableEvent _cancellable) {
				_cancellable.setCanceled(true);
			}

			if (!(entity instanceof LivingEntity _livEnt22 && _livEnt22.isBlocking())
				&& sourceentity instanceof GrowlingManEntity
				&& !((entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F) > 50.0F)
				&& (entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_GrabbingPlayer) : 0) == 0
				&& (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer) : 0) == 0) {
				if (entity instanceof Player) {
					if (Math.random() > 0.93
						&& Math.random() < 0.98
						&& (
								sourceentity instanceof GrowlingManEntity _datEntIxx
									? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_GrabPlayerCooldown)
									: 0
							)
							== 0
						&& (
							!sourceentity.isShiftKeyDown()
								|| world.getBlockState(BlockPos.containing(sourceentity.getX(), sourceentity.getY(), sourceentity.getZ())).getBlock()
									!= Blocks.WATER
								|| world.getBlockState(BlockPos.containing(sourceentity.getX(), sourceentity.getY(), sourceentity.getZ())).getBlock()
									!= Blocks.WATER
								|| world.getBlockState(BlockPos.containing(sourceentity.getX(), sourceentity.getY() - 1.0, sourceentity.getZ())).getBlock()
									!= Blocks.WATER
								|| world.getBlockState(BlockPos.containing(sourceentity.getX(), sourceentity.getY() - 1.0, sourceentity.getZ())).getBlock()
									!= Blocks.WATER
						)) {
						if (sourceentity instanceof GrowlingManEntity _datEntSetI) {
							_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_GrabbingPlayer, 1);
						}
					} else if ((sourceentity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0)
						== 10) {
						entity.hurt(
							new DamageSource(
								world.registryAccess()
									.registryOrThrow(Registries.DAMAGE_TYPE)
									.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("growling_man:gm_damage")))
							),
							(float)((entity instanceof LivingEntity _livEntx ? _livEntx.getMaxHealth() : -1.0F) * 1.2)
						);
					} else {
						entity.hurt(
							new DamageSource(
								world.registryAccess()
									.registryOrThrow(Registries.DAMAGE_TYPE)
									.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("growling_man:gm_damage")))
							),
							(entity instanceof LivingEntity _livEntx ? _livEntx.getMaxHealth() : -1.0F) / 5.0F
						);
					}
				} else {
					entity.hurt(
						new DamageSource(
							world.registryAccess()
								.registryOrThrow(Registries.DAMAGE_TYPE)
								.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("growling_man:gm_damage")))
						),
						(entity instanceof LivingEntity _livEntx ? _livEntx.getMaxHealth() : -1.0F) / 5.0F
					);
				}
			}

			if (!(entity instanceof LivingEntity _livEnt61 && _livEnt61.isBlocking())
				&& sourceentity instanceof GrowlingManEntity
				&& (entity instanceof LivingEntity _livEntx ? _livEntx.getMaxHealth() : -1.0F) > 50.0F
				&& (entity instanceof GrowlingManEntity _datEntIxxxx ? (Integer)_datEntIxxxx.getEntityData().get(GrowlingManEntity.DATA_GrabbingPlayer) : 0) == 0
				&& (entity instanceof GrowlingManEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer) : 0) == 0) {
				entity.hurt(
					new DamageSource(
						world.registryAccess()
							.registryOrThrow(Registries.DAMAGE_TYPE)
							.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("growling_man:gm_damage")))
					),
					70.0F
				);
			}

			if (entity instanceof LivingEntity _livEnt68 && _livEnt68.isBlocking() && sourceentity instanceof GrowlingManEntity) {
				if ((entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof ShieldItem) {
					if (entity instanceof Player _player) {
						_player.getCooldowns().addCooldown((entity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).getItem(), 50);
					}

					ItemStack _ist = entity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY;
					PortUtil.hurtAndBreak(_ist, 1, null);
				} else if ((entity instanceof LivingEntity _livEntx ? _livEntx.getOffhandItem() : ItemStack.EMPTY).getItem() instanceof ShieldItem) {
					if (entity instanceof Player _player) {
						_player.getCooldowns().addCooldown((entity instanceof LivingEntity _livEntxx ? _livEntxx.getOffhandItem() : ItemStack.EMPTY).getItem(), 50);
					}

					ItemStack _ist = entity instanceof LivingEntity _livEntxx ? _livEntxx.getOffhandItem() : ItemStack.EMPTY;
					PortUtil.hurtAndBreak(_ist, 1, null);
				}
			}
		}
	}
}
