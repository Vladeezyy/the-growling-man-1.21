package net.voxla.growlingman.port.bus;

import net.minecraft.world.entity.player.Player;

public abstract class PlayerTickEvent extends PlayerEvent {
	protected PlayerTickEvent(Player player) {
		super(player);
	}

	/** After the player ticked, on the server and on the client */
	public static class Post extends PlayerTickEvent {
		public Post(Player player) {
			super(player);
		}
	}
}
