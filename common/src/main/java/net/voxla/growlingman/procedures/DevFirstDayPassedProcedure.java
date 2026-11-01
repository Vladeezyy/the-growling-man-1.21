package net.voxla.growlingman.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.voxla.growlingman.network.GrowlingManModVariables;

public class DevFirstDayPassedProcedure {
	public static String execute(LevelAccessor world) {
		return "First Day Passed: " + GrowlingManModVariables.WorldVariables.get(world).HasOneDayPassed;
	}
}
