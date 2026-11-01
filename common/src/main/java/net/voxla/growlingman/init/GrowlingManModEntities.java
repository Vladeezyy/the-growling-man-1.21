package net.voxla.growlingman.init;

import net.minecraft.core.registries.Registries;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.voxla.growlingman.port.bus.EntityAttributeCreationEvent;
import net.voxla.growlingman.port.bus.SubscribeEvent;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.voxla.growlingman.port.platform.Registrar;
import net.minecraft.core.registries.BuiltInRegistries;
import net.voxla.growlingman.port.platform.RegistryEntry;
import net.voxla.growlingman.entity.GrowlingManEntity;

@EventBusSubscriber
public class GrowlingManModEntities {
	public static final Registrar<EntityType<?>> REGISTRY = Registrar.create(Registries.ENTITY_TYPE, "growling_man");
	public static final RegistryEntry<EntityType<?>, EntityType<GrowlingManEntity>> GROWLING_MAN = register(
		"growling_man",
		Builder.of(GrowlingManEntity::new, MobCategory.MONSTER)
			
			.clientTrackingRange(256)
			.updateInterval(3)
			
			.sized(0.7F, 2.8F)
	);

	private static <T extends Entity> RegistryEntry<EntityType<?>, EntityType<T>> register(String registryname, Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> entityTypeBuilder.build(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, net.minecraft.resources.Identifier.fromNamespaceAndPath("growling_man", registryname))));
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put((EntityType)GROWLING_MAN.get(), GrowlingManEntity.createAttributes().build());
	}
}
