package net.voxla.growlingman.procedures;

import net.voxla.growlingman.port.GMEntities;
import java.util.Comparator;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.voxla.growlingman.entity.GrowlingManEntity;

public class GMClimbingProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity != null) {
			if ((
					world.getBlockState(BlockPos.containing(x + 1.0, y + 0.0, z)).canOcclude()
							&& world.getBlockState(BlockPos.containing(x + 1.0, y + 1.0, z)).canOcclude()
							&& world.getBlockState(BlockPos.containing(x + 1.0, y + 2.0, z)).canOcclude()
						|| world.getBlockState(BlockPos.containing(x - 1.0, y + 0.0, z)).canOcclude()
							&& world.getBlockState(BlockPos.containing(x - 1.0, y + 1.0, z)).canOcclude()
							&& world.getBlockState(BlockPos.containing(x - 1.0, y + 2.0, z)).canOcclude()
						|| world.getBlockState(BlockPos.containing(x, y + 0.0, z + 1.0)).canOcclude()
							&& world.getBlockState(BlockPos.containing(x, y + 1.0, z + 1.0)).canOcclude()
							&& world.getBlockState(BlockPos.containing(x, y + 2.0, z + 1.0)).canOcclude()
						|| world.getBlockState(BlockPos.containing(x, y + 0.0, z - 1.0)).canOcclude()
							&& world.getBlockState(BlockPos.containing(x, y + 1.0, z - 1.0)).canOcclude()
							&& world.getBlockState(BlockPos.containing(x, y + 2.0, z - 1.0)).canOcclude()
				)
				&& !entity.isInWaterRainOrBubble()) {
				if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null) {
					Vec3 _center = new Vec3(x, y, z);

					for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(50.0), e -> true)
						.stream()
						.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
						.toList()) {
						if (entityiterator == (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null)
							&& entityiterator.getY() > entity.getY()
							&& entity instanceof GrowlingManEntity _datEntSetL) {
							_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_Climbing, true);
						}
					}
				}
			} else if ((world.getBlockState(BlockPos.containing(x + 1.0, y + 0.0, z)).canOcclude() || entity.getDirection() != Direction.EAST) && !entity.isInWaterRainOrBubble()) {
				if ((world.getBlockState(BlockPos.containing(x - 1.0, y + 0.0, z)).canOcclude() || entity.getDirection() != Direction.WEST) && !entity.isInWaterRainOrBubble()) {
					if ((world.getBlockState(BlockPos.containing(x, y + 0.0, z + 1.0)).canOcclude() || entity.getDirection() != Direction.SOUTH) && !entity.isInWaterRainOrBubble()) {
						if ((!world.getBlockState(BlockPos.containing(x, y + 0.0, z - 1.0)).canOcclude() && entity.getDirection() == Direction.NORTH || entity.isInWaterRainOrBubble())
							&& entity instanceof GrowlingManEntity _datEntSetL) {
							_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_Climbing, false);
						}
					} else if (entity instanceof GrowlingManEntity _datEntSetL) {
						_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_Climbing, false);
					}
				} else if (entity instanceof GrowlingManEntity _datEntSetL) {
					_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_Climbing, false);
				}
			} else if (entity instanceof GrowlingManEntity _datEntSetL) {
				_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_Climbing, false);
			}

			if ((
					world.getBlockState(BlockPos.containing(x + 1.0, y - 1.0, z)).canOcclude()
						|| world.getBlockState(BlockPos.containing(x + 2.0, y - 1.0, z)).canOcclude()
						|| world.getBlockState(BlockPos.containing(x + 3.0, y - 1.0, z)).canOcclude()
				)
				&& entity.getDirection() == Direction.EAST
				&& !entity.isInWaterRainOrBubble()) {
				if (entity instanceof GrowlingManEntity _datEntL47
					&& (Boolean)_datEntL47.getEntityData().get(GrowlingManEntity.DATA_Climbing)
					&& world.getBlockState(BlockPos.containing(x, y + 3.6, z)).canOcclude()
					&& entity instanceof GrowlingManEntity _datEntSetL) {
					_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_HorizontalClimbing, true);
				}
			} else if ((
					world.getBlockState(BlockPos.containing(x - 1.0, y - 1.0, z)).canOcclude()
						|| world.getBlockState(BlockPos.containing(x - 2.0, y - 1.0, z)).canOcclude()
						|| world.getBlockState(BlockPos.containing(x - 3.0, y - 1.0, z)).canOcclude()
				)
				&& entity.getDirection() == Direction.WEST
				&& !entity.isInWaterRainOrBubble()) {
				if (entity instanceof GrowlingManEntity _datEntL56
					&& (Boolean)_datEntL56.getEntityData().get(GrowlingManEntity.DATA_Climbing)
					&& world.getBlockState(BlockPos.containing(x, y + 3.6, z)).canOcclude()
					&& entity instanceof GrowlingManEntity _datEntSetL) {
					_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_HorizontalClimbing, true);
				}
			} else if ((
					world.getBlockState(BlockPos.containing(x, y - 1.0, z + 1.0)).canOcclude()
						|| world.getBlockState(BlockPos.containing(x, y - 1.0, z + 2.0)).canOcclude()
						|| world.getBlockState(BlockPos.containing(x, y - 1.0, z + 3.0)).canOcclude()
				)
				&& entity.getDirection() == Direction.SOUTH
				&& !entity.isInWaterRainOrBubble()) {
				if (entity instanceof GrowlingManEntity _datEntL65
					&& (Boolean)_datEntL65.getEntityData().get(GrowlingManEntity.DATA_Climbing)
					&& world.getBlockState(BlockPos.containing(x, y + 3.6, z)).canOcclude()
					&& entity instanceof GrowlingManEntity _datEntSetL) {
					_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_HorizontalClimbing, true);
				}
			} else if ((
					world.getBlockState(BlockPos.containing(x, y - 1.0, z - 1.0)).canOcclude()
						|| world.getBlockState(BlockPos.containing(x, y - 1.0, z - 2.0)).canOcclude()
						|| world.getBlockState(BlockPos.containing(x, y - 1.0, z - 3.0)).canOcclude()
				)
				&& entity.getDirection() == Direction.NORTH
				&& !entity.isInWaterRainOrBubble()) {
				if (entity instanceof GrowlingManEntity _datEntL74
					&& (Boolean)_datEntL74.getEntityData().get(GrowlingManEntity.DATA_Climbing)
					&& world.getBlockState(BlockPos.containing(x, y + 3.6, z)).canOcclude()
					&& entity instanceof GrowlingManEntity _datEntSetL) {
					_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_HorizontalClimbing, true);
				}
			} else if (entity instanceof GrowlingManEntity _datEntSetL) {
				_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_HorizontalClimbing, false);
			}

			if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null) {
				Vec3 _center = new Vec3(x, y, z);

				for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(50.0), e -> true)
					.stream()
					.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
					.toList()) {
					if (entityiteratorx == (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) && entityiteratorx.getY() < entity.getY()) {
						if (entity instanceof GrowlingManEntity _datEntSetL) {
							_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_Climbing, false);
						}

						if (entity instanceof GrowlingManEntity _datEntSetL) {
							_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_HorizontalClimbing, false);
						}
					}
				}
			}

			if (entity instanceof GrowlingManEntity _datEntL87
				&& (Boolean)_datEntL87.getEntityData().get(GrowlingManEntity.DATA_Climbing)
				&& !(entity instanceof GrowlingManEntity _datEntL88 && (Boolean)_datEntL88.getEntityData().get(GrowlingManEntity.DATA_HorizontalClimbing))) {
				if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null) {
					Vec3 _center = new Vec3(x, y, z);

					for (Entity entityiteratorxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(50.0), e -> true)
						.stream()
						.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
						.toList()) {
						if (entity.getDeltaMovement().y() < 0.1 && entityiteratorxx == (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null)) {
							entity.lookAt(Anchor.EYES, new Vec3(entityiteratorxx.getX(), entityiteratorxx.getY() + 1.7, entityiteratorxx.getZ()));
						}
					}
				}

				if (world.getBlockState(BlockPos.containing(x + 1.0, y + 0.0, z)).canOcclude() && entity.getDirection() == Direction.EAST) {
					entity.setDeltaMovement(new Vec3(0.2, 0.2, entity.getDeltaMovement().z() / 4.0));
					entity.fallDistance = 0.0F;
				} else if (world.getBlockState(BlockPos.containing(x - 1.0, y + 0.0, z)).canOcclude() && entity.getDirection() == Direction.WEST) {
					entity.setDeltaMovement(new Vec3(-0.2, 0.2, entity.getDeltaMovement().z() / 4.0));
					entity.fallDistance = 0.0F;
				} else if (world.getBlockState(BlockPos.containing(x, y + 0.0, z + 1.0)).canOcclude() && entity.getDirection() == Direction.SOUTH) {
					entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x() / 4.0, 0.2, 0.2));
					entity.fallDistance = 0.0F;
				} else if (world.getBlockState(BlockPos.containing(x, y + 0.0, z - 1.0)).canOcclude() && entity.getDirection() == Direction.NORTH) {
					entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x() / 4.0, 0.2, -0.2));
					entity.fallDistance = 0.0F;
				}
			} else if (entity instanceof GrowlingManEntity _datEntL123 && (Boolean)_datEntL123.getEntityData().get(GrowlingManEntity.DATA_HorizontalClimbing)) {
				if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null) {
					Vec3 _center = new Vec3(x, y, z);

					for (Entity entityiteratorxxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(50.0), e -> true)
						.stream()
						.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
						.toList()) {
						if (entity.getDeltaMovement().y() < 0.1 && entityiteratorxxx == (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null)) {
							entity.lookAt(Anchor.EYES, new Vec3(entityiteratorxxx.getX(), entityiteratorxxx.getY() + 1.7, entityiteratorxxx.getZ()));
						}
					}
				}

				if ((
						world.getBlockState(BlockPos.containing(x + 1.0, y - 1.0, z)).canOcclude()
							|| world.getBlockState(BlockPos.containing(x + 2.0, y - 1.0, z)).canOcclude()
							|| world.getBlockState(BlockPos.containing(x + 3.0, y - 1.0, z)).canOcclude()
					)
					&& entity.getDirection() == Direction.EAST) {
					if (world.isEmptyBlock(BlockPos.containing(x + 0.5, y + 3.6, z))) {
						entity.setDeltaMovement(new Vec3(0.1, 0.2, entity.getDeltaMovement().z() / 4.0));
					} else {
						entity.setDeltaMovement(new Vec3(-0.1, 0.2, entity.getDeltaMovement().z() / 4.0));
					}

					entity.fallDistance = 0.0F;
				} else if ((
						world.getBlockState(BlockPos.containing(x - 1.0, y - 1.0, z)).canOcclude()
							|| world.getBlockState(BlockPos.containing(x - 2.0, y - 1.0, z)).canOcclude()
							|| world.getBlockState(BlockPos.containing(x - 3.0, y - 1.0, z)).canOcclude()
					)
					&& entity.getDirection() == Direction.WEST) {
					if (world.isEmptyBlock(BlockPos.containing(x - 0.5, y + 3.6, z))) {
						entity.setDeltaMovement(new Vec3(-0.1, 0.2, entity.getDeltaMovement().z() / 4.0));
					} else {
						entity.setDeltaMovement(new Vec3(0.1, 0.2, entity.getDeltaMovement().z() / 4.0));
					}

					entity.fallDistance = 0.0F;
				} else if ((
						world.getBlockState(BlockPos.containing(x, y - 1.0, z + 1.0)).canOcclude()
							|| world.getBlockState(BlockPos.containing(x, y - 1.0, z + 2.0)).canOcclude()
							|| world.getBlockState(BlockPos.containing(x, y - 1.0, z + 3.0)).canOcclude()
					)
					&& entity.getDirection() == Direction.SOUTH) {
					if (world.isEmptyBlock(BlockPos.containing(x, y + 3.6, z + 0.5))) {
						entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x() / 4.0, 0.2, 0.1));
					} else {
						entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x() / 4.0, 0.2, -0.1));
					}

					entity.fallDistance = 0.0F;
				} else if ((
						world.getBlockState(BlockPos.containing(x, y - 1.0, z - 1.0)).canOcclude()
							|| world.getBlockState(BlockPos.containing(x, y - 1.0, z - 2.0)).canOcclude()
							|| world.getBlockState(BlockPos.containing(x, y - 1.0, z - 3.0)).canOcclude()
					)
					&& entity.getDirection() == Direction.NORTH) {
					if (world.isEmptyBlock(BlockPos.containing(x, y + 3.6, z - 0.5))) {
						entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x() / 4.0, 0.2, -0.1));
					} else {
						entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x() / 4.0, 0.2, 0.1));
					}

					entity.fallDistance = 0.0F;
				}
			}

			if ((
					entity instanceof GrowlingManEntity _datEntL178 && (Boolean)_datEntL178.getEntityData().get(GrowlingManEntity.DATA_Climbing)
						|| entity instanceof GrowlingManEntity _datEntL179 && (Boolean)_datEntL179.getEntityData().get(GrowlingManEntity.DATA_HorizontalClimbing)
				)
				&& entity instanceof GrowlingManEntity) {
				if (world.getBlockState(BlockPos.containing(x + 1.0, y + 0.0, z)).canOcclude()
					&& world.getBlockState(BlockPos.containing(x + 1.0, y + 1.0, z)).canOcclude()
					&& world.getBlockState(BlockPos.containing(x + 1.0, y + 2.0, z)).canOcclude()
					&& GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 5.0, 5.0, 5.0)).isEmpty()
					&& entity.getDirection() == Direction.EAST) {
					if (entity.getDeltaMovement().y() == 0.2) {
						entity.setYRot(-90.0F);
						entity.setXRot(entity.getXRot());
						entity.setYBodyRot(entity.getYRot());
						entity.setYHeadRot(entity.getYRot());
						entity.yRotO = entity.getYRot();
						entity.xRotO = entity.getXRot();
						if (entity instanceof LivingEntity _entity) {
							_entity.yBodyRotO = _entity.getYRot();
							_entity.yHeadRotO = _entity.getYRot();
						}
					}
				} else if (world.getBlockState(BlockPos.containing(x - 1.0, y + 0.0, z)).canOcclude()
					&& world.getBlockState(BlockPos.containing(x - 1.0, y + 1.0, z)).canOcclude()
					&& world.getBlockState(BlockPos.containing(x - 1.0, y + 2.0, z)).canOcclude()
					&& GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 5.0, 5.0, 5.0)).isEmpty()
					&& entity.getDirection() == Direction.WEST) {
					if (entity.getDeltaMovement().y() == 0.2) {
						entity.setYRot(90.0F);
						entity.setXRot(entity.getXRot());
						entity.setYBodyRot(entity.getYRot());
						entity.setYHeadRot(entity.getYRot());
						entity.yRotO = entity.getYRot();
						entity.xRotO = entity.getXRot();
						if (entity instanceof LivingEntity _entity) {
							_entity.yBodyRotO = _entity.getYRot();
							_entity.yHeadRotO = _entity.getYRot();
						}
					}
				} else if (world.getBlockState(BlockPos.containing(x, y + 0.0, z + 1.0)).canOcclude()
					&& world.getBlockState(BlockPos.containing(x, y + 1.0, z + 1.0)).canOcclude()
					&& world.getBlockState(BlockPos.containing(x, y + 2.0, z + 1.0)).canOcclude()
					&& GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 5.0, 5.0, 5.0)).isEmpty()
					&& entity.getDirection() == Direction.SOUTH) {
					if (entity.getDeltaMovement().y() == 0.2) {
						entity.setYRot(0.0F);
						entity.setXRot(entity.getXRot());
						entity.setYBodyRot(entity.getYRot());
						entity.setYHeadRot(entity.getYRot());
						entity.yRotO = entity.getYRot();
						entity.xRotO = entity.getXRot();
						if (entity instanceof LivingEntity _entity) {
							_entity.yBodyRotO = _entity.getYRot();
							_entity.yHeadRotO = _entity.getYRot();
						}
					}
				} else if (world.getBlockState(BlockPos.containing(x, y + 0.0, z - 1.0)).canOcclude()
					&& world.getBlockState(BlockPos.containing(x, y + 1.0, z - 1.0)).canOcclude()
					&& world.getBlockState(BlockPos.containing(x, y + 2.0, z - 1.0)).canOcclude()
					&& GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 5.0, 5.0, 5.0)).isEmpty()
					&& entity.getDirection() == Direction.NORTH
					&& entity.getDeltaMovement().y() == 0.2) {
					entity.setYRot(180.0F);
					entity.setXRot(entity.getXRot());
					entity.setYBodyRot(entity.getYRot());
					entity.setYHeadRot(entity.getYRot());
					entity.yRotO = entity.getYRot();
					entity.xRotO = entity.getXRot();
					if (entity instanceof LivingEntity _entity) {
						_entity.yBodyRotO = _entity.getYRot();
						_entity.yHeadRotO = _entity.getYRot();
					}
				}

				if (world.isEmptyBlock(BlockPos.containing(x + 0.5, y + 3.6, z))
					|| !GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 5.0, 5.0, 5.0)).isEmpty()
					|| entity.getDirection() != Direction.EAST) {
					if (!world.isEmptyBlock(BlockPos.containing(x - 0.5, y + 3.6, z))
						&& GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 5.0, 5.0, 5.0)).isEmpty()
						&& entity.getDirection() == Direction.WEST) {
						if (entity.getDeltaMovement().x() == 0.1
							&& entity instanceof GrowlingManEntity _datEntL230
							&& (Boolean)_datEntL230.getEntityData().get(GrowlingManEntity.DATA_HorizontalClimbing)) {
							entity.setYRot(90.0F);
							entity.setXRot(entity.getXRot());
							entity.setYBodyRot(entity.getYRot());
							entity.setYHeadRot(entity.getYRot());
							entity.yRotO = entity.getYRot();
							entity.xRotO = entity.getXRot();
							if (entity instanceof LivingEntity _entity) {
								_entity.yBodyRotO = _entity.getYRot();
								_entity.yHeadRotO = _entity.getYRot();
							}
						}
					} else if (!world.isEmptyBlock(BlockPos.containing(x, y + 3.6, z + 0.5))
						&& GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 5.0, 5.0, 5.0)).isEmpty()
						&& entity.getDirection() == Direction.SOUTH) {
						if (entity.getDeltaMovement().z() == -0.1
							&& entity instanceof GrowlingManEntity _datEntL238
							&& (Boolean)_datEntL238.getEntityData().get(GrowlingManEntity.DATA_HorizontalClimbing)) {
							entity.setYRot(0.0F);
							entity.setXRot(entity.getXRot());
							entity.setYBodyRot(entity.getYRot());
							entity.setYHeadRot(entity.getYRot());
							entity.yRotO = entity.getYRot();
							entity.xRotO = entity.getXRot();
							if (entity instanceof LivingEntity _entity) {
								_entity.yBodyRotO = _entity.getYRot();
								_entity.yHeadRotO = _entity.getYRot();
							}
						}
					} else if (!world.isEmptyBlock(BlockPos.containing(x, y + 3.6, z - 0.5))
						&& GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 5.0, 5.0, 5.0)).isEmpty()
						&& entity.getDirection() == Direction.NORTH
						&& entity.getDeltaMovement().z() == 0.1
						&& entity instanceof GrowlingManEntity _datEntL246
						&& (Boolean)_datEntL246.getEntityData().get(GrowlingManEntity.DATA_HorizontalClimbing)) {
						entity.setYRot(180.0F);
						entity.setXRot(entity.getXRot());
						entity.setYBodyRot(entity.getYRot());
						entity.setYHeadRot(entity.getYRot());
						entity.yRotO = entity.getYRot();
						entity.xRotO = entity.getXRot();
						if (entity instanceof LivingEntity _entity) {
							_entity.yBodyRotO = _entity.getYRot();
							_entity.yHeadRotO = _entity.getYRot();
						}
					}
				} else if (entity.getDeltaMovement().x() == -0.1
					&& entity instanceof GrowlingManEntity _datEntL222
					&& (Boolean)_datEntL222.getEntityData().get(GrowlingManEntity.DATA_HorizontalClimbing)) {
					entity.setYRot(-90.0F);
					entity.setXRot(entity.getXRot());
					entity.setYBodyRot(entity.getYRot());
					entity.setYHeadRot(entity.getYRot());
					entity.yRotO = entity.getYRot();
					entity.xRotO = entity.getXRot();
					if (entity instanceof LivingEntity _entity) {
						_entity.yBodyRotO = _entity.getYRot();
						_entity.yHeadRotO = _entity.getYRot();
					}
				}
			}
		}
	}
}
