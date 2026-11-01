package net.voxla.growlingman.procedures;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.voxla.growlingman.init.GrowlingManModMobEffects;

public class MentalBreakdownOverlayDisplayOverlayIngameProcedure {
	public static boolean execute(Entity entity) {
		return entity == null ? false : entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder());
	}
}
