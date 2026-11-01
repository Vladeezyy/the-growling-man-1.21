package net.voxla.growlingman.init;

import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.voxla.growlingman.entity.GrowlingManEntity;

@EventBusSubscriber
public class EntityAnimationFactory {
   @SubscribeEvent
   public static void onEntityTick(LivingTickEvent event) {
      if (event != null && event.getEntity() != null && event.getEntity() instanceof GrowlingManEntity syncable) {
         String animation = syncable.getSyncedAnimation();
         if (!animation.equals("undefined")) {
            syncable.setAnimation("undefined");
            syncable.animationprocedure = animation;
         }
      }
   }
}
