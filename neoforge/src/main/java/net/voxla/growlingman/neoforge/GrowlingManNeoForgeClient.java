package net.voxla.growlingman.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import net.voxla.growlingman.GrowlingManMod;
import net.voxla.growlingman.port.PortUtil;
import net.voxla.growlingman.port.bus.ClientEventIndex;
import net.voxla.growlingman.port.bus.GMBus;

/** NeoForge client entry point: the client events posted on {@link GMBus} */
@Mod(value = GrowlingManMod.MODID, dist = Dist.CLIENT)
public final class GrowlingManNeoForgeClient {
	public GrowlingManNeoForgeClient(IEventBus modBus) {
		ClientEventIndex.register();
		PortUtil.clientRegistries = () -> Minecraft.getInstance().level == null ? null : Minecraft.getInstance().level.registryAccess();
		PortUtil.clientPlayer = () -> Minecraft.getInstance().player;

		modBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> GMBus.post(new net.voxla.growlingman.port.bus.EntityRenderersEvent.RegisterRenderers(
				new net.voxla.growlingman.port.bus.EntityRenderersEvent.RegisterRenderers.Sink() {
					@Override
					public <T extends Entity> void register(EntityType<? extends T> type, EntityRendererProvider<T> provider) {
						event.registerEntityRenderer(type, provider);
					}
				})));
		modBus.addListener((RegisterKeyMappingsEvent event) -> GMBus.post(new net.voxla.growlingman.port.bus.RegisterKeyMappingsEvent(event::register)));

		IEventBus bus = NeoForge.EVENT_BUS;
		bus.addListener((ClientTickEvent.Post event) -> GMBus.post(new net.voxla.growlingman.port.bus.ClientTickEvent.Post()));
		bus.addListener((RenderGuiEvent.Pre event) -> GMBus.post(new net.voxla.growlingman.port.bus.RenderGuiEvent.Pre(event.getGuiGraphics())));
		bus.addListener((ScreenEvent.Render.Post event) -> GMBus.post(new net.voxla.growlingman.port.bus.ScreenEvent.Render.Post(event.getScreen(), event.getGuiGraphics())));
		bus.addListener((ViewportEvent.ComputeFov event) -> {
			var ours = new net.voxla.growlingman.port.bus.ViewportEvent.ComputeFov(event.getCamera(), event.getPartialTick(), event.getFOV(), event.usedConfiguredFov());
			GMBus.post(ours);
			if (ours.getFOV() != event.getFOV())
				event.setFOV(ours.getFOV());
		});
	}
}
