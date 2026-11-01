package net.voxla.growlingman.procedures;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;
import net.voxla.growlingman.init.GrowlingManModBlocks;

public class GMBreakGlassProcProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      String BlockN = "";
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
                     .contains("shattered")) {
                  BlockN = ForgeRegistries.BLOCKS.getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock()).toString();
                  if (!world.isClientSide()) {
                     world.levelEvent(2001, BlockPos.containing(x + sx, y + sy, z + sz), Block.getId(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz))));
                     BlockPos _bp = BlockPos.containing(x + sx, y + sy, z + sz);
                     BlockState _bs = ((Block)GrowlingManModBlocks.REGEN_BLOCK.get()).defaultBlockState();
                     BlockState _bso = world.getBlockState(_bp);
                     UnmodifiableIterator _level = _bso.getValues().entrySet().iterator();

                     while (_level.hasNext()) {
                        Entry<Property<?>, Comparable<?>> entry = (Entry<Property<?>, Comparable<?>>)_level.next();
                        Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
                        if (_property != null && _bs.getValue(_property) != null) {
                           try {
                              _bs = (BlockState)_bs.setValue(_property, entry.getValue());
                           } catch (Exception var24) {
                           }
                        }
                     }

                     BlockEntity _be = world.getBlockEntity(_bp);
                     CompoundTag _bnbt = null;
                     if (_be != null) {
                        _bnbt = _be.saveWithFullMetadata();
                        _be.setRemoved();
                     }

                     world.setBlock(_bp, _bs, 3);
                     if (_bnbt != null) {
                        BlockEntity var37 = world.getBlockEntity(_bp);
                        if (var37 != null) {
                           try {
                              var37.load(_bnbt);
                           } catch (Exception var25) {
                           }
                        }
                     }

                     if (!world.isClientSide()) {
                        _bp = BlockPos.containing(x + sx, y + sy, z + sz);
                        BlockEntity _blockEntity = world.getBlockEntity(_bp);
                        _bso = world.getBlockState(_bp);
                        if (_blockEntity != null) {
                           _blockEntity.getPersistentData().putString("BlockName", BlockN);
                        }

                        if (world instanceof Level _levelx) {
                           _levelx.sendBlockUpdated(_bp, _bso, _bso, 3);
                        }
                     }

                     if (ForgeRegistries.BLOCKS
                           .getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
                           .toString()
                           .toLowerCase()
                           .contains("pane")
                        && !world.isClientSide()) {
                        _bp = BlockPos.containing(x + sx, y + sy, z + sz);
                        BlockEntity _blockEntityx = world.getBlockEntity(_bp);
                        _bso = world.getBlockState(_bp);
                        if (_blockEntityx != null) {
                           _blockEntityx.getPersistentData().putBoolean("IsPane", true);
                        }

                        if (world instanceof Level _levelx) {
                           _levelx.sendBlockUpdated(_bp, _bso, _bso, 3);
                        }
                     }
                  }
               }

               sz++;
            }

            sy++;
         }

         sx++;
      }
   }
}
