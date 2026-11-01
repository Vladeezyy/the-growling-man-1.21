package net.voxla.growlingman.init;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.voxla.growlingman.client.renderer.GrowlingManRenderer;

@EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)
public class GrowlingManModEntityRenderers {
   @SubscribeEvent
   public static void registerEntityRenderers(RegisterRenderers event) {
      event.registerEntityRenderer((EntityType)GrowlingManModEntities.GROWLING_MAN.get(), GrowlingManRenderer::new);
   }
}
