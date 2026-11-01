package net.voxla.growlingman.port.bus;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public abstract class LivingEntityUseItemEvent extends LivingEvent {
	private final ItemStack item;

	protected LivingEntityUseItemEvent(LivingEntity entity, ItemStack item) {
		super(entity);
		this.item = item;
	}

	public ItemStack getItem() {
		return this.item;
	}

	/** The entity finished using the item (eating, drinking); {@link #getItem()} is a copy of the stack before */
	public static class Finish extends LivingEntityUseItemEvent {
		public Finish(LivingEntity entity, ItemStack item) {
			super(entity, item);
		}
	}
}
