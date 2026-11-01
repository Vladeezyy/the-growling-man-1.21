package net.voxla.growlingman.procedures;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import java.util.Comparator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.voxla.growlingman.entity.GrowlingManEntity;

public class SetGMAIProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, CommandContext<CommandSourceStack> arguments) {
		Vec3 _center = new Vec3(x, y, z);

		for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(75.0), e -> true)
			.stream()
			.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
			.toList()) {
			if (entityiterator instanceof GrowlingManEntity) {
				if (entityiterator instanceof GrowlingManEntity _datEntSetI) {
					_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_AI, (int)DoubleArgumentType.getDouble(arguments, "ai_number"));
				}
				break;
			}
		}
	}
}
