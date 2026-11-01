package net.voxla.growlingman.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * The settings of growling_man_config.toml (the same keys, comments and defaults as 1.0.1). NeoForge backs them with a
 * ModConfigSpec, Fabric reads the same file (port/TomlConfigFile); both copy the loaded values into {@link Value}.
 */
public class GrowlingManConfigConfiguration {
	public static final String FILE_NAME = "growling_man_config.toml";
	public static final String CATEGORY = "Growling Man Config";
	public static final List<Value<?>> VALUES = new ArrayList<>();
	public static final Value<Double> GM_SPAWNTIME_MIN = define("GM Spawntime Min",
		"a random number is generated between min and max for the spawn time, the spawn numbers are in seconds so set how many seconds you want it to take for him to spawn on min and max, WARNING do not make max time lower than min time otherwise it will cause bugs or crash the game", 60.0);
	public static final Value<Double> GM_SPAWNTIME_MAX = define("GM Spawntime Max",
		"a random number is generated between min and max for the spawn time, the spawn numbers are in seconds so set how many seconds you want it to take for him to spawn on min and max, WARNING do not make max time lower than min time otherwise it will cause bugs or crash the game", 240.0);
	public static final Value<Double> GM_SPAWN_CHANCE = define("GM Spawn Chance",
		"if you want a higher chance of him spawning set the spawn chance to a lower number, if you want a lower chance for him to spawn set the spawn chance to a higher number, WARNING do not set this number below 1 otherwise it may bug or crash the game", 1.0);
	public static final Value<Boolean> REGENERATE_BLOCKS = define("Regenerate Blocks If Broken By The Entity",
		null, false);
	public static final Value<Double> GM_CHASETIME_MIN = define("GM Chase Time Min",
		"a random number is generated between min and max for the chase time, the chase numbers are in minutes so set how many minutes you want it to take for him to disappear on min and max, WARNING do not make max time lower than min time otherwise it will cause bugs or crash the game", 1.0);
	public static final Value<Double> GM_CHASETIME_MAX = define("GM Chase Time Max",
		"a random number is generated between min and max for the chase time, the chase numbers are in minutes so set how many minutes you want it to take for him to disappear on min and max, WARNING do not make max time lower than min time otherwise it will cause bugs or crash the game", 2.0);
	public static final Value<Boolean> DONT_SPAWN_GM_ON_FIRST_DAY = define("Don't Spawn GM On First Day",
		null, false);
	public static final Value<Double> SPAWN_DISTANCE = define("GM Max Spawn Distance",
		"a random number is generated between 20 and the set config number for the spawn distance, spawn distance is in blocks so 100 would be 100 blocks away etc, WARNING do not set spawn distance below 20 otherwise it will cause bugs or crash the game", 70.0);
	public static final Value<Double> ANIMALS_KILLED_QUICKLY_TO_GET_MARKED = define("Amount Of Quick Animal Kills Needed To Get Marked",
		"max for animal kills is 20 and min is 3, if you set a number lower or higher then it will default to the closest max or min amount", 6.0);

	private static <T> Value<T> define(String key, String comment, T defaultValue) {
		Value<T> value = new Value<>(key, comment, defaultValue);
		VALUES.add(value);
		return value;
	}

	public static final class Value<T> {
		public final String key;
		public final String comment;
		public final T defaultValue;
		private volatile T value;

		Value(String key, String comment, T defaultValue) {
			this.key = key;
			this.comment = comment;
			this.defaultValue = defaultValue;
			this.value = defaultValue;
		}

		public T get() {
			return this.value;
		}

		@SuppressWarnings("unchecked")
		public void set(Object value) {
			this.value = (T)value;
		}
	}
}
