package net.voxla.growlingman.procedures;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.LevelAccessor;
import net.voxla.growlingman.network.GrowlingManModVariables;

public class SetSpawnTimeCommandProcedure {
	public static void execute(LevelAccessor world, CommandContext<CommandSourceStack> arguments) {
		GrowlingManModVariables.MapVariables.get(world).GMSpawnTimer = DoubleArgumentType.getDouble(arguments, "spawn_time_number");
		GrowlingManModVariables.MapVariables.get(world).syncData(world);
	}
}
