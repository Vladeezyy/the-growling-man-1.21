package net.voxla.growlingman.procedures;

import net.voxla.growlingman.port.GMEntities;
import java.util.Comparator;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.voxla.growlingman.entity.GrowlingManEntity;

public class PullCameraEventProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity != null) {
			if (entity instanceof GrowlingManEntity) {
				Vec3 _center = new Vec3(x, y, z);

				for (Entity entityiterator : GMEntities.playersAsEntities(world, new AABB(_center, _center).inflate(100.0))
					.stream()
					.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
					.toList()) {
					if (entityiterator instanceof Player) {
						if (entityiterator instanceof Player) {
							double dx = entity.getX() - entityiterator.getX();
							double dy = entity.getEyeY() - entityiterator.getEyeY();
							double dz = entity.getZ() - entityiterator.getZ();
							double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
							if (distance > 0.0) {
								float yaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
								float pitch = (float)(-Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz))));
								float newYaw = Mth.rotLerp(0.3F, entityiterator.getYRot(), yaw);
								float newPitch = Mth.lerp(0.3F, entityiterator.getXRot(), pitch);
								entityiterator.setYRot(newYaw);
								entityiterator.setXRot(newPitch);
								entityiterator.yRotO = newYaw;
								entityiterator.xRotO = newPitch;
								entityiterator.setYHeadRot(newYaw);
								entityiterator.setYBodyRot(newYaw);
							}
						}
						break;
					}
				}
			}
		}
	}
}
