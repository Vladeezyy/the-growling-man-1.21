package net.voxla.growlingman.port.bus;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** A player reeled in a catch (server) */
public class ItemFishedEvent extends PlayerEvent {
	private final List<ItemStack> drops;

	public ItemFishedEvent(Player player, List<ItemStack> drops) {
		super(player);
		this.drops = drops;
	}

	public List<ItemStack> getDrops() {
		return this.drops;
	}
}
