package net.voxla.growlingman.procedures;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Locale;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

public class RegenBlockOnTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (!(new Object() {
         public String getValue(LevelAccessor world, BlockPos pos, String tag) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            return blockEntity != null ? blockEntity.getPersistentData().getString(tag) : "";
         }
      }).getValue(world, BlockPos.containing(x, y, z), "BlockName").equals("")) {
         if ((new Object() {
            public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
               BlockEntity blockEntity = world.getBlockEntity(pos);
               return blockEntity != null ? blockEntity.getPersistentData().getBoolean(tag) : false;
            }
         }).getValue(world, BlockPos.containing(x, y, z), "IsPane")) {
            if (!world.isClientSide()) {
               BlockPos _bp = BlockPos.containing(x, y, z);
               BlockState _bs = ((Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation((new Object() {
                  public String getValue(LevelAccessor world, BlockPos pos, String tag) {
                     BlockEntity blockEntity = world.getBlockEntity(pos);
                     return blockEntity != null ? blockEntity.getPersistentData().getString(tag) : "";
                  }
               }).getValue(world, BlockPos.containing(x, y, z), "BlockName").toLowerCase(Locale.ENGLISH)))).defaultBlockState();
               BlockState _bso = world.getBlockState(_bp);
               UnmodifiableIterator var10 = _bso.getValues().entrySet().iterator();

               while (var10.hasNext()) {
                  Entry<Property<?>, Comparable<?>> entry = (Entry<Property<?>, Comparable<?>>)var10.next();
                  Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
                  if (_property != null && _bs.getValue(_property) != null) {
                     try {
                        _bs = (BlockState)_bs.setValue(_property, entry.getValue());
                     } catch (Exception var16) {
                     }
                  }
               }

               world.setBlock(_bp, _bs, 3);
               if (world.getBlockState(BlockPos.containing(x + 1.0, y, z)).canOcclude()) {
                  _bp = BlockPos.containing(x, y, z);
                  _bs = world.getBlockState(_bp);
                  if (_bs.getBlock().getStateDefinition().getProperty("east") instanceof BooleanProperty _booleanProp) {
                     world.setBlock(_bp, (BlockState)_bs.setValue(_booleanProp, true), 3);
                  }
               }

               if (world.getBlockState(BlockPos.containing(x - 1.0, y, z)).canOcclude()) {
                  _bp = BlockPos.containing(x, y, z);
                  _bs = world.getBlockState(_bp);
                  if (_bs.getBlock().getStateDefinition().getProperty("west") instanceof BooleanProperty _booleanProp) {
                     world.setBlock(_bp, (BlockState)_bs.setValue(_booleanProp, true), 3);
                  }
               }

               if (world.getBlockState(BlockPos.containing(x, y, z + 1.0)).canOcclude()) {
                  _bp = BlockPos.containing(x, y, z);
                  _bs = world.getBlockState(_bp);
                  if (_bs.getBlock().getStateDefinition().getProperty("south") instanceof BooleanProperty _booleanProp) {
                     world.setBlock(_bp, (BlockState)_bs.setValue(_booleanProp, true), 3);
                  }
               }

               if (world.getBlockState(BlockPos.containing(x, y, z - 1.0)).canOcclude()) {
                  _bp = BlockPos.containing(x, y, z);
                  _bs = world.getBlockState(_bp);
                  if (_bs.getBlock().getStateDefinition().getProperty("north") instanceof BooleanProperty _booleanProp) {
                     world.setBlock(_bp, (BlockState)_bs.setValue(_booleanProp, true), 3);
                  }
               }
            }
         } else if (!(new Object() {
            public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
               BlockEntity blockEntity = world.getBlockEntity(pos);
               return blockEntity != null ? blockEntity.getPersistentData().getBoolean(tag) : false;
            }
         }).getValue(world, BlockPos.containing(x, y, z), "IsPane") && !world.isClientSide()) {
            BlockPos _bp = BlockPos.containing(x, y, z);
            BlockState _bs = ((Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation((new Object() {
               public String getValue(LevelAccessor world, BlockPos pos, String tag) {
                  BlockEntity blockEntity = world.getBlockEntity(pos);
                  return blockEntity != null ? blockEntity.getPersistentData().getString(tag) : "";
               }
            }).getValue(world, BlockPos.containing(x, y, z), "BlockName").toLowerCase(Locale.ENGLISH)))).defaultBlockState();
            BlockState _bso = world.getBlockState(_bp);
            UnmodifiableIterator var39 = _bso.getValues().entrySet().iterator();

            while (var39.hasNext()) {
               Entry<Property<?>, Comparable<?>> entry = (Entry<Property<?>, Comparable<?>>)var39.next();
               Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
               if (_property != null && _bs.getValue(_property) != null) {
                  try {
                     _bs = (BlockState)_bs.setValue(_property, entry.getValue());
                  } catch (Exception var15) {
                  }
               }
            }

            world.setBlock(_bp, _bs, 3);
         }
      } else if ((new Object() {
         public String getValue(LevelAccessor world, BlockPos pos, String tag) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            return blockEntity != null ? blockEntity.getPersistentData().getString(tag) : "";
         }
      }).getValue(world, BlockPos.containing(x, y, z), "BlockName").equals("") && !world.isClientSide()) {
         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = Blocks.AIR.defaultBlockState();
         BlockState _bso = world.getBlockState(_bp);
         UnmodifiableIterator var40 = _bso.getValues().entrySet().iterator();

         while (var40.hasNext()) {
            Entry<Property<?>, Comparable<?>> entry = (Entry<Property<?>, Comparable<?>>)var40.next();
            Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
            if (_property != null && _bs.getValue(_property) != null) {
               try {
                  _bs = (BlockState)_bs.setValue(_property, entry.getValue());
               } catch (Exception var14) {
               }
            }
         }

         world.setBlock(_bp, _bs, 3);
      }
   }
}
