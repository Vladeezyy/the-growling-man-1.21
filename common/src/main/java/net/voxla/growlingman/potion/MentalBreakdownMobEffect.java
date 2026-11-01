package net.voxla.growlingman.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.voxla.growlingman.procedures.MentalBreakdownOnEffectActiveTickProcedure;

public class MentalBreakdownMobEffect extends MobEffect {
	public MentalBreakdownMobEffect() {
		super(MobEffectCategory.HARMFUL, -16777216);
	}

	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		MentalBreakdownOnEffectActiveTickProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
		return super.applyEffectTick(entity, amplifier);
	}

	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}
}
