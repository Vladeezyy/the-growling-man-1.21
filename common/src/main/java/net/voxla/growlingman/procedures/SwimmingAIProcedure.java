package net.voxla.growlingman.procedures;

import java.util.Comparator;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SwimmingAIProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity != null) {
			Vec3 _center = new Vec3(x, y, z);

			for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(50.0), e -> true)
				.stream()
				.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
				.toList()) {
				if ((
						world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.WATER
							|| world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.WATER
					)
					&& (entity instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null) != null
					&& (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null).isAlive()
					&& entityiterator == (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null)) {
					if (!world.isClientSide()) {
						entity.lookAt(
							Anchor.EYES,
							new Vec3(
								entityiterator.getX(),
								entityiterator.getY() + entityiterator.getBbHeight() - entityiterator.getBbHeight() / 5.0F,
								entityiterator.getZ()
							)
						);
					}

					if (!entityiterator.isPassenger()
						|| !(entityiterator.getVehicle() instanceof Boat)
							&& !(entityiterator.getVehicle() instanceof ChestBoat)
							&& !(entityiterator.getVehicle() instanceof Minecart)) {
						if ((
								world.getBlockState(BlockPos.containing(x, y + 0.5, z)).getBlock() != Blocks.WATER
									|| world.getBlockState(BlockPos.containing(x, y + 0.5, z)).getBlock() != Blocks.WATER
							)
							&& entityiterator.getY() > entity.getY()) {
							if (!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 9.0, 9.0, 9.0), e -> true).isEmpty()) {
								entity.setDeltaMovement(new Vec3(entity.getLookAngle().x / 7.0, entity.getDeltaMovement().y(), entity.getLookAngle().z / 7.0));
							} else {
								entity.setDeltaMovement(new Vec3(entity.getLookAngle().x / 6.0, entity.getDeltaMovement().y(), entity.getLookAngle().z / 6.0));
							}
						} else if ((
								world.getBlockState(BlockPos.containing(x, y + 0.5, z)).getBlock() == Blocks.WATER
									|| world.getBlockState(BlockPos.containing(x, y + 0.5, z)).getBlock() == Blocks.WATER
							)
							&& entityiterator.getY() > entity.getY()) {
							if (!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 9.0, 9.0, 9.0), e -> true).isEmpty()) {
								entity.setDeltaMovement(new Vec3(entity.getLookAngle().x / 7.0, entity.getDeltaMovement().y() + 0.1, entity.getLookAngle().z / 7.0));
							} else {
								entity.setDeltaMovement(new Vec3(entity.getLookAngle().x / 6.0, entity.getDeltaMovement().y() + 0.1, entity.getLookAngle().z / 6.0));
							}
						} else if ((
								world.getBlockState(BlockPos.containing(x, y - 0.5, z)).getBlock() == Blocks.WATER
									|| world.getBlockState(BlockPos.containing(x, y - 0.5, z)).getBlock() == Blocks.WATER
							)
							&& entityiterator.getY() < entity.getY()) {
							if (!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 9.0, 9.0, 9.0), e -> true).isEmpty()) {
								entity.setDeltaMovement(new Vec3(entity.getLookAngle().x / 7.0, entity.getDeltaMovement().y() + -0.1, entity.getLookAngle().z / 7.0));
							} else {
								entity.setDeltaMovement(new Vec3(entity.getLookAngle().x / 6.0, entity.getDeltaMovement().y() + -0.1, entity.getLookAngle().z / 6.0));
							}
						} else if (entityiterator.getY() == entity.getY()) {
							if (!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 9.0, 9.0, 9.0), e -> true).isEmpty()) {
								entity.setDeltaMovement(new Vec3(entity.getLookAngle().x / 7.0, entity.getDeltaMovement().y(), entity.getLookAngle().z / 7.0));
							} else {
								entity.setDeltaMovement(new Vec3(entity.getLookAngle().x / 6.0, entity.getDeltaMovement().y(), entity.getLookAngle().z / 6.0));
							}
						}
					} else if ((
							world.getBlockState(BlockPos.containing(x, y + 0.5, z)).getBlock() != Blocks.WATER
								|| world.getBlockState(BlockPos.containing(x, y + 0.5, z)).getBlock() != Blocks.WATER
						)
						&& entityiterator.getY() > entity.getY()) {
						if (!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 6.0, 6.0, 6.0), e -> true).isEmpty()) {
							entity.setDeltaMovement(new Vec3(entity.getLookAngle().x / 4.0, entity.getDeltaMovement().y(), entity.getLookAngle().z / 4.0));
						} else {
							entity.setDeltaMovement(new Vec3(entity.getLookAngle().x / 3.0, entity.getDeltaMovement().y(), entity.getLookAngle().z / 3.0));
						}
					} else if ((
							world.getBlockState(BlockPos.containing(x, y + 0.5, z)).getBlock() == Blocks.WATER
								|| world.getBlockState(BlockPos.containing(x, y + 0.5, z)).getBlock() == Blocks.WATER
						)
						&& entityiterator.getY() > entity.getY()) {
						if (!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 6.0, 6.0, 6.0), e -> true).isEmpty()) {
							entity.setDeltaMovement(new Vec3(entity.getLookAngle().x / 4.0, entity.getDeltaMovement().y() + 0.1, entity.getLookAngle().z / 4.0));
						} else {
							entity.setDeltaMovement(new Vec3(entity.getLookAngle().x / 3.0, entity.getDeltaMovement().y() + 0.1, entity.getLookAngle().z / 3.0));
						}
					} else if ((
							world.getBlockState(BlockPos.containing(x, y - 0.5, z)).getBlock() == Blocks.WATER
								|| world.getBlockState(BlockPos.containing(x, y - 0.5, z)).getBlock() == Blocks.WATER
						)
						&& entityiterator.getY() < entity.getY()) {
						if (!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 6.0, 6.0, 6.0), e -> true).isEmpty()) {
							entity.setDeltaMovement(new Vec3(entity.getLookAngle().x / 4.0, entity.getDeltaMovement().y() + -0.1, entity.getLookAngle().z / 4.0));
						} else {
							entity.setDeltaMovement(new Vec3(entity.getLookAngle().x / 3.0, entity.getDeltaMovement().y() + -0.1, entity.getLookAngle().z / 4.0));
						}
					} else if (entityiterator.getY() == entity.getY()) {
						if (!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 6.0, 6.0, 6.0), e -> true).isEmpty()) {
							entity.setDeltaMovement(new Vec3(entity.getLookAngle().x / 4.0, entity.getDeltaMovement().y(), entity.getLookAngle().z / 4.0));
						} else {
							entity.setDeltaMovement(new Vec3(entity.getLookAngle().x / 3.0, entity.getDeltaMovement().y(), entity.getLookAngle().z / 3.0));
						}
					}
				}
			}
		}
	}
}
