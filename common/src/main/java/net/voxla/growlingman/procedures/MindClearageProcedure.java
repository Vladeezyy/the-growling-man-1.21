package net.voxla.growlingman.procedures;

import java.text.DecimalFormat;
import net.minecraft.world.entity.Entity;
import net.voxla.growlingman.network.GrowlingManModVariables;

public class MindClearageProcedure {
   public static String execute(Entity entity) {
      return entity == null
         ? ""
         : "Mind Clearing Timer: "
            + new DecimalFormat("####")
               .format(
                  0.0
                     + ((GrowlingManModVariables.PlayerVariables)entity.getCapability(GrowlingManModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                           .orElse(new GrowlingManModVariables.PlayerVariables()))
                        .HoldingBreathTimer
               );
   }
}
