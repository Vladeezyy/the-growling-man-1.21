package net.voxla.growlingman.port.bus;

import net.minecraft.world.entity.player.Player;

public class PlayerWakeUpEvent extends PlayerEvent {
	public PlayerWakeUpEvent(Player player) {
		super(player);
	}
}
