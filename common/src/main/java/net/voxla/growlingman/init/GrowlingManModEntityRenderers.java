package net.voxla.growlingman.init;

import net.minecraft.world.entity.EntityType;
import net.voxla.growlingman.port.bus.Dist;
import net.voxla.growlingman.port.bus.EntityRenderersEvent;
import net.voxla.growlingman.port.bus.SubscribeEvent;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.voxla.growlingman.client.renderer.GrowlingManRenderer;

@EventBusSubscriber(value = Dist.CLIENT)
public class GrowlingManModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer((EntityType)GrowlingManModEntities.GROWLING_MAN.get(), GrowlingManRenderer::new);
	}
}
