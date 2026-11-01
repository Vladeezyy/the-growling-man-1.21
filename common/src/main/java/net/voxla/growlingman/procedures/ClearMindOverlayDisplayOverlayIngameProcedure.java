package net.voxla.growlingman.procedures;

import net.minecraft.world.entity.Entity;
import net.voxla.growlingman.network.GrowlingManModVariables;

public class ClearMindOverlayDisplayOverlayIngameProcedure {
   public static boolean execute(Entity entity) {
      return entity == null
         ? false
         : ((GrowlingManModVariables.PlayerVariables)entity.getCapability(GrowlingManModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                  .orElse(new GrowlingManModVariables.PlayerVariables()))
               .cym_overlay_opacity
            > 0.0;
   }
}
