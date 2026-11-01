package net.voxla.growlingman.procedures;

import net.minecraft.world.entity.Entity;
import net.voxla.growlingman.entity.GrowlingManEntity;

public class GMWanderCondProcedure {
	public static boolean execute(Entity entity) {
		return entity == null
			? false
			: (entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_WalkTarget) : "").equals("")
				&& !(entity instanceof GrowlingManEntity _datEntL1 && (Boolean)_datEntL1.getEntityData().get(GrowlingManEntity.DATA_Chasing));
	}
}
