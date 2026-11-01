package net.voxla.growlingman.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;

import net.voxla.growlingman.GrowlingManMod;
import net.voxla.growlingman.configuration.GrowlingManConfigConfiguration;
import net.voxla.growlingman.port.TomlConfigFile;
import net.voxla.growlingman.port.bus.EntityAttributeCreationEvent;
import net.voxla.growlingman.port.bus.GMBus;
import net.voxla.growlingman.port.bus.LevelTickEvent;
import net.voxla.growlingman.port.bus.LivingDeathEvent;
import net.voxla.growlingman.port.bus.LivingIncomingDamageEvent;
import net.voxla.growlingman.port.bus.PlayerEvent;
import net.voxla.growlingman.port.bus.RegisterCommandsEvent;
import net.voxla.growlingman.port.bus.ServerTickEvent;

/** Fabric entry point: common init, Fabric API events (and the mixins in .mixin) posted on {@link GMBus} */
public final class GrowlingManFabric implements ModInitializer {
	private static int configCheck;

	@Override
	public void onInitialize() {
		GrowlingManMod.init();
		TomlConfigFile.load(FabricLoader.getInstance().getConfigDir().resolve(GrowlingManConfigConfiguration.FILE_NAME));
		FabricPlatform.registerAll();
		GMBus.post(new EntityAttributeCreationEvent(FabricDefaultAttributeRegistry::register));

		ServerLifecycleEvents.SERVER_STARTING.register(server -> FabricPlatform.server = server);
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> FabricPlatform.server = null);
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			GMBus.post(new ServerTickEvent.Post(server));
			// NeoForge reloads a changed config file too
			if (++configCheck % 20 == 0)
				TomlConfigFile.reloadIfChanged();
		});
		ServerTickEvents.END_WORLD_TICK.register(level -> GMBus.post(new LevelTickEvent.Post(level)));
		CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> GMBus.post(new RegisterCommandsEvent(dispatcher, selection, context)));
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> !GMBus.post(new LivingIncomingDamageEvent(entity, source, amount)));
		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> !GMBus.post(new LivingDeathEvent(entity, source)));
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> GMBus.post(new PlayerEvent.PlayerLoggedInEvent(handler.getPlayer())));
		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> GMBus.post(new PlayerEvent.Clone(newPlayer, oldPlayer, !alive)));
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> GMBus.post(new PlayerEvent.PlayerRespawnEvent(newPlayer, alive)));
		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> GMBus.post(new PlayerEvent.PlayerChangedDimensionEvent(player)));
	}
}
