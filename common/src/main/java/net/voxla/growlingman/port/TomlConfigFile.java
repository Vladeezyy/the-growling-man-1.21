package net.voxla.growlingman.port;

import net.voxla.growlingman.GrowlingManMod;
import net.voxla.growlingman.configuration.GrowlingManConfigConfiguration;
import net.voxla.growlingman.configuration.GrowlingManConfigConfiguration.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * growling_man_config.toml on Fabric, in the format NeoForge (and Forge 1.20.1) writes: the values under
 * ["Growling Man Config"], comments as # lines. A missing file or missing keys are written with the defaults; a changed
 * file is read again ({@link #reloadIfChanged}, checked once per second).
 */
public final class TomlConfigFile {
	private static Path path;
	private static long modified = Long.MIN_VALUE;

	private TomlConfigFile() {
	}

	public static void load(Path file) {
		path = file;
		read();
	}

	public static void reloadIfChanged() {
		if (path == null)
			return;
		try {
			if (Files.exists(path) && Files.getLastModifiedTime(path).toMillis() != modified)
				read();
		} catch (IOException ignored) {
		}
	}

	private static void read() {
		Map<String, String> found = new HashMap<>();
		try {
			if (Files.exists(path)) {
				String section = "";
				for (String raw : Files.readAllLines(path)) {
					String line = raw.strip();
					if (line.isEmpty() || line.startsWith("#"))
						continue;
					if (line.startsWith("[")) {
						section = unquote(line.substring(1, line.lastIndexOf(']')).strip());
						continue;
					}
					int eq = line.indexOf('=');
					if (eq > 0 && section.equals(GrowlingManConfigConfiguration.CATEGORY))
						found.put(unquote(line.substring(0, eq).strip()), line.substring(eq + 1).strip());
				}
			}
		} catch (IOException e) {
			GrowlingManMod.LOGGER.warn("Could not read {}: {}", path, e.toString());
		}
		boolean complete = true;
		for (Value<?> value : GrowlingManConfigConfiguration.VALUES) {
			String text = found.get(value.key);
			Object parsed = text == null ? null : parse(text, value.defaultValue);
			if (parsed == null) {
				complete = false;
				parsed = value.defaultValue;
			}
			value.set(parsed);
		}
		if (!complete)
			write();
		try {
			modified = Files.getLastModifiedTime(path).toMillis();
		} catch (IOException e) {
			modified = Long.MIN_VALUE;
		}
	}

	private static Object parse(String text, Object defaultValue) {
		int comment = text.indexOf('#');
		if (comment >= 0)
			text = text.substring(0, comment).strip();
		try {
			if (defaultValue instanceof Boolean)
				return text.equals("true") ? Boolean.TRUE : text.equals("false") ? Boolean.FALSE : null;
			if (defaultValue instanceof Double)
				return Double.parseDouble(text);
		} catch (NumberFormatException ignored) {
		}
		return null;
	}

	private static void write() {
		StringBuilder out = new StringBuilder();
		out.append("\n[\"").append(GrowlingManConfigConfiguration.CATEGORY).append("\"]\n");
		for (Value<?> value : GrowlingManConfigConfiguration.VALUES) {
			if (value.comment != null)
				out.append("\t#").append(value.comment).append('\n');
			out.append("\t\"").append(value.key.replace("\"", "\\\"")).append("\" = ").append(value.get()).append('\n');
		}
		try {
			Files.createDirectories(path.getParent());
			Files.writeString(path, out.toString());
		} catch (IOException e) {
			GrowlingManMod.LOGGER.warn("Could not write {}: {}", path, e.toString());
		}
	}

	private static String unquote(String s) {
		if (s.length() >= 2 && (s.startsWith("\"") && s.endsWith("\"") || s.startsWith("'") && s.endsWith("'")))
			return s.substring(1, s.length() - 1).replace("\\\"", "\"");
		return s;
	}
}
