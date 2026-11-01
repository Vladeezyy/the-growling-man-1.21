package net.voxla.growlingman.procedures;

import java.text.DecimalFormat;
import net.minecraft.world.entity.Entity;
import net.voxla.growlingman.network.GrowlingManModVariables;

public class DevAnimalKillCooldownthingProcedure {
	public static String execute(Entity entity) {
		return entity == null
			? ""
			: "Animals Killed Reset Time: "
				+ new DecimalFormat("########")
					.format(
						0.0
							+ GrowlingManModVariables.of(entity)
								.AnimalsKilledResetWait
					);
	}
}
