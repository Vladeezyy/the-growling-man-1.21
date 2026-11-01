package net.voxla.growlingman.procedures;

import net.voxla.growlingman.port.GMEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.voxla.growlingman.entity.GrowlingManEntity;

public class GMSneakProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity != null) {
			if (!world.getBlockState(BlockPos.containing(x, y + 0.0, z)).canOcclude()
				&& !world.getBlockState(BlockPos.containing(x, y + 1.0, z)).canOcclude()
				&& (
					world.getBlockState(BlockPos.containing(x, y + 2.0, z)).canOcclude()
						|| world.getBlockState(BlockPos.containing(x, y + 2.0, z)).is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.parse("minecraft:leaves")))
						|| world.getBlockState(BlockPos.containing(x, y + 3.0, z)).canOcclude()
						|| world.getBlockState(BlockPos.containing(x, y + 3.0, z)).is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.parse("minecraft:leaves")))
				)
				&& !(entity instanceof GrowlingManEntity _datEntL8 && (Boolean)_datEntL8.getEntityData().get(GrowlingManEntity.DATA_HorizontalClimbing))) {
				if (!entity.isShiftKeyDown()) {
					entity.setShiftKeyDown(true);
				}
			} else if (entity.isShiftKeyDown()) {
				entity.setShiftKeyDown(false);
			}

			if (!world.getBlockState(BlockPos.containing(x - entity.getLookAngle().z * 1.3, y + 1.0, z + entity.getLookAngle().x * 1.3)).canOcclude()
				&& !world.getBlockState(BlockPos.containing(x + entity.getLookAngle().z * 1.3, y + 1.0, z - entity.getLookAngle().x * 1.3)).canOcclude()) {
				if (entity instanceof GrowlingManEntity _datEntL21
					&& (Boolean)_datEntL21.getEntityData().get(GrowlingManEntity.DATA_FoldInArms)
					&& entity instanceof GrowlingManEntity _datEntSetL) {
					_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_FoldInArms, false);
				}
			} else if (!(entity instanceof GrowlingManEntity _datEntL19 && (Boolean)_datEntL19.getEntityData().get(GrowlingManEntity.DATA_FoldInArms))
				&& entity instanceof GrowlingManEntity _datEntSetL) {
				_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_FoldInArms, true);
			}

			if (entity.isShiftKeyDown()) {
				if ((
						entity instanceof GrowlingManEntity _datEntL24 && (Boolean)_datEntL24.getEntityData().get(GrowlingManEntity.DATA_Chasing)
							|| entity instanceof GrowlingManEntity _datEntL25 && (Boolean)_datEntL25.getEntityData().get(GrowlingManEntity.DATA_IsBeingSeen)
							|| (
									!(entity instanceof GrowlingManEntity _datEntL26 && (Boolean)_datEntL26.getEntityData().get(GrowlingManEntity.DATA_PlayerNorth))
										|| !world.getBlockState(BlockPos.containing(x + 0.0, y + 1.0, z - 1.0)).canOcclude()
								)
								&& (
									!(entity instanceof GrowlingManEntity _datEntL28 && (Boolean)_datEntL28.getEntityData().get(GrowlingManEntity.DATA_PlayerSouth))
										|| !world.getBlockState(BlockPos.containing(x + 0.0, y + 1.0, z + 1.0)).canOcclude()
								)
								&& (
									!(entity instanceof GrowlingManEntity _datEntL30 && (Boolean)_datEntL30.getEntityData().get(GrowlingManEntity.DATA_PlayerEast))
										|| !world.getBlockState(BlockPos.containing(x + 1.0, y + 1.0, z + 0.0)).canOcclude()
								)
								&& (
									!(entity instanceof GrowlingManEntity _datEntL32 && (Boolean)_datEntL32.getEntityData().get(GrowlingManEntity.DATA_PlayerWest))
										|| !world.getBlockState(BlockPos.containing(x - 1.0, y + 1.0, z + 0.0)).canOcclude()
								)
					)
					&& (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) != 11) {
					if (entity instanceof GrowlingManEntity _datEntL37 && (Boolean)_datEntL37.getEntityData().get(GrowlingManEntity.DATA_FoldInArms)) {
						if (entity instanceof GrowlingManEntity _datEntL38 && (Boolean)_datEntL38.getEntityData().get(GrowlingManEntity.DATA_Chasing)) {
							if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_IdleAnimation) : "")
									.equals("animation.growling_man.stareshakecrouchar")
								&& entity instanceof GrowlingManEntity _datEntSetS) {
								_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_IdleAnimation, "animation.growling_man.stareshakecrouchar");
							}
						} else if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_IdleAnimation) : "")
								.equals("animation.growling_man.idlecrouchar")
							&& entity instanceof GrowlingManEntity _datEntSetS) {
							_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_IdleAnimation, "animation.growling_man.idlecrouchar");
						}
					} else if (entity instanceof GrowlingManEntity _datEntL43 && (Boolean)_datEntL43.getEntityData().get(GrowlingManEntity.DATA_Chasing)) {
						if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_IdleAnimation) : "")
								.equals("animation.growling_man.stareshakecrouch")
							&& entity instanceof GrowlingManEntity _datEntSetS) {
							_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_IdleAnimation, "animation.growling_man.stareshakecrouch");
						}
					} else if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_IdleAnimation) : "")
							.equals("animation.growling_man.idlecrouch")
						&& entity instanceof GrowlingManEntity _datEntSetS) {
						_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_IdleAnimation, "animation.growling_man.idlecrouch");
					}
				} else if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_IdleAnimation) : "")
						.equals("animation.growling_man.hidingcrouch")
					&& entity instanceof GrowlingManEntity _datEntSetS) {
					_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_IdleAnimation, "animation.growling_man.hidingcrouch");
				}

				if (entity instanceof GrowlingManEntity _datEntL48 && (Boolean)_datEntL48.getEntityData().get(GrowlingManEntity.DATA_FoldInArms)) {
					if (entity instanceof GrowlingManEntity _datEntL49 && (Boolean)_datEntL49.getEntityData().get(GrowlingManEntity.DATA_Chasing)) {
						if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_WalkingAnimation) : "")
								.equals("animation.growling_man.movecrouchar")
							&& entity instanceof GrowlingManEntity _datEntSetS) {
							_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_WalkingAnimation, "animation.growling_man.movecrouchar");
						}

						if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_RunningAnimation) : "")
								.equals("animation.growling_man.movefastcroucharag")
							&& entity instanceof GrowlingManEntity _datEntSetS) {
							_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_RunningAnimation, "animation.growling_man.movefastcroucharag");
						}
					} else {
						if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_WalkingAnimation) : "")
								.equals("animation.growling_man.movecrouchar")
							&& entity instanceof GrowlingManEntity _datEntSetS) {
							_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_WalkingAnimation, "animation.growling_man.movecrouchar");
						}

						if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_RunningAnimation) : "")
								.equals("animation.growling_man.movefastcrouchar")
							&& entity instanceof GrowlingManEntity _datEntSetS) {
							_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_RunningAnimation, "animation.growling_man.movefastcrouchar");
						}
					}
				} else if (entity instanceof GrowlingManEntity _datEntL58 && (Boolean)_datEntL58.getEntityData().get(GrowlingManEntity.DATA_Chasing)) {
					if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_WalkingAnimation) : "")
							.equals("animation.growling_man.movecrouch")
						&& entity instanceof GrowlingManEntity _datEntSetS) {
						_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_WalkingAnimation, "animation.growling_man.movecrouch");
					}

					if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_RunningAnimation) : "")
							.equals("animation.growling_man.movefastcrouchag")
						&& entity instanceof GrowlingManEntity _datEntSetS) {
						_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_RunningAnimation, "animation.growling_man.movefastcrouchag");
					}
				} else {
					if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_WalkingAnimation) : "")
							.equals("animation.growling_man.movecrouch")
						&& entity instanceof GrowlingManEntity _datEntSetS) {
						_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_WalkingAnimation, "animation.growling_man.movecrouch");
					}

					if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_RunningAnimation) : "")
							.equals("animation.growling_man.movefastcrouch")
						&& entity instanceof GrowlingManEntity _datEntSetS) {
						_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_RunningAnimation, "animation.growling_man.movefastcrouch");
					}
				}
			} else if (!entity.isShiftKeyDown()) {
				if ((
						entity instanceof GrowlingManEntity _datEntL68 && (Boolean)_datEntL68.getEntityData().get(GrowlingManEntity.DATA_Chasing)
							|| entity instanceof GrowlingManEntity _datEntL69 && (Boolean)_datEntL69.getEntityData().get(GrowlingManEntity.DATA_IsBeingSeen)
							|| (
									!(entity instanceof GrowlingManEntity _datEntL70 && (Boolean)_datEntL70.getEntityData().get(GrowlingManEntity.DATA_PlayerNorth))
										|| !world.getBlockState(BlockPos.containing(x + 0.0, y + 1.0, z - 1.0)).canOcclude()
								)
								&& (
									!(entity instanceof GrowlingManEntity _datEntL72 && (Boolean)_datEntL72.getEntityData().get(GrowlingManEntity.DATA_PlayerSouth))
										|| !world.getBlockState(BlockPos.containing(x + 0.0, y + 1.0, z + 1.0)).canOcclude()
								)
								&& (
									!(entity instanceof GrowlingManEntity _datEntL74 && (Boolean)_datEntL74.getEntityData().get(GrowlingManEntity.DATA_PlayerEast))
										|| !world.getBlockState(BlockPos.containing(x + 1.0, y + 1.0, z + 0.0)).canOcclude()
								)
								&& (
									!(entity instanceof GrowlingManEntity _datEntL76 && (Boolean)_datEntL76.getEntityData().get(GrowlingManEntity.DATA_PlayerWest))
										|| !world.getBlockState(BlockPos.containing(x - 1.0, y + 1.0, z + 0.0)).canOcclude()
								)
					)
					&& (entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) != 11) {
					if (entity instanceof GrowlingManEntity _datEntL81 && (Boolean)_datEntL81.getEntityData().get(GrowlingManEntity.DATA_FoldInArms)) {
						if (entity instanceof GrowlingManEntity _datEntL82 && (Boolean)_datEntL82.getEntityData().get(GrowlingManEntity.DATA_Chasing)) {
							if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_IdleAnimation) : "")
									.equals("animation.growling_man.stareshakear")
								&& entity instanceof GrowlingManEntity _datEntSetS) {
								_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_IdleAnimation, "animation.growling_man.stareshakear");
							}
						} else if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_IdleAnimation) : "")
								.equals("animation.growling_man.idlear")
							&& entity instanceof GrowlingManEntity _datEntSetS) {
							_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_IdleAnimation, "animation.growling_man.idlear");
						}
					} else if (entity instanceof GrowlingManEntity _datEntL87 && (Boolean)_datEntL87.getEntityData().get(GrowlingManEntity.DATA_Chasing)) {
						if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_IdleAnimation) : "")
								.equals("animation.growling_man.stareshake")
							&& entity instanceof GrowlingManEntity _datEntSetS) {
							_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_IdleAnimation, "animation.growling_man.stareshake");
						}
					} else if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_IdleAnimation) : "")
							.equals("animation.growling_man.idle")
						&& entity instanceof GrowlingManEntity _datEntSetS) {
						_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_IdleAnimation, "animation.growling_man.idle");
					}
				} else if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_IdleAnimation) : "")
						.equals("animation.growling_man.hiding")
					&& entity instanceof GrowlingManEntity _datEntSetS) {
					_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_IdleAnimation, "animation.growling_man.hiding");
				}

				if (entity instanceof GrowlingManEntity _datEntL92 && (Boolean)_datEntL92.getEntityData().get(GrowlingManEntity.DATA_FoldInArms)) {
					if (entity instanceof GrowlingManEntity _datEntL93 && (Boolean)_datEntL93.getEntityData().get(GrowlingManEntity.DATA_Chasing)) {
						if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_WalkingAnimation) : "")
								.equals("animation.growling_man.movear")
							&& entity instanceof GrowlingManEntity _datEntSetS) {
							_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_WalkingAnimation, "animation.growling_man.movear");
						}

						if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_RunningAnimation) : "")
								.equals("animation.growling_man.movefastarag")
							&& entity instanceof GrowlingManEntity _datEntSetS) {
							_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_RunningAnimation, "animation.growling_man.movefastarag");
						}
					} else {
						if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_WalkingAnimation) : "")
								.equals("animation.growling_man.movear")
							&& entity instanceof GrowlingManEntity _datEntSetS) {
							_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_WalkingAnimation, "animation.growling_man.movear");
						}

						if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_RunningAnimation) : "")
								.equals("animation.growling_man.movefastar")
							&& entity instanceof GrowlingManEntity _datEntSetS) {
							_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_RunningAnimation, "animation.growling_man.movefastar");
						}
					}
				} else if (entity instanceof GrowlingManEntity _datEntL102 && (Boolean)_datEntL102.getEntityData().get(GrowlingManEntity.DATA_Chasing)) {
					if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_WalkingAnimation) : "")
							.equals("animation.growling_man.move")
						&& entity instanceof GrowlingManEntity _datEntSetS) {
						_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_WalkingAnimation, "animation.growling_man.move");
					}

					if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_RunningAnimation) : "")
							.equals("animation.growling_man.movefastag")
						&& entity instanceof GrowlingManEntity _datEntSetS) {
						_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_RunningAnimation, "animation.growling_man.movefastag");
					}
				} else {
					if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_WalkingAnimation) : "")
							.equals("animation.growling_man.move")
						&& entity instanceof GrowlingManEntity _datEntSetS) {
						_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_WalkingAnimation, "animation.growling_man.move");
					}

					if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_RunningAnimation) : "")
							.equals("animation.growling_man.movefast")
						&& entity instanceof GrowlingManEntity _datEntSetS) {
						_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_RunningAnimation, "animation.growling_man.movefast");
					}
				}
			}

			if (entity instanceof GrowlingManEntity) {
				if (world.getBlockState(BlockPos.containing(x + 1.0, y + 0.0, z)).canOcclude()
					&& world.getBlockState(BlockPos.containing(x + 1.0, y + 1.0, z)).canOcclude()
					&& world.getBlockState(BlockPos.containing(x + 1.0, y + 2.0, z)).canOcclude()
					&& GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 5.0, 5.0, 5.0)).isEmpty()
					&& entity.getDirection() == Direction.EAST) {
					if (entity.getDeltaMovement().y() == 0.2
						&& entity instanceof GrowlingManEntity _datEntL119
						&& (Boolean)_datEntL119.getEntityData().get(GrowlingManEntity.DATA_Climbing)) {
						if (!(entity instanceof GrowlingManEntity _datEntL120 && (Boolean)_datEntL120.getEntityData().get(GrowlingManEntity.DATA_PlayEventAnimation))
							&& entity instanceof GrowlingManEntity _datEntSetL) {
							_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayEventAnimation, true);
						}

						if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_EventAnimation) : "")
								.equals("animation.growling_man.climb")
							&& entity instanceof GrowlingManEntity _datEntSetS) {
							_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_EventAnimation, "animation.growling_man.climb");
						}
					}
				} else if (world.getBlockState(BlockPos.containing(x - 1.0, y + 0.0, z)).canOcclude()
					&& world.getBlockState(BlockPos.containing(x - 1.0, y + 1.0, z)).canOcclude()
					&& world.getBlockState(BlockPos.containing(x - 1.0, y + 2.0, z)).canOcclude()
					&& GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 5.0, 5.0, 5.0)).isEmpty()
					&& entity.getDirection() == Direction.WEST) {
					if (entity.getDeltaMovement().y() == 0.2
						&& entity instanceof GrowlingManEntity _datEntL131
						&& (Boolean)_datEntL131.getEntityData().get(GrowlingManEntity.DATA_Climbing)) {
						if (!(entity instanceof GrowlingManEntity _datEntL132 && (Boolean)_datEntL132.getEntityData().get(GrowlingManEntity.DATA_PlayEventAnimation))
							&& entity instanceof GrowlingManEntity _datEntSetL) {
							_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayEventAnimation, true);
						}

						if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_EventAnimation) : "")
								.equals("animation.growling_man.climb")
							&& entity instanceof GrowlingManEntity _datEntSetS) {
							_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_EventAnimation, "animation.growling_man.climb");
						}
					}
				} else if (world.getBlockState(BlockPos.containing(x, y + 0.0, z + 1.0)).canOcclude()
					&& world.getBlockState(BlockPos.containing(x, y + 1.0, z + 1.0)).canOcclude()
					&& world.getBlockState(BlockPos.containing(x, y + 2.0, z + 1.0)).canOcclude()
					&& GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 5.0, 5.0, 5.0)).isEmpty()
					&& entity.getDirection() == Direction.DOWN) {
					if (entity.getDeltaMovement().y() == 0.2
						&& entity instanceof GrowlingManEntity _datEntL143
						&& (Boolean)_datEntL143.getEntityData().get(GrowlingManEntity.DATA_Climbing)) {
						if (!(entity instanceof GrowlingManEntity _datEntL144 && (Boolean)_datEntL144.getEntityData().get(GrowlingManEntity.DATA_PlayEventAnimation))
							&& entity instanceof GrowlingManEntity _datEntSetL) {
							_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayEventAnimation, true);
						}

						if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_EventAnimation) : "")
								.equals("animation.growling_man.climb")
							&& entity instanceof GrowlingManEntity _datEntSetS) {
							_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_EventAnimation, "animation.growling_man.climb");
						}
					}
				} else if (!world.getBlockState(BlockPos.containing(x, y + 0.0, z - 1.0)).canOcclude()
					|| !world.getBlockState(BlockPos.containing(x, y + 1.0, z - 1.0)).canOcclude()
					|| !world.getBlockState(BlockPos.containing(x, y + 2.0, z - 1.0)).canOcclude()
					|| !GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 5.0, 5.0, 5.0)).isEmpty()
					|| entity.getDirection() != Direction.NORTH) {
					if (world.isEmptyBlock(BlockPos.containing(x + 0.5, y + 3.6, z))
						|| !GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 5.0, 5.0, 5.0)).isEmpty()
						|| entity.getDirection() != Direction.EAST) {
						if (!world.isEmptyBlock(BlockPos.containing(x - 0.5, y + 3.6, z))
							&& GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 5.0, 5.0, 5.0)).isEmpty()
							&& entity.getDirection() == Direction.WEST) {
							if (entity.getDeltaMovement().x() == -0.1
								&& entity instanceof GrowlingManEntity _datEntL175
								&& (Boolean)_datEntL175.getEntityData().get(GrowlingManEntity.DATA_HorizontalClimbing)) {
								if (!(
										entity instanceof GrowlingManEntity _datEntL176
											&& (Boolean)_datEntL176.getEntityData().get(GrowlingManEntity.DATA_PlayEventAnimation)
									)
									&& entity instanceof GrowlingManEntity _datEntSetL) {
									_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayEventAnimation, true);
								}

								if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_EventAnimation) : "")
										.equals("animation.growling_man.climbh")
									&& entity instanceof GrowlingManEntity _datEntSetS) {
									_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_EventAnimation, "animation.growling_man.climbh");
								}
							}
						} else if (!world.isEmptyBlock(BlockPos.containing(x, y + 3.6, z + 0.5))
							&& GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 5.0, 5.0, 5.0)).isEmpty()
							&& entity.getDirection() == Direction.SOUTH) {
							if (entity.getDeltaMovement().x() == -0.1
								&& entity instanceof GrowlingManEntity _datEntL185
								&& (Boolean)_datEntL185.getEntityData().get(GrowlingManEntity.DATA_HorizontalClimbing)) {
								if (!(
										entity instanceof GrowlingManEntity _datEntL186
											&& (Boolean)_datEntL186.getEntityData().get(GrowlingManEntity.DATA_PlayEventAnimation)
									)
									&& entity instanceof GrowlingManEntity _datEntSetL) {
									_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayEventAnimation, true);
								}

								if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_EventAnimation) : "")
										.equals("animation.growling_man.climbh")
									&& entity instanceof GrowlingManEntity _datEntSetS) {
									_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_EventAnimation, "animation.growling_man.climbh");
								}
							}
						} else if (!world.isEmptyBlock(BlockPos.containing(x, y + 3.6, z - 0.5))
							&& GMEntities.players(world, AABB.ofSize(new Vec3(x, y, z), 5.0, 5.0, 5.0)).isEmpty()
							&& entity.getDirection() == Direction.NORTH) {
							if (entity.getDeltaMovement().x() == -0.1
								&& entity instanceof GrowlingManEntity _datEntL195
								&& (Boolean)_datEntL195.getEntityData().get(GrowlingManEntity.DATA_HorizontalClimbing)) {
								if (!(
										entity instanceof GrowlingManEntity _datEntL196
											&& (Boolean)_datEntL196.getEntityData().get(GrowlingManEntity.DATA_PlayEventAnimation)
									)
									&& entity instanceof GrowlingManEntity _datEntSetL) {
									_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayEventAnimation, true);
								}

								if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_EventAnimation) : "")
										.equals("animation.growling_man.climbh")
									&& entity instanceof GrowlingManEntity _datEntSetS) {
									_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_EventAnimation, "animation.growling_man.climbh");
								}
							}
						} else if ((
								entity instanceof GrowlingManEntity _datEntIxx
									? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_SmackPlayerOffPillar)
									: 0
							)
							>= 20) {
							if (!(
									entity instanceof GrowlingManEntity _datEntL201
										&& (Boolean)_datEntL201.getEntityData().get(GrowlingManEntity.DATA_PlayEventAnimation)
								)
								&& entity instanceof GrowlingManEntity _datEntSetL) {
								_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayEventAnimation, true);
							}

							if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_EventAnimation) : "")
									.equals("animation.growling_man.hitpillar")
								&& entity instanceof GrowlingManEntity _datEntSetS) {
								_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_EventAnimation, "animation.growling_man.hitpillar");
							}
						} else if ((
									entity instanceof GrowlingManEntity _datEntIxxx
										? (Integer)_datEntIxxx.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent)
										: 0
								)
								> 6
							&& (
									entity instanceof GrowlingManEntity _datEntIxx
										? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent)
										: 0
								)
								< 150) {
							if (!(
									entity instanceof GrowlingManEntity _datEntL207
										&& (Boolean)_datEntL207.getEntityData().get(GrowlingManEntity.DATA_PlayEventAnimation)
								)
								&& entity instanceof GrowlingManEntity _datEntSetL) {
								_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayEventAnimation, true);
							}

							if (entity.isShiftKeyDown()) {
								if (entity instanceof GrowlingManEntity _datEntL210 && (Boolean)_datEntL210.getEntityData().get(GrowlingManEntity.DATA_FoldInArms)) {
									if (!(entity instanceof GrowlingManEntity _datEntS
												? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_EventAnimation)
												: "")
											.equals("animation.growling_man.stareshakecrouchar")
										&& entity instanceof GrowlingManEntity _datEntSetS) {
										_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_EventAnimation, "animation.growling_man.stareshakecrouchar");
									}
								} else if (!(entity instanceof GrowlingManEntity _datEntS
											? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_EventAnimation)
											: "")
										.equals("animation.growling_man.stareshakecrouch")
									&& entity instanceof GrowlingManEntity _datEntSetS) {
									_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_EventAnimation, "animation.growling_man.stareshakecrouch");
								}
							} else if (!entity.isShiftKeyDown()) {
								if (entity instanceof GrowlingManEntity _datEntL216 && (Boolean)_datEntL216.getEntityData().get(GrowlingManEntity.DATA_FoldInArms)) {
									if (!(entity instanceof GrowlingManEntity _datEntS
												? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_EventAnimation)
												: "")
											.equals("animation.growling_man.stareshakear")
										&& entity instanceof GrowlingManEntity _datEntSetS) {
										_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_EventAnimation, "animation.growling_man.stareshakear");
									}
								} else if (!(entity instanceof GrowlingManEntity _datEntS
											? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_EventAnimation)
											: "")
										.equals("animation.growling_man.stareshake")
									&& entity instanceof GrowlingManEntity _datEntSetS) {
									_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_EventAnimation, "animation.growling_man.stareshake");
								}
							}
						} else if ((
									entity instanceof GrowlingManEntity _datEntIxx
										? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent)
										: 0
								)
								< 400
							|| entity.getDeltaMovement().x() == 0.0 && entity.getDeltaMovement().z() == 0.0) {
							if (entity instanceof GrowlingManEntity _datEntL232
								&& (Boolean)_datEntL232.getEntityData().get(GrowlingManEntity.DATA_PlayEventAnimation)
								&& entity instanceof GrowlingManEntity _datEntSetL) {
								_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayEventAnimation, false);
							}
						} else {
							if (!(
									entity instanceof GrowlingManEntity _datEntL224
										&& (Boolean)_datEntL224.getEntityData().get(GrowlingManEntity.DATA_PlayEventAnimation)
								)
								&& entity instanceof GrowlingManEntity _datEntSetL) {
								_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayEventAnimation, true);
							}

							if (entity.isShiftKeyDown()) {
								if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_EventAnimation) : "")
										.equals("animation.growling_man.lungecrouch")
									&& entity instanceof GrowlingManEntity _datEntSetS) {
									_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_EventAnimation, "animation.growling_man.lungecrouch");
								}
							} else if (!entity.isShiftKeyDown()
								&& !(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_EventAnimation) : "")
									.equals("animation.growling_man.lunge")
								&& entity instanceof GrowlingManEntity _datEntSetS) {
								_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_EventAnimation, "animation.growling_man.lunge");
							}
						}
					} else if (entity.getDeltaMovement().x() == -0.1
						&& entity instanceof GrowlingManEntity _datEntL165
						&& (Boolean)_datEntL165.getEntityData().get(GrowlingManEntity.DATA_HorizontalClimbing)) {
						if (!(entity instanceof GrowlingManEntity _datEntL166 && (Boolean)_datEntL166.getEntityData().get(GrowlingManEntity.DATA_PlayEventAnimation))
							&& entity instanceof GrowlingManEntity _datEntSetL) {
							_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayEventAnimation, true);
						}

						if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_EventAnimation) : "")
								.equals("animation.growling_man.climbh")
							&& entity instanceof GrowlingManEntity _datEntSetS) {
							_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_EventAnimation, "animation.growling_man.climbh");
						}
					}
				} else if (entity.getDeltaMovement().y() == 0.2
					&& entity instanceof GrowlingManEntity _datEntL155
					&& (Boolean)_datEntL155.getEntityData().get(GrowlingManEntity.DATA_Climbing)) {
					if (!(entity instanceof GrowlingManEntity _datEntL156 && (Boolean)_datEntL156.getEntityData().get(GrowlingManEntity.DATA_PlayEventAnimation))
						&& entity instanceof GrowlingManEntity _datEntSetL) {
						_datEntSetL.getEntityData().set(GrowlingManEntity.DATA_PlayEventAnimation, true);
					}

					if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_EventAnimation) : "")
							.equals("animation.growling_man.climb")
						&& entity instanceof GrowlingManEntity _datEntSetS) {
						_datEntSetS.getEntityData().set(GrowlingManEntity.DATA_EventAnimation, "animation.growling_man.climb");
					}
				}
			}
		}
	}
}
