package net.voxla.growlingman.port;

import com.google.gson.JsonObject;
import com.google.gson.Gson;

import java.nio.file.Files;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;

/**
 * The mod's config/growling_manconfig.json, read once and kept: 1.20.1 opened and parsed the file in every procedure
 * that looks at a setting, which includes natural spawn checks (every spawn attempt), entity and player ticks and the
 * insanity HUD (every frame). The file is looked at again at most once per second and re-read when it changed, so
 * editing it still takes effect while the game runs. Callers must not modify the returned object.
 */
public final class GMConfig {
	private static final long RECHECK_NANOS = 1_000_000_000L;
	private static File file;
	private static JsonObject cached;
	private static long modified = -1, checkedAt;

	/** Like reading and parsing {@code config} each time, including the exception when the file is missing. */
	public static synchronized JsonObject read(File config) throws IOException {
		long now = System.nanoTime();
		if (cached != null && config.equals(file) && now - checkedAt < RECHECK_NANOS)
			return cached;
		checkedAt = now;
		long m = config.lastModified();
		if (m == 0L && !config.exists())
			throw new FileNotFoundException(config.getPath());
		if (cached == null || m != modified || !config.equals(file)) {
			cached = new Gson().fromJson(String.join("", Files.readAllLines(config.toPath())), JsonObject.class);
			modified = m;
			file = config;
		}
		return cached;
	}

	private GMConfig() {
	}
}
