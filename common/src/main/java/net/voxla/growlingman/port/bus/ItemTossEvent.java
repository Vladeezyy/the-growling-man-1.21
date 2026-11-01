package net.voxla.growlingman.port.bus;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

/** A player dropped an item (server; the integrated server in single player) */
public class ItemTossEvent extends Event {
	private final ItemEntity entity;
	private final Player player;

	public ItemTossEvent(ItemEntity entity, Player player) {
		this.entity = entity;
		this.player = player;
	}

	public ItemEntity getEntity() {
		return this.entity;
	}

	public Player getPlayer() {
		return this.player;
	}
}
