package net.voxla.growlingman.procedures;

import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.registries.BuiltInRegistries;
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
							.contains("shattered")) {
						BlockN = BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock()).toString();
						if (!world.isClientSide()) {
							world.levelEvent(2001, BlockPos.containing(x + sx, y + sy, z + sz), Block.getId(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz))));
							BlockPos _bp = BlockPos.containing(x + sx, y + sy, z + sz);
							BlockState _bs = ((Block)GrowlingManModBlocks.REGEN_BLOCK.get()).defaultBlockState();
							BlockState _bso = world.getBlockState(_bp);
							for (Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
								Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
								if (_property != null && _bs.getValue(_property) != null) {
									try {
										_bs = (BlockState)_bs.setValue(_property, (Comparable)entry.getValue());
									} catch (Exception var24) {
									}
								}
							}

							BlockEntity _be = world.getBlockEntity(_bp);
							CompoundTag _bnbt = null;
							if (_be != null) {
								_bnbt = _be.saveWithFullMetadata(world.registryAccess());
								_be.setRemoved();
							}

							world.setBlock(_bp, _bs, 3);
							if (_bnbt != null) {
								BlockEntity var37 = world.getBlockEntity(_bp);
								if (var37 != null) {
									try {
										var37.loadWithComponents(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, world.registryAccess(), _bnbt));
									} catch (Exception var25) {
									}
								}
							}

							if (!world.isClientSide()) {
								_bp = BlockPos.containing(x + sx, y + sy, z + sz);
								BlockEntity _blockEntity = world.getBlockEntity(_bp);
								_bso = world.getBlockState(_bp);
								if (_blockEntity != null) {
									net.voxla.growlingman.port.PortUtil.data(_blockEntity).putString("BlockName", BlockN);
								}

								if (world instanceof Level _levelx) {
									_levelx.sendBlockUpdated(_bp, _bso, _bso, 3);
								}
							}

							if (BuiltInRegistries.BLOCK
									.getKey(world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock())
									.toString()
									.toLowerCase()
									.contains("pane")
								&& !world.isClientSide()) {
								_bp = BlockPos.containing(x + sx, y + sy, z + sz);
								BlockEntity _blockEntityx = world.getBlockEntity(_bp);
								_bso = world.getBlockState(_bp);
								if (_blockEntityx != null) {
									net.voxla.growlingman.port.PortUtil.data(_blockEntityx).putBoolean("IsPane", true);
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
