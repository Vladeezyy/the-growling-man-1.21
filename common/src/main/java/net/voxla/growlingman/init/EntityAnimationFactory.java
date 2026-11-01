package net.voxla.growlingman.init;

import net.voxla.growlingman.port.bus.EntityTickEvent;
import net.voxla.growlingman.port.bus.SubscribeEvent;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.voxla.growlingman.entity.GrowlingManEntity;

@EventBusSubscriber
public class EntityAnimationFactory {
	@SubscribeEvent
	public static void onEntityTick(EntityTickEvent.Pre event) {
		if (event != null && event.getEntity() != null && event.getEntity() instanceof GrowlingManEntity syncable) {
			String animation = syncable.getSyncedAnimation();
			if (!animation.equals("undefined")) {
				syncable.setAnimation("undefined");
				syncable.animationprocedure = animation;
			}
		}
	}
}
