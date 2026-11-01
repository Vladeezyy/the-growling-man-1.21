package net.voxla.growlingman.init;

import net.minecraft.core.registries.Registries;

import net.minecraft.world.effect.MobEffect;
import net.voxla.growlingman.port.platform.Registrar;
import net.minecraft.core.registries.BuiltInRegistries;
import net.voxla.growlingman.port.platform.RegistryEntry;
import net.voxla.growlingman.potion.MentalBreakdownMobEffect;

public class GrowlingManModMobEffects {
	public static final Registrar<MobEffect> REGISTRY = Registrar.create(Registries.MOB_EFFECT, "growling_man");
	public static final RegistryEntry<MobEffect, MobEffect> MENTAL_BREAKDOWN = REGISTRY.register("mental_breakdown", () -> new MentalBreakdownMobEffect());
}
