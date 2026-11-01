package net.voxla.growlingman.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.voxla.growlingman.GrowlingManMod;
import net.voxla.growlingman.init.GrowlingManModBlockEntities;
import net.voxla.growlingman.port.GMEntities;
import net.voxla.growlingman.port.bus.GMBus;

/** NeoForge entry point: common init, then NeoForge's events posted on {@link GMBus} */
@Mod(GrowlingManMod.MODID)
public final class GrowlingManNeoForge {
	public GrowlingManNeoForge(IEventBus modBus) {
		GrowlingManMod.init();
		NeoForgePlatform.registerAll(modBus);

		modBus.addListener((FMLCommonSetupEvent event) -> GMBus.post(new net.voxla.growlingman.port.bus.FMLCommonSetupEvent()));
		modBus.addListener((EntityAttributeCreationEvent event) -> GMBus.post(new net.voxla.growlingman.port.bus.EntityAttributeCreationEvent(event::put)));
		modBus.addListener((RegisterSpawnPlacementsEvent event) -> GMBus.post(new net.voxla.growlingman.port.bus.RegisterSpawnPlacementsEvent(
				new net.voxla.growlingman.port.bus.RegisterSpawnPlacementsEvent.Sink() {
					@Override
					public <T extends net.minecraft.world.entity.Mob> void register(net.minecraft.world.entity.EntityType<T> type, net.minecraft.world.entity.SpawnPlacementType placement,
							net.minecraft.world.level.levelgen.Heightmap.Types heightmap, net.minecraft.world.entity.SpawnPlacements.SpawnPredicate<T> predicate,
							net.voxla.growlingman.port.bus.RegisterSpawnPlacementsEvent.Operation operation) {
						event.register(type, placement, heightmap, predicate, RegisterSpawnPlacementsEvent.Operation.valueOf(operation.name()));
					}
				})));
		modBus.addListener((BuildCreativeModeTabContentsEvent event) -> GMBus.post(new net.voxla.growlingman.port.bus.BuildCreativeModeTabContentsEvent(event.getTabKey(), event::accept)));
		modBus.addListener(GrowlingManNeoForge::registerCapabilities);

		IEventBus bus = NeoForge.EVENT_BUS;
		bus.addListener((ServerTickEvent.Post event) -> GMBus.post(new net.voxla.growlingman.port.bus.ServerTickEvent.Post(event.getServer())));
		bus.addListener((RegisterCommandsEvent event) -> GMBus.post(new net.voxla.growlingman.port.bus.RegisterCommandsEvent(event.getDispatcher(), event.getCommandSelection(), event.getBuildContext())));
		bus.addListener((LivingIncomingDamageEvent event) -> {
			var ours = new net.voxla.growlingman.port.bus.LivingIncomingDamageEvent(event.getEntity(), event.getSource(), event.getAmount());
			if (GMBus.post(ours))
				event.setCanceled(true);
			else if (ours.getAmount() != event.getAmount())
				event.setAmount(ours.getAmount());
		});
		bus.addListener((LivingDeathEvent event) -> {
			if (GMBus.post(new net.voxla.growlingman.port.bus.LivingDeathEvent(event.getEntity(), event.getSource())))
				event.setCanceled(true);
		});
		bus.addListener((PlayerTickEvent.Post event) -> GMBus.post(new net.voxla.growlingman.port.bus.PlayerTickEvent.Post(event.getEntity())));
		bus.addListener((PlayerEvent.PlayerLoggedInEvent event) -> GMBus.post(new net.voxla.growlingman.port.bus.PlayerEvent.PlayerLoggedInEvent(event.getEntity())));
		bus.addListener((PlayerEvent.PlayerRespawnEvent event) -> GMBus.post(new net.voxla.growlingman.port.bus.PlayerEvent.PlayerRespawnEvent(event.getEntity(), event.isEndConquered())));
		bus.addListener((PlayerEvent.PlayerChangedDimensionEvent event) -> GMBus.post(new net.voxla.growlingman.port.bus.PlayerEvent.PlayerChangedDimensionEvent(event.getEntity())));
		bus.addListener((PlayerEvent.Clone event) -> GMBus.post(new net.voxla.growlingman.port.bus.PlayerEvent.Clone(event.getEntity(), event.getOriginal(), event.isWasDeath())));
		bus.addListener((PlayerEvent.ItemCraftedEvent event) -> GMBus.post(new net.voxla.growlingman.port.bus.PlayerEvent.ItemCraftedEvent(event.getEntity(), event.getCrafting())));
		bus.addListener((PlayerEvent.ItemSmeltedEvent event) -> GMBus.post(new net.voxla.growlingman.port.bus.PlayerEvent.ItemSmeltedEvent(event.getEntity(), event.getSmelting())));
		bus.addListener((PlayerWakeUpEvent event) -> GMBus.post(new net.voxla.growlingman.port.bus.PlayerWakeUpEvent(event.getEntity())));
		bus.addListener((ItemFishedEvent event) -> GMBus.post(new net.voxla.growlingman.port.bus.ItemFishedEvent(event.getEntity(), event.getDrops())));
		bus.addListener((PlayerInteractEvent.RightClickBlock event) -> GMBus.post(
				new net.voxla.growlingman.port.bus.PlayerInteractEvent.RightClickBlock(event.getEntity(), event.getHand(), event.getPos(), event.getFace())));
		bus.addListener((ItemEntityPickupEvent.Pre event) -> GMBus.post(new net.voxla.growlingman.port.bus.ItemEntityPickupEvent.Pre(event.getPlayer(), event.getItemEntity())));
		bus.addListener((LivingEntityUseItemEvent.Finish event) -> GMBus.post(new net.voxla.growlingman.port.bus.LivingEntityUseItemEvent.Finish(event.getEntity(), event.getItem())));
		// the only listener (EntityAnimationFactory) handles this mod's entities: skip the rest of the world's
		bus.addListener((EntityTickEvent.Pre event) -> {
			if (GMEntities.ours(event.getEntity()))
				GMBus.post(new net.voxla.growlingman.port.bus.EntityTickEvent.Pre(event.getEntity()));
		});
		bus.addListener((EntityJoinLevelEvent event) -> GMBus.post(new net.voxla.growlingman.port.bus.EntityJoinLevelEvent(event.getEntity(), event.getLevel())));
		bus.addListener((EntityLeaveLevelEvent event) -> GMBus.post(new net.voxla.growlingman.port.bus.EntityLeaveLevelEvent(event.getEntity(), event.getLevel())));
	}

	/** The shattered glass blocks expose their slots to pipes and hoppers (as in 3.3.7) */
	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
		for (var entry : GrowlingManModBlockEntities.REGISTRY.getEntries()) {
			@SuppressWarnings("unchecked")
			BlockEntityType<net.minecraft.world.level.block.entity.BlockEntity> type = (BlockEntityType<net.minecraft.world.level.block.entity.BlockEntity>) entry.get();
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type, (blockEntity, side) -> blockEntity instanceof WorldlyContainer container ? new SidedInvWrapper(container, side) : null);
		}
	}
}
