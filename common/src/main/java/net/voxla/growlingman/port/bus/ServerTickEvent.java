package net.voxla.growlingman.port.bus;

import net.minecraft.server.MinecraftServer;

public abstract class ServerTickEvent extends Event {
	private final MinecraftServer server;

	protected ServerTickEvent(MinecraftServer server) {
		this.server = server;
	}

	public MinecraftServer getServer() {
		return this.server;
	}

	public static class Post extends ServerTickEvent {
		public Post(MinecraftServer server) {
			super(server);
		}
	}
}
