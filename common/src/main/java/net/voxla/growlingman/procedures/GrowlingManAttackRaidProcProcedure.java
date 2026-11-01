package net.voxla.growlingman.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.voxla.growlingman.entity.GrowlingManEntity;

public class GrowlingManAttackRaidProcProcedure {
   public static boolean execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      return entity == null
         ? false
         : world instanceof ServerLevel _level0
            && _level0.isRaided(BlockPos.containing(x, y, z))
            && (entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) != 11
            && (entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) != 17
            && (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_AI) : 0) != 18;
   }
}
