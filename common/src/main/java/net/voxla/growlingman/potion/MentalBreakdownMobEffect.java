package net.voxla.growlingman.potion;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.voxla.growlingman.procedures.MentalBreakdownOnEffectActiveTickProcedure;

public class MentalBreakdownMobEffect extends MobEffect {
	public MentalBreakdownMobEffect() {
		super(MobEffectCategory.HARMFUL, -16777216);
	}

	public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
		MentalBreakdownOnEffectActiveTickProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
		return super.applyEffectTick(level, entity, amplifier);
	}

	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}
}
