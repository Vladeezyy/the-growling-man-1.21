package net.voxla.growlingman.port.bus;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

public abstract class ItemEntityPickupEvent extends Event {
	private final Player player;
	private final ItemEntity itemEntity;

	protected ItemEntityPickupEvent(Player player, ItemEntity itemEntity) {
		this.player = player;
		this.itemEntity = itemEntity;
	}

	public Player getPlayer() {
		return this.player;
	}

	public ItemEntity getItemEntity() {
		return this.itemEntity;
	}

	/** A player touches an item on the ground and is about to pick it up (server) */
	public static class Pre extends ItemEntityPickupEvent {
		public Pre(Player player, ItemEntity itemEntity) {
			super(player, itemEntity);
		}
	}
}
