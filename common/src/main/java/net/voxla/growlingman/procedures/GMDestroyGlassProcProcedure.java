package net.voxla.growlingman.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.registries.BuiltInRegistries;

public class GMDestroyGlassProcProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		double sx = 0.0;
		double sy = 0.0;
		double sz = 0.0;
		sx = -1.0;

		for (int index0 = 0; index0 < 3; index0++) {
			sy = 0.0;

			for (int index1 = 0; index1 < 3; index1++) {
				sz = -1.0;

				for (int index2 = 0; index2 < 3; index2++) {
					if (BuiltInRegistries.BLOCK
							.getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
							.toString()
							.toLowerCase()
							.contains("glass")
						&& !BuiltInRegistries.BLOCK
							.getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
							.toString()
							.toLowerCase()
							.contains("kiln")
						&& !BuiltInRegistries.BLOCK
							.getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
							.toString()
							.toLowerCase()
							.contains("door")
						&& !BuiltInRegistries.BLOCK
							.getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
							.toString()
							.toLowerCase()
							.contains("furnace")
						&& !BuiltInRegistries.BLOCK
							.getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
							.toString()
							.toLowerCase()
							.contains("chest")
						&& !BuiltInRegistries.BLOCK
							.getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
							.toString()
							.toLowerCase()
							.contains("smoker")
						&& !BuiltInRegistries.BLOCK
							.getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
							.toString()
							.toLowerCase()
							.contains("barrel")
						&& !BuiltInRegistries.BLOCK
							.getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
							.toString()
							.toLowerCase()
							.contains("box")
						&& !BuiltInRegistries.BLOCK
							.getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
							.toString()
							.toLowerCase()
							.contains("reinforced")
						&& !BuiltInRegistries.BLOCK
							.getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
							.toString()
							.toLowerCase()
							.contains("reinforce")
						&& !BuiltInRegistries.BLOCK
							.getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
							.toString()
							.toLowerCase()
							.contains("cabinet")
						&& !BuiltInRegistries.BLOCK
							.getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
							.toString()
							.toLowerCase()
							.contains("dresser")
						&& !BuiltInRegistries.BLOCK
							.getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
							.toString()
							.toLowerCase()
							.contains("drawer")
						&& !BuiltInRegistries.BLOCK
							.getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
							.toString()
							.toLowerCase()
							.contains("shatter")
						&& !BuiltInRegistries.BLOCK
							.getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
							.toString()
							.toLowerCase()
							.contains("shattered")
						&& !world.isClientSide()) {
						world.destroyBlock(BlockPos.containing(x + sx, y + sy, z + sz), false);
					}

					sz++;
				}

				sy++;
			}

			sx++;
		}
	}
}
