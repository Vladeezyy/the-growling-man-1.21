package net.voxla.growlingman.port.bus;

import net.minecraft.world.level.Level;

public abstract class LevelTickEvent extends Event {
	private final Level level;

	protected LevelTickEvent(Level level) {
		this.level = level;
	}

	public Level getLevel() {
		return this.level;
	}

	/** After a server level ticks */
	public static class Post extends LevelTickEvent {
		public Post(Level level) {
			super(level);
		}
	}
}
