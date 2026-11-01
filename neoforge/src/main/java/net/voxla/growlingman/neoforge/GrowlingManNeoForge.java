package net.voxla.growlingman.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import net.voxla.growlingman.GrowlingManMod;
import net.voxla.growlingman.configuration.GrowlingManConfigConfiguration;
import net.voxla.growlingman.port.bus.GMBus;

import java.util.ArrayList;
import java.util.List;

/** NeoForge entry point: common init, then the NeoForge events the mod listens to posted on {@link GMBus} */
@Mod(GrowlingManMod.MODID)
public final class GrowlingManNeoForge {
	public GrowlingManNeoForge(IEventBus modBus, ModContainer container) {
		GrowlingManMod.init();
		NeoForgePlatform.registerAll(modBus);
		registerConfig(modBus, container);

		modBus.addListener((EntityAttributeCreationEvent event) -> GMBus.post(new net.voxla.growlingman.port.bus.EntityAttributeCreationEvent(event::put)));

		IEventBus bus = NeoForge.EVENT_BUS;
		bus.addListener((ServerTickEvent.Post event) -> GMBus.post(new net.voxla.growlingman.port.bus.ServerTickEvent.Post(event.getServer())));
		bus.addListener((LevelTickEvent.Post event) -> {
			if (!event.getLevel().isClientSide())
				GMBus.post(new net.voxla.growlingman.port.bus.LevelTickEvent.Post(event.getLevel()));
		});
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
	}

	/** growling_man_config.toml as in 1.0.1 (a COMMON config, so the Configured mod edits it); values copied on load / reload */
	private static void registerConfig(IEventBus modBus, ModContainer container) {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		List<Runnable> copy = new ArrayList<>();
		builder.push(GrowlingManConfigConfiguration.CATEGORY);
		for (GrowlingManConfigConfiguration.Value<?> value : GrowlingManConfigConfiguration.VALUES) {
			if (value.comment != null)
				builder.comment(value.comment);
			ModConfigSpec.ConfigValue<Object> configValue = builder.define(value.key, (Object) value.defaultValue);
			copy.add(() -> value.set(configValue.get()));
		}
		builder.pop();
		ModConfigSpec spec = builder.build();
		container.registerConfig(ModConfig.Type.COMMON, spec, GrowlingManConfigConfiguration.FILE_NAME);
		modBus.addListener((ModConfigEvent event) -> {
			if (event.getConfig().getSpec() == spec && !(event instanceof ModConfigEvent.Unloading))
				copy.forEach(Runnable::run);
		});
	}
}
