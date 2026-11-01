package net.voxla.growlingman.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import net.voxla.growlingman.GrowlingManMod;
import net.voxla.growlingman.init.GrowlingManModMobEffects;
import net.voxla.growlingman.port.EffectVisibility;
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
		modBus.addListener((EntityRenderersEvent.RegisterLayerDefinitions event) -> GMBus.post(new net.voxla.growlingman.port.bus.EntityRenderersEvent.RegisterLayerDefinitions(event::registerLayerDefinition)));
		modBus.addListener((RegisterParticleProvidersEvent event) -> GMBus.post(new net.voxla.growlingman.port.bus.RegisterParticleProvidersEvent(
				new net.voxla.growlingman.port.bus.RegisterParticleProvidersEvent.Sink() {
					@Override
					public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleEngine.SpriteParticleRegistration<T> registration) {
						event.registerSpriteSet(type, registration);
					}
				})));
		modBus.addListener((RegisterMenuScreensEvent event) -> GMBus.post(new net.voxla.growlingman.port.bus.RegisterMenuScreensEvent(event::register)));
		modBus.addListener(GrowlingManNeoForgeClient::registerClientExtensions);

		IEventBus bus = NeoForge.EVENT_BUS;
		bus.addListener((ClientTickEvent.Post event) -> GMBus.post(new net.voxla.growlingman.port.bus.ClientTickEvent.Post()));
		bus.addListener((RenderGuiEvent.Pre event) -> GMBus.post(new net.voxla.growlingman.port.bus.RenderGuiEvent.Pre(event.getGuiGraphics())));
		bus.addListener((ScreenEvent.Render.Post event) -> GMBus.post(new net.voxla.growlingman.port.bus.ScreenEvent.Render.Post(event.getScreen(), event.getGuiGraphics())));
		bus.addListener((ViewportEvent.ComputeFogColor event) -> {
			var ours = new net.voxla.growlingman.port.bus.ViewportEvent.ComputeFogColor(event.getCamera(), event.getPartialTick(), event.getRed(), event.getGreen(), event.getBlue());
			GMBus.post(ours);
			event.setRed(ours.getRed());
			event.setGreen(ours.getGreen());
			event.setBlue(ours.getBlue());
		});
		bus.addListener((ViewportEvent.RenderFog event) -> {
			var ours = new net.voxla.growlingman.port.bus.ViewportEvent.RenderFog(event.getMode(), event.getCamera(), event.getPartialTick(), event.getNearPlaneDistance(),
					event.getFarPlaneDistance(), event.getFogShape());
			if (GMBus.post(ours)) {
				event.setNearPlaneDistance(ours.getNearPlaneDistance());
				event.setFarPlaneDistance(ours.getFarPlaneDistance());
				event.setFogShape(ours.getFogShape());
				event.setCanceled(true);
			}
		});
	}

	/** The effects' inventory / HUD visibility (port.EffectVisibility) */
	private static void registerClientExtensions(RegisterClientExtensionsEvent event) {
		for (var entry : GrowlingManModMobEffects.REGISTRY.getEntries()) {
			MobEffect effect = entry.get();
			if (effect instanceof EffectVisibility visibility) {
				event.registerMobEffect(new IClientMobEffectExtensions() {
					@Override
					public boolean isVisibleInInventory(MobEffectInstance instance) {
						return visibility.isVisibleInInventory(instance);
					}

					@Override
					public boolean isVisibleInGui(MobEffectInstance instance) {
						return visibility.isVisibleInGui(instance);
					}
				}, effect);
			}
		}
	}
}
