package net.voxla.growlingman.procedures;

import java.util.Comparator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.voxla.growlingman.entity.GrowlingManEntity;

public class PlayerInGMViewProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 320.0, 320.0, 320.0), e -> true).isEmpty() && entity instanceof GrowlingManEntity) {
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(160.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiterator instanceof Player) {
                  if (entity != null && entityiterator != null) {
                     Vec3 lookVec = entity.getLookAngle().normalize();
                     Vec3 toTarget = entityiterator.position().subtract(entity.position()).normalize();
                     double dot = lookVec.dot(toTarget);
                     boolean inFOV = dot > 0.866;
                     boolean canSee = entity instanceof LivingEntity livingEntity && livingEntity.hasLineOfSight(entityiterator);
                     if (inFOV && canSee) {
                        if (!(entity instanceof GrowlingManEntity _datEntL3 && (Boolean)_datEntL3.getEntityData().get(GrowlingManEntity.DATA_CanSeePlayer))
                           && entity instanceof GrowlingManEntity _datEntSetL) {
                           _datEntSetL.getEntityData().set(GrowlingManEntity.DATA_CanSeePlayer, true);
                        }
                     } else if (entity instanceof GrowlingManEntity _datEntL5
                        && (Boolean)_datEntL5.getEntityData().get(GrowlingManEntity.DATA_CanSeePlayer)
                        && entity instanceof GrowlingManEntity _datEntSetL) {
                        _datEntSetL.getEntityData().set(GrowlingManEntity.DATA_CanSeePlayer, false);
                     }
                  }
                  break;
               }
            }
         }
      }
   }
}
