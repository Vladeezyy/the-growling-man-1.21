package net.voxla.growlingman.port.bus;

import net.voxla.growlingman.port.platform.Platform;

import java.nio.file.Path;
import java.util.function.Supplier;

public enum FMLPaths {
	GAMEDIR(() -> Platform.INSTANCE.gameDir()), CONFIGDIR(() -> Platform.INSTANCE.configDir());

	private final Supplier<Path> path;

	FMLPaths(Supplier<Path> path) {
		this.path = path;
	}

	public Path get() {
		return this.path.get();
	}
}
