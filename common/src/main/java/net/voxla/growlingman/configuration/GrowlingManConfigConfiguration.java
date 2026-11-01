package net.voxla.growlingman.configuration;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class GrowlingManConfigConfiguration {
   public static final Builder BUILDER = new Builder();
   private static final Builder PUSH = BUILDER.push("Growling Man Config");
   public static final ConfigValue<Double> GM_SPAWNTIME_MIN = BUILDER.comment(
         "a random number is generated between min and max for the spawn time, the spawn numbers are in seconds so set how many seconds you want it to take for him to spawn on min and max, WARNING do not make max time lower than min time otherwise it will cause bugs or crash the game"
      )
      .define("GM Spawntime Min", 60.0);
   public static final ConfigValue<Double> GM_SPAWNTIME_MAX = BUILDER.comment(
         "a random number is generated between min and max for the spawn time, the spawn numbers are in seconds so set how many seconds you want it to take for him to spawn on min and max, WARNING do not make max time lower than min time otherwise it will cause bugs or crash the game"
      )
      .define("GM Spawntime Max", 240.0);
   public static final ConfigValue<Double> GM_SPAWN_CHANCE = BUILDER.comment(
         "if you want a higher chance of him spawning set the spawn chance to a lower number, if you want a lower chance for him to spawn set the spawn chance to a higher number, WARNING do not set this number below 1 otherwise it may bug or crash the game"
      )
      .define("GM Spawn Chance", 1.0);
   public static final ConfigValue<Boolean> REGENERATE_BLOCKS = BUILDER.define("Regenerate Blocks If Broken By The Entity", false);
   public static final ConfigValue<Double> GM_CHASETIME_MIN = BUILDER.comment(
         "a random number is generated between min and max for the chase time, the chase numbers are in minutes so set how many minutes you want it to take for him to disappear on min and max, WARNING do not make max time lower than min time otherwise it will cause bugs or crash the game"
      )
      .define("GM Chase Time Min", 1.0);
   public static final ConfigValue<Double> GM_CHASETIME_MAX = BUILDER.comment(
         "a random number is generated between min and max for the chase time, the chase numbers are in minutes so set how many minutes you want it to take for him to disappear on min and max, WARNING do not make max time lower than min time otherwise it will cause bugs or crash the game"
      )
      .define("GM Chase Time Max", 2.0);
   public static final ConfigValue<Boolean> DONT_SPAWN_GM_ON_FIRST_DAY = BUILDER.define("Don't Spawn GM On First Day", false);
   public static final ConfigValue<Double> SPAWN_DISTANCE = BUILDER.comment(
         "a random number is generated between 20 and the set config number for the spawn distance, spawn distance is in blocks so 100 would be 100 blocks away etc, WARNING do not set spawn distance below 20 otherwise it will cause bugs or crash the game"
      )
      .define("GM Max Spawn Distance", 70.0);
   public static final ConfigValue<Double> ANIMALS_KILLED_QUICKLY_TO_GET_MARKED = BUILDER.comment(
         "max for animal kills is 20 and min is 3, if you set a number lower or higher then it will default to the closest max or min amount"
      )
      .define("Amount Of Quick Animal Kills Needed To Get Marked", 6.0);

   private static final Builder POP = BUILDER.pop();
   public static final ForgeConfigSpec SPEC = BUILDER.build();
}
