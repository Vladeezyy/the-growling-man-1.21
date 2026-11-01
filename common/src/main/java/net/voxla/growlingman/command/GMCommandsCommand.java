package net.voxla.growlingman.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.voxla.growlingman.port.bus.RegisterCommandsEvent;
import net.voxla.growlingman.port.bus.SubscribeEvent;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.voxla.growlingman.procedures.DevOverlayProcProcedure;
import net.voxla.growlingman.procedures.SetGMAIProcedure;
import net.voxla.growlingman.procedures.SetMarkedCommandProcedure;
import net.voxla.growlingman.procedures.SetSpawnTimeCommandProcedure;
import net.voxla.growlingman.procedures.SetUnmarkCommandProcedure;

@EventBusSubscriber
public class GMCommandsCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher()
			.register(
				(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
											"growling_man_dev"
										)
										.requires(s -> s.hasPermission(2)))
									.then(
										Commands.literal("set_mark")
											.then(Commands.argument("mark_number", DoubleArgumentType.doubleArg(0.0, 144000.0)).executes(arguments -> {
												Level world = arguments.getSource().getLevel();
												double x = ((CommandSourceStack)arguments.getSource()).getPosition().x();
												double y = ((CommandSourceStack)arguments.getSource()).getPosition().y();
												double z = ((CommandSourceStack)arguments.getSource()).getPosition().z();
												Entity entity = ((CommandSourceStack)arguments.getSource()).getEntity();
												if (entity == null && world instanceof ServerLevel _servLevel) {
													entity = net.voxla.growlingman.port.platform.Platform.INSTANCE.fakePlayer(_servLevel);
												}

												Direction direction = Direction.DOWN;
												if (entity != null) {
													direction = entity.getDirection();
												}

												SetMarkedCommandProcedure.execute(arguments, entity);
												return 0;
											}))
									))
								.then(
									Commands.literal("set_unmark")
										.then(Commands.argument("unmark_number", DoubleArgumentType.doubleArg(0.0, 9000.0)).executes(arguments -> {
											Level world = arguments.getSource().getLevel();
											double x = ((CommandSourceStack)arguments.getSource()).getPosition().x();
											double y = ((CommandSourceStack)arguments.getSource()).getPosition().y();
											double z = ((CommandSourceStack)arguments.getSource()).getPosition().z();
											Entity entity = ((CommandSourceStack)arguments.getSource()).getEntity();
											if (entity == null && world instanceof ServerLevel _servLevel) {
												entity = net.voxla.growlingman.port.platform.Platform.INSTANCE.fakePlayer(_servLevel);
											}

											Direction direction = Direction.DOWN;
											if (entity != null) {
												direction = entity.getDirection();
											}

											SetUnmarkCommandProcedure.execute(arguments, entity);
											return 0;
										}))
								))
							.then(
								Commands.literal("set_closest_gm_ai")
									.then(Commands.argument("ai_number", DoubleArgumentType.doubleArg(0.0, 25.0)).executes(arguments -> {
										Level world = arguments.getSource().getLevel();
										double x = ((CommandSourceStack)arguments.getSource()).getPosition().x();
										double y = ((CommandSourceStack)arguments.getSource()).getPosition().y();
										double z = ((CommandSourceStack)arguments.getSource()).getPosition().z();
										Entity entity = ((CommandSourceStack)arguments.getSource()).getEntity();
										if (entity == null && world instanceof ServerLevel _servLevel) {
											entity = net.voxla.growlingman.port.platform.Platform.INSTANCE.fakePlayer(_servLevel);
										}

										Direction direction = Direction.DOWN;
										if (entity != null) {
											direction = entity.getDirection();
										}

										SetGMAIProcedure.execute(world, x, y, z, arguments);
										return 0;
									}))
							))
						.then(
							Commands.literal("set_spawn_timer")
								.then(Commands.argument("spawn_time_number", DoubleArgumentType.doubleArg(25.0, 35.0)).executes(arguments -> {
									Level world = arguments.getSource().getLevel();
									double x = ((CommandSourceStack)arguments.getSource()).getPosition().x();
									double y = ((CommandSourceStack)arguments.getSource()).getPosition().y();
									double z = ((CommandSourceStack)arguments.getSource()).getPosition().z();
									Entity entity = ((CommandSourceStack)arguments.getSource()).getEntity();
									if (entity == null && world instanceof ServerLevel _servLevel) {
										entity = net.voxla.growlingman.port.platform.Platform.INSTANCE.fakePlayer(_servLevel);
									}

									Direction direction = Direction.DOWN;
									if (entity != null) {
										direction = entity.getDirection();
									}

									SetSpawnTimeCommandProcedure.execute(world, arguments);
									return 0;
								}))
						))
					.then(Commands.literal("show_dev_overlay").then(Commands.argument("dev_overlay", BoolArgumentType.bool()).executes(arguments -> {
						Level world = arguments.getSource().getLevel();
						double x = ((CommandSourceStack)arguments.getSource()).getPosition().x();
						double y = ((CommandSourceStack)arguments.getSource()).getPosition().y();
						double z = ((CommandSourceStack)arguments.getSource()).getPosition().z();
						Entity entity = ((CommandSourceStack)arguments.getSource()).getEntity();
						if (entity == null && world instanceof ServerLevel _servLevel) {
							entity = net.voxla.growlingman.port.platform.Platform.INSTANCE.fakePlayer(_servLevel);
						}

						Direction direction = Direction.DOWN;
						if (entity != null) {
							direction = entity.getDirection();
						}

						DevOverlayProcProcedure.execute(world, arguments);
						return 0;
					})))
			);
	}
}
