package net.voxla.growlingman.port.bus;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.BiConsumer;

public class EntityAttributeCreationEvent extends Event {
	private final BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier> sink;

	public EntityAttributeCreationEvent(BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier> sink) {
		this.sink = sink;
	}

	public void put(EntityType<? extends LivingEntity> type, AttributeSupplier attributes) {
		this.sink.accept(type, attributes);
	}
}
