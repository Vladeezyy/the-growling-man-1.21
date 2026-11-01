package net.voxla.growlingman.procedures;

import net.minecraft.world.entity.Entity;
import net.voxla.growlingman.network.GrowlingManModVariables;

public class HoldBreathKeyOnKeyReleasedProcedure {
	public static void execute(Entity entity) {
		if (entity != null) {
			if (GrowlingManModVariables.of(entity)
				.IsHoldingBreath) {
				boolean _setval = false;
				{
					GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
					_vars.IsHoldingBreath = _setval;
					_vars.syncPlayerVariables(entity);
				}
			}
		}
	}
}
