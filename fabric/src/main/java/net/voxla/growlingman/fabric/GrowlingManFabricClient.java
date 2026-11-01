package net.voxla.growlingman.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import net.voxla.growlingman.port.PortUtil;
import net.voxla.growlingman.port.TomlConfigFile;
import net.voxla.growlingman.port.bus.ClientEventIndex;
import net.voxla.growlingman.port.bus.ClientTickEvent;
import net.voxla.growlingman.port.bus.EntityRenderersEvent;
import net.voxla.growlingman.port.bus.GMBus;
import net.voxla.growlingman.port.bus.RegisterKeyMappingsEvent;
import net.voxla.growlingman.port.bus.ScreenEvent;

/** Fabric client entry point: renderers, keys, packets and the client events posted on {@link GMBus} */
public final class GrowlingManFabricClient implements ClientModInitializer {
	private static int configCheck;

	@Override
	public void onInitializeClient() {
		ClientEventIndex.register();
		PortUtil.clientRegistries = () -> Minecraft.getInstance().level == null ? null : Minecraft.getInstance().level.registryAccess();
		PortUtil.clientPlayer = () -> Minecraft.getInstance().player;
		FabricPlatform.CLIENTBOUND.forEach(GrowlingManFabricClient::receive);

		GMBus.post(new EntityRenderersEvent.RegisterRenderers(new EntityRenderersEvent.RegisterRenderers.Sink() {
			@Override
			public <T extends Entity> void register(EntityType<? extends T> type, EntityRendererProvider<T> provider) {
				EntityRendererRegistry.register(type, provider);
			}
		}));
		GMBus.post(new RegisterKeyMappingsEvent(KeyBindingHelper::registerKeyBinding));

		ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
			GMBus.post(new ClientTickEvent.Post());
			// a client of a remote server reads its own copy of the config
			if (minecraft.getSingleplayerServer() == null && ++configCheck % 20 == 0)
				TomlConfigFile.reloadIfChanged();
		});
		ScreenEvents.AFTER_INIT.register((minecraft, screen, width, height) -> ScreenEvents.afterRender(screen)
				.register((s, guiGraphics, mouseX, mouseY, partialTick) -> GMBus.post(new ScreenEvent.Render.Post(s, guiGraphics))));
	}

	private static <T extends CustomPacketPayload> void receive(FabricPlatform.Clientbound<T> packet) {
		ClientPlayNetworking.registerGlobalReceiver(packet.type(), (payload, context) -> packet.handler().accept(payload));
	}
}
