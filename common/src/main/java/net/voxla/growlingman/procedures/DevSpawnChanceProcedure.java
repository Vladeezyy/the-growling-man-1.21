package net.voxla.growlingman.procedures;

import java.text.DecimalFormat;
import net.minecraft.world.level.LevelAccessor;
import net.voxla.growlingman.network.GrowlingManModVariables;

public class DevSpawnChanceProcedure {
	public static String execute(LevelAccessor world) {
		return "Spawn Chance: " + new DecimalFormat("########").format(0.0 + GrowlingManModVariables.WorldVariables.get(world).GMSpawnChance);
	}
}
