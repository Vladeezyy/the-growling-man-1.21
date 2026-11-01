package net.voxla.growlingman.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;

import net.voxla.growlingman.GrowlingManMod;
import net.voxla.growlingman.port.bus.BuildCreativeModeTabContentsEvent;
import net.voxla.growlingman.port.bus.EntityAttributeCreationEvent;
import net.voxla.growlingman.port.bus.EntityJoinLevelEvent;
import net.voxla.growlingman.port.bus.EntityLeaveLevelEvent;
import net.voxla.growlingman.port.bus.FMLCommonSetupEvent;
import net.voxla.growlingman.port.bus.LivingDeathEvent;
import net.voxla.growlingman.port.bus.LivingIncomingDamageEvent;
import net.voxla.growlingman.port.bus.GMBus;
import net.voxla.growlingman.port.bus.PlayerEvent;
import net.voxla.growlingman.port.bus.PlayerInteractEvent;
import net.voxla.growlingman.port.bus.PlayerWakeUpEvent;
import net.voxla.growlingman.port.bus.RegisterCommandsEvent;
import net.voxla.growlingman.port.bus.RegisterSpawnPlacementsEvent;
import net.voxla.growlingman.port.bus.ServerTickEvent;

/** Fabric entry point: common init, Fabric API events (and the mixins in .mixin) posted on {@link GMBus} */
public final class GrowlingManFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		GrowlingManMod.init();
		FabricPlatform.registerAll();
		GMBus.post(new EntityAttributeCreationEvent(FabricDefaultAttributeRegistry::register));
		GMBus.post(new RegisterSpawnPlacementsEvent(new RegisterSpawnPlacementsEvent.Sink() {
			@Override
			public <T extends Mob> void register(net.minecraft.world.entity.EntityType<T> type, net.minecraft.world.entity.SpawnPlacementType placement,
					net.minecraft.world.level.levelgen.Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> predicate, RegisterSpawnPlacementsEvent.Operation operation) {
				// the mod only replaces (its own types, which have no placement yet)
				SpawnPlacements.register(type, placement, heightmap, predicate);
			}
		}));
		FabricBiomeModifiers.register();
		ItemGroupEvents.MODIFY_ENTRIES_ALL.register((tab, entries) -> BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab)
				.ifPresent(key -> GMBus.post(new BuildCreativeModeTabContentsEvent(key, entries::accept))));
		GMBus.post(new FMLCommonSetupEvent());

		ServerLifecycleEvents.SERVER_STARTING.register(server -> FabricPlatform.server = server);
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> FabricPlatform.server = null);
		ServerTickEvents.END_SERVER_TICK.register(server -> GMBus.post(new ServerTickEvent.Post(server)));
		CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> GMBus.post(new RegisterCommandsEvent(dispatcher, selection, context)));
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> !GMBus.post(new LivingIncomingDamageEvent(entity, source, amount)));
		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> !GMBus.post(new LivingDeathEvent(entity, source)));
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> GMBus.post(new PlayerEvent.PlayerLoggedInEvent(handler.getPlayer())));
		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> GMBus.post(new PlayerEvent.Clone(newPlayer, oldPlayer, !alive)));
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> GMBus.post(new PlayerEvent.PlayerRespawnEvent(newPlayer, alive)));
		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> GMBus.post(new PlayerEvent.PlayerChangedDimensionEvent(player)));
		EntitySleepEvents.STOP_SLEEPING.register((entity, pos) -> {
			if (entity instanceof Player player)
				GMBus.post(new PlayerWakeUpEvent(player));
		});
		UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
			GMBus.post(new PlayerInteractEvent.RightClickBlock(player, hand, hit.getBlockPos(), hit.getDirection()));
			return InteractionResult.PASS;
		});
		ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> GMBus.post(new EntityJoinLevelEvent(entity, level)));
		ServerEntityEvents.ENTITY_UNLOAD.register((entity, level) -> GMBus.post(new EntityLeaveLevelEvent(entity, level)));
	}
}
