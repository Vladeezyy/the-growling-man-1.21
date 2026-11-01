package net.voxla.growlingman.procedures;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent.ComputeFov;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.voxla.growlingman.init.GrowlingManModMobEffects;
import net.voxla.growlingman.network.GrowlingManModVariables;

@EventBusSubscriber(Dist.CLIENT)
public class HoldingBreathFovProcedure {
   public static ComputeFov provider = null;

   public static void setFOV(double fov) {
      provider.setFOV(fov);
   }

   @SubscribeEvent
   public static void computeFOV(ComputeFov event) {
      provider = event;
      ClientLevel level = Minecraft.getInstance().level;
      Entity entity = provider.getCamera().getEntity();
      if (level != null && entity != null) {
         Vec3 entPos = entity.getPosition((float)provider.getPartialTick());
         execute(provider, level, entity, provider.getFOV());
      }
   }

   public static boolean execute(LevelAccessor world, Entity entity, double fov) {
      return execute(null, world, entity, fov);
   }

   private static boolean execute(@Nullable Event event, LevelAccessor world, Entity entity, double fov) {
      if (entity == null) {
         return false;
      } else {
         double FovEffected = 0.0;
         if (world.isClientSide()
            && ((GrowlingManModVariables.PlayerVariables)entity.getCapability(GrowlingManModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                     .orElse(new GrowlingManModVariables.PlayerVariables()))
                  .HoldingBreathTimer
               > 0.0) {
            FovEffected = fov
               - ((GrowlingManModVariables.PlayerVariables)entity.getCapability(GrowlingManModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                        .orElse(new GrowlingManModVariables.PlayerVariables()))
                     .HoldingBreathTimer
                  / 30.0;
            setFOV(FovEffected);
         } else if (world.isClientSide() && entity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect((MobEffect)GrowlingManModMobEffects.MENTAL_BREAKDOWN.get())
            )
          {
            if ((
                  entity instanceof LivingEntity _livEnt && _livEnt.hasEffect((MobEffect)GrowlingManModMobEffects.MENTAL_BREAKDOWN.get())
                     ? _livEnt.getEffect((MobEffect)GrowlingManModMobEffects.MENTAL_BREAKDOWN.get()).getDuration()
                     : 0
               )
               >= 150) {
               FovEffected = fov + Mth.nextDouble(RandomSource.create(), -2.0, 2.0);
            } else if ((
                  entity instanceof LivingEntity _livEnt && _livEnt.hasEffect((MobEffect)GrowlingManModMobEffects.MENTAL_BREAKDOWN.get())
                     ? _livEnt.getEffect((MobEffect)GrowlingManModMobEffects.MENTAL_BREAKDOWN.get()).getDuration()
                     : 0
               )
               >= 100) {
               FovEffected = fov + Mth.nextDouble(RandomSource.create(), -1.5, 1.5);
            } else if ((
                  entity instanceof LivingEntity _livEnt && _livEnt.hasEffect((MobEffect)GrowlingManModMobEffects.MENTAL_BREAKDOWN.get())
                     ? _livEnt.getEffect((MobEffect)GrowlingManModMobEffects.MENTAL_BREAKDOWN.get()).getDuration()
                     : 0
               )
               >= 50) {
               FovEffected = fov + Mth.nextDouble(RandomSource.create(), -0.9, 0.9);
            } else {
               FovEffected = fov + Mth.nextDouble(RandomSource.create(), -0.3, 0.3);
            }

            setFOV(FovEffected);
         }

         return false;
      }
   }
}
