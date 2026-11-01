package net.voxla.growlingman.procedures;

import net.minecraft.world.entity.Entity;
import net.voxla.growlingman.network.GrowlingManModVariables;

public class ShowMarkConditionProcedure {
	public static boolean execute(Entity entity) {
		return entity == null
			? false
			: GrowlingManModVariables.of(entity)
					.IsMarked
				> 0.0;
	}
}
