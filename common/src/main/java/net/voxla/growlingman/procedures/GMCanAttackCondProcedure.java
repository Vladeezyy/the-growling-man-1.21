package net.voxla.growlingman.procedures;

import net.minecraft.world.entity.Entity;
import net.voxla.growlingman.entity.GrowlingManEntity;

public class GMCanAttackCondProcedure {
   public static boolean execute(Entity entity) {
      return entity == null
         ? false
         : entity instanceof GrowlingManEntity _datEntL0
            && (Boolean)_datEntL0.getEntityData().get(GrowlingManEntity.DATA_Chasing)
            && (entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) != 17
            && (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) != 18;
   }
}
