package net.voxla.growlingman.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

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
               if (ForgeRegistries.BLOCKS
                     .getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
                     .toString()
                     .toLowerCase()
                     .contains("glass")
                  && !ForgeRegistries.BLOCKS
                     .getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
                     .toString()
                     .toLowerCase()
                     .contains("kiln")
                  && !ForgeRegistries.BLOCKS
                     .getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
                     .toString()
                     .toLowerCase()
                     .contains("door")
                  && !ForgeRegistries.BLOCKS
                     .getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
                     .toString()
                     .toLowerCase()
                     .contains("furnace")
                  && !ForgeRegistries.BLOCKS
                     .getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
                     .toString()
                     .toLowerCase()
                     .contains("chest")
                  && !ForgeRegistries.BLOCKS
                     .getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
                     .toString()
                     .toLowerCase()
                     .contains("smoker")
                  && !ForgeRegistries.BLOCKS
                     .getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
                     .toString()
                     .toLowerCase()
                     .contains("barrel")
                  && !ForgeRegistries.BLOCKS
                     .getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
                     .toString()
                     .toLowerCase()
                     .contains("box")
                  && !ForgeRegistries.BLOCKS
                     .getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
                     .toString()
                     .toLowerCase()
                     .contains("reinforced")
                  && !ForgeRegistries.BLOCKS
                     .getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
                     .toString()
                     .toLowerCase()
                     .contains("reinforce")
                  && !ForgeRegistries.BLOCKS
                     .getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
                     .toString()
                     .toLowerCase()
                     .contains("cabinet")
                  && !ForgeRegistries.BLOCKS
                     .getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
                     .toString()
                     .toLowerCase()
                     .contains("dresser")
                  && !ForgeRegistries.BLOCKS
                     .getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
                     .toString()
                     .toLowerCase()
                     .contains("drawer")
                  && !ForgeRegistries.BLOCKS
                     .getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
                     .toString()
                     .toLowerCase()
                     .contains("shatter")
                  && !ForgeRegistries.BLOCKS
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
