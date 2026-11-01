package net.voxla.growlingman.procedures;

import net.minecraft.world.entity.Entity;
import net.voxla.growlingman.network.GrowlingManModVariables;

public class HoldBreathKeyOnKeyReleasedProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (((GrowlingManModVariables.PlayerVariables)entity.getCapability(GrowlingManModVariables.PLAYER_VARIABLES_CAPABILITY, null)
               .orElse(new GrowlingManModVariables.PlayerVariables()))
            .IsHoldingBreath) {
            boolean _setval = false;
            entity.getCapability(GrowlingManModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
               capability.IsHoldingBreath = _setval;
               capability.syncPlayerVariables(entity);
            });
         }
      }
   }
}
