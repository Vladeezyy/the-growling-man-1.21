package net.voxla.growlingman.procedures;

import java.text.DecimalFormat;
import net.minecraft.world.entity.Entity;
import net.voxla.growlingman.network.GrowlingManModVariables;

public class DevAnimalsKilledProcedure {
	public static String execute(Entity entity) {
		return entity == null
			? ""
			: "Animals Killed: "
				+ new DecimalFormat("########")
					.format(
						0.0
							+ GrowlingManModVariables.of(entity)
								.AnimalsKilled
					);
	}
}
