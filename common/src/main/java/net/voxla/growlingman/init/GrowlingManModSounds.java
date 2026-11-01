package net.voxla.growlingman.init;

import net.minecraft.core.registries.Registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.voxla.growlingman.port.platform.Registrar;
import net.minecraft.core.registries.BuiltInRegistries;
import net.voxla.growlingman.port.platform.RegistryEntry;

public class GrowlingManModSounds {
	public static final Registrar<SoundEvent> REGISTRY = Registrar.create(Registries.SOUND_EVENT, "growling_man");
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_PLAYER_MARKED = REGISTRY.register(
		"entity.player.marked", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.player.marked"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_GROWLING_MAN_STEP = REGISTRY.register(
		"entity.growling_man.step", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.growling_man.step"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_GROWLING_MAN_DEATH = REGISTRY.register(
		"entity.growling_man.death", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.growling_man.death"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_GROWLING_MAN_ERRATIC = REGISTRY.register(
		"entity.growling_man.erratic", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.growling_man.erratic"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_GROWLING_MAN_HURT = REGISTRY.register(
		"entity.growling_man.hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.growling_man.hurt"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_GROWLING_MAN_SPORADIC = REGISTRY.register(
		"entity.growling_man.sporadic", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.growling_man.sporadic"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_PLAYER_GM_HEARTBEAT_SLOW = REGISTRY.register(
		"entity.player.gm_heartbeat_slow", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.player.gm_heartbeat_slow"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_PLAYER_GM_HEARTBEAT_FAST = REGISTRY.register(
		"entity.player.gm_heartbeat_fast", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.player.gm_heartbeat_fast"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_PLAYER_GM_MENTAL_BREAKDOWN = REGISTRY.register(
		"entity.player.gm_mental_breakdown", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.player.gm_mental_breakdown"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_PLAYER_GM_CLEAR_MIND_FAIL = REGISTRY.register(
		"entity.player.gm_clear_mind_fail", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.player.gm_clear_mind_fail"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_GROWLING_MAN_AMBIENT = REGISTRY.register(
		"entity.growling_man.ambient", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.growling_man.ambient"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_GROWLING_MAN_GROWL_MAIN = REGISTRY.register(
		"entity.growling_man.growl_main", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.growling_man.growl_main"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_GROWLING_MAN_SPORADIC_LIMBS = REGISTRY.register(
		"entity.growling_man.sporadic_limbs", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.growling_man.sporadic_limbs"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_GROWLING_MAN_MANIFEST = REGISTRY.register(
		"entity.growling_man.manifest", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.growling_man.manifest"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_PLAYER_HEARTBEAT_MED = REGISTRY.register(
		"entity.player.heartbeat_med", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.player.heartbeat_med"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_PLAYER_KILLS_TOO_MUCH = REGISTRY.register(
		"entity.player.kills_too_much", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.player.kills_too_much"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_GROWLING_MAN_NEAR = REGISTRY.register(
		"entity.growling_man.near", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.growling_man.near"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_GROWLING_MAN_AMBIENT_DISTANT = REGISTRY.register(
		"entity.growling_man.ambient_distant", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.growling_man.ambient_distant"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_GROWLING_MAN_SPORADIC_LIMBS_DISTANT = REGISTRY.register(
		"entity.growling_man.sporadic_limbs_distant",
		() -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.growling_man.sporadic_limbs_distant"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_GROWLING_MAN_GROWL_MAIN_DISTANT = REGISTRY.register(
		"entity.growling_man.growl_main_distant", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.growling_man.growl_main_distant"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_GROWLING_MAN_ERRATIC_DISTANT = REGISTRY.register(
		"entity.growling_man.erratic_distant", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.growling_man.erratic_distant"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> ENTITY_GROWLING_MAN_SPORADIC_DISTANT = REGISTRY.register(
		"entity.growling_man.sporadic_distant", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "entity.growling_man.sporadic_distant"))
	);
	public static final RegistryEntry<SoundEvent, SoundEvent> AMBIENT_DISTANT = REGISTRY.register(
		"ambient.distant", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("growling_man", "ambient.distant"))
	);
}
