package net.voxla.growlingman.procedures;

import java.text.DecimalFormat;
import net.minecraft.world.level.LevelAccessor;
import net.voxla.growlingman.network.GrowlingManModVariables;

public class DevSpawnTimeProcedure {
	public static String execute(LevelAccessor world) {
		return "Spawn Timer: " + new DecimalFormat("########").format(0.0 + GrowlingManModVariables.MapVariables.get(world).GMSpawnTimer);
	}
}
