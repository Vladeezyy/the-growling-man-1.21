package net.voxla.growlingman.procedures;

import java.util.Comparator;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.voxla.growlingman.entity.GrowlingManEntity;
import net.voxla.growlingman.network.GrowlingManModVariables;

public class AI1Procedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_AIIdleTimer) : 0) < 3600
            && entity instanceof GrowlingManEntity _datEntSetI) {
            _datEntSetI.getEntityData()
               .set(
                  GrowlingManEntity.DATA_AIIdleTimer,
                  (entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_AIIdleTimer) : 0) + 1
               );
         }

         if ((entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_AIIdleTimer) : 0) >= 3600) {
            if (entity instanceof GrowlingManEntity _datEntSetI) {
               _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_AIIdleTimer, 0);
            }

            if (Math.random() > 0.6) {
               if (entity instanceof GrowlingManEntity _datEntSetI) {
                  _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_AI, Mth.nextInt(RandomSource.create(), 1, 3));
               }
            } else if (Math.random() > 0.6) {
               Vec3 _center = new Vec3(x, y, z);

               for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(150.0), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if (entityiterator instanceof Player
                     && ((GrowlingManModVariables.PlayerVariables)entity.getCapability(GrowlingManModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                              .orElse(new GrowlingManModVariables.PlayerVariables()))
                           .IsMarked
                        > 0.0) {
                     if (Math.random() > 0.6) {
                        if (entity instanceof GrowlingManEntity _datEntSetI) {
                           _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_AI, 10);
                        }
                     } else if (!(entity instanceof GrowlingManEntity _datEntL9 && (Boolean)_datEntL9.getEntityData().get(GrowlingManEntity.DATA_Chasing))
                        && entity instanceof GrowlingManEntity _datEntSetL) {
                        _datEntSetL.getEntityData().set(GrowlingManEntity.DATA_Chasing, true);
                     }
                     break;
                  }

                  if (entityiterator instanceof Player
                     && ((GrowlingManModVariables.PlayerVariables)entity.getCapability(GrowlingManModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                              .orElse(new GrowlingManModVariables.PlayerVariables()))
                           .IsMarked
                        <= 0.0) {
                     if (Math.random() > 0.6) {
                        if (entity instanceof GrowlingManEntity _datEntSetI) {
                           _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_AI, Mth.nextInt(RandomSource.create(), 1, 3));
                        }
                     } else if ((
                              entity instanceof GrowlingManEntity _datEntIx
                                 ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer)
                                 : 0
                           )
                           == 0
                        && entity instanceof GrowlingManEntity _datEntSetI) {
                        _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_DeManifestTimer, 1);
                     }
                     break;
                  }
               }
            } else if ((entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer) : 0)
                  == 0
               && entity instanceof GrowlingManEntity _datEntSetI) {
               _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_DeManifestTimer, 1);
            }
         }
      }
   }
}
