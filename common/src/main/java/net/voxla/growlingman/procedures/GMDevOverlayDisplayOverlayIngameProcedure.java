package net.voxla.growlingman.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.voxla.growlingman.network.GrowlingManModVariables;

public class GMDevOverlayDisplayOverlayIngameProcedure {
   public static boolean execute(LevelAccessor world) {
      return GrowlingManModVariables.MapVariables.get(world).gmdev_overlay;
   }
}
