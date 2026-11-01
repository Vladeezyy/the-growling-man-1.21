package net.voxla.growlingman.procedures;

import net.minecraft.world.entity.Entity;
import net.voxla.growlingman.entity.GrowlingManEntity;

public class AI0Procedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if ((entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_AIIdleTimer) : 0) < 1200
            && entity instanceof GrowlingManEntity _datEntSetI) {
            _datEntSetI.getEntityData()
               .set(
                  GrowlingManEntity.DATA_AIIdleTimer,
                  (entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_AIIdleTimer) : 0) + 1
               );
         }

         if ((entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_AIIdleTimer) : 0) >= 1200) {
            if ((entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer) : 0) == 0
               && entity instanceof GrowlingManEntity _datEntSetI) {
               _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_DeManifestTimer, 1);
            }

            if (entity instanceof GrowlingManEntity _datEntSetI) {
               _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_AIIdleTimer, 0);
            }
         }
      }
   }
}
