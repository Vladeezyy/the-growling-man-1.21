package net.voxla.growlingman.port.bus;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class PlayerInteractEvent extends PlayerEvent {
	private final InteractionHand hand;
	private final BlockPos pos;

	protected PlayerInteractEvent(Player player, InteractionHand hand, BlockPos pos) {
		super(player);
		this.hand = hand;
		this.pos = pos;
	}

	public InteractionHand getHand() {
		return this.hand;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public Level getLevel() {
		return this.getEntity().level();
	}

	/** A player right-clicks a block (client and server); listeners here only observe it */
	public static class RightClickBlock extends PlayerInteractEvent {
		private final Direction face;

		public RightClickBlock(Player player, InteractionHand hand, BlockPos pos, Direction face) {
			super(player, hand, pos);
			this.face = face;
		}

		public Direction getFace() {
			return this.face;
		}
	}
}
