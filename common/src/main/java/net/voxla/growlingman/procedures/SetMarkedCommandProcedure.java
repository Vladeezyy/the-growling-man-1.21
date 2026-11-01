package net.voxla.growlingman.procedures;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import net.voxla.growlingman.network.GrowlingManModVariables;

public class SetMarkedCommandProcedure {
	public static void execute(CommandContext<CommandSourceStack> arguments, Entity entity) {
		if (entity != null) {
			double _setval = DoubleArgumentType.getDouble(arguments, "mark_number");
			{
GrowlingManModVariables.PlayerVariables _vars = GrowlingManModVariables.of(entity);
				_vars.IsMarked = _setval;
				_vars.syncPlayerVariables(entity);
			}
		}
	}
}
