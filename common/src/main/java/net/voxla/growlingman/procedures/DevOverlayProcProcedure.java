package net.voxla.growlingman.procedures;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.LevelAccessor;
import net.voxla.growlingman.network.GrowlingManModVariables;

public class DevOverlayProcProcedure {
	public static void execute(LevelAccessor world, CommandContext<CommandSourceStack> arguments) {
		GrowlingManModVariables.MapVariables.get(world).gmdev_overlay = BoolArgumentType.getBool(arguments, "dev_overlay");
		GrowlingManModVariables.MapVariables.get(world).syncData(world);
	}
}
