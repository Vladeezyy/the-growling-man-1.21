package net.voxla.growlingman.init;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.voxla.growlingman.potion.MentalBreakdownMobEffect;

public class GrowlingManModMobEffects {
   public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "growling_man");
   public static final RegistryObject<MobEffect> MENTAL_BREAKDOWN = REGISTRY.register("mental_breakdown", () -> new MentalBreakdownMobEffect());
}
