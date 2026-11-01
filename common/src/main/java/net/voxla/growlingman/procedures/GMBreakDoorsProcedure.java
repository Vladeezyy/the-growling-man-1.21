package net.voxla.growlingman.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;
import net.voxla.growlingman.entity.GrowlingManEntity;

public class GMBreakDoorsProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity != null) {
			if (BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("door")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("trap")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("copper")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("iron")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("metal")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("reinforced")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("silver")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("gold")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("platinum")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("platinum")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("cobalt")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("zinc")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("lead")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("titanium")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("nickel")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("brass")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("bronze")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("tungsten")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("aluminum")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("chromium")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("granite")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("diorite")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("andesite")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("stone")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("deepslate")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x + 1.0, y, z)).getBlock()).toString().toLowerCase().contains("quartz")) {
				if ((entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_BreakDoorAmount) : 0) < 3
					&& (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_BreakDoorTimer) : 0) <= 0) {
					if (entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData()
							.set(
								GrowlingManEntity.DATA_BreakDoorAmount,
								(entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_BreakDoorAmount) : 0)
									+ 1
							);
					}

					if (entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_BreakDoorTimer, 20);
					}

					if (!world.isClientSide() && world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(
								null,
								BlockPos.containing(x + 1.0, y + 1.0, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.attack_wooden_door")),
								SoundSource.BLOCKS,
								1.0F,
								1.0F
							);
						} else {
							_level.playLocalSound(
								x + 1.0,
								y + 1.0,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.attack_wooden_door")),
								SoundSource.BLOCKS,
								1.0F,
								1.0F,
								false
							);
						}
					}
				}

				if ((entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_BreakDoorAmount) : 0) >= 3
					&& (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_BreakDoorTimer) : 0) <= 0) {
					if (entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_BreakDoorAmount, 0);
					}

					if (!world.isClientSide()) {
						BlockPos _pos = BlockPos.containing(x + 1.0, y, z);
						Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x + 1.0, y + 0.5, z), null);
						world.destroyBlock(_pos, false);
						if (world instanceof Level _levelx) {
							if (!_levelx.isClientSide()) {
								_levelx.playSound(
									null,
									BlockPos.containing(x + 1.0, y + 1.0, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.attack_wooden_door")),
									SoundSource.BLOCKS,
									1.0F,
									1.0F
								);
							} else {
								_levelx.playLocalSound(
									x + 1.0,
									y + 1.0,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.attack_wooden_door")),
									SoundSource.BLOCKS,
									1.0F,
									1.0F,
									false
								);
							}
						}

						if (world instanceof Level _levelxx) {
							if (!_levelxx.isClientSide()) {
								_levelxx.playSound(
									null,
									BlockPos.containing(x + 1.0, y + 1.0, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.break_wooden_door")),
									SoundSource.BLOCKS,
									1.0F,
									1.0F
								);
							} else {
								_levelxx.playLocalSound(
									x + 1.0,
									y + 1.0,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.break_wooden_door")),
									SoundSource.BLOCKS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					}
				}
			} else if (BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("door")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("trap")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("copper")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("iron")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("metal")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("reinforced")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("silver")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("gold")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("platinum")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("platinum")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("cobalt")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("zinc")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("lead")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("titanium")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("nickel")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("brass")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("bronze")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("tungsten")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("aluminum")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("chromium")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("granite")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("diorite")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("andesite")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("stone")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("deepslate")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x - 1.0, y, z)).getBlock()).toString().toLowerCase().contains("quartz")) {
				if ((entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_BreakDoorAmount) : 0) < 3
					&& (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_BreakDoorTimer) : 0) <= 0) {
					if (entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData()
							.set(
								GrowlingManEntity.DATA_BreakDoorAmount,
								(entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_BreakDoorAmount) : 0)
									+ 1
							);
					}

					if (entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_BreakDoorTimer, 20);
					}

					if (!world.isClientSide() && world instanceof Level _levelxxx) {
						if (!_levelxxx.isClientSide()) {
							_levelxxx.playSound(
								null,
								BlockPos.containing(x - 1.0, y + 1.0, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.attack_wooden_door")),
								SoundSource.BLOCKS,
								1.0F,
								1.0F
							);
						} else {
							_levelxxx.playLocalSound(
								x - 1.0,
								y + 1.0,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.attack_wooden_door")),
								SoundSource.BLOCKS,
								1.0F,
								1.0F,
								false
							);
						}
					}
				}

				if ((entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_BreakDoorAmount) : 0) >= 3
					&& (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_BreakDoorTimer) : 0) <= 0) {
					if (entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_BreakDoorAmount, 0);
					}

					if (!world.isClientSide()) {
						BlockPos _posx = BlockPos.containing(x - 1.0, y, z);
						Block.dropResources(world.getBlockState(_posx), world, BlockPos.containing(x + 1.0, y + 0.5, z), null);
						world.destroyBlock(_posx, false);
						if (world instanceof Level _levelxxxx) {
							if (!_levelxxxx.isClientSide()) {
								_levelxxxx.playSound(
									null,
									BlockPos.containing(x - 1.0, y + 1.0, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.attack_wooden_door")),
									SoundSource.BLOCKS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxx.playLocalSound(
									x - 1.0,
									y + 1.0,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.attack_wooden_door")),
									SoundSource.BLOCKS,
									1.0F,
									1.0F,
									false
								);
							}
						}

						if (world instanceof Level _levelxxxxx) {
							if (!_levelxxxxx.isClientSide()) {
								_levelxxxxx.playSound(
									null,
									BlockPos.containing(x - 1.0, y + 1.0, z),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.break_wooden_door")),
									SoundSource.BLOCKS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxx.playLocalSound(
									x - 1.0,
									y + 1.0,
									z,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.break_wooden_door")),
									SoundSource.BLOCKS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					}
				}
			} else if (BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("door")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("trap")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("copper")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("iron")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("metal")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("reinforced")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("silver")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("gold")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("platinum")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("platinum")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("cobalt")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("zinc")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("lead")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("titanium")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("nickel")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("brass")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("bronze")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("tungsten")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("aluminum")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("chromium")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("granite")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("diorite")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("andesite")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("stone")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("deepslate")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z + 1.0)).getBlock()).toString().toLowerCase().contains("quartz")) {
				if ((entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_BreakDoorAmount) : 0) < 3
					&& (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_BreakDoorTimer) : 0) <= 0) {
					if (entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData()
							.set(
								GrowlingManEntity.DATA_BreakDoorAmount,
								(entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_BreakDoorAmount) : 0)
									+ 1
							);
					}

					if (entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_BreakDoorTimer, 20);
					}

					if (!world.isClientSide() && world instanceof Level _levelxxxxxx) {
						if (!_levelxxxxxx.isClientSide()) {
							_levelxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + 1.0, z + 1.0),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.attack_wooden_door")),
								SoundSource.BLOCKS,
								1.0F,
								1.0F
							);
						} else {
							_levelxxxxxx.playLocalSound(
								x,
								y + 1.0,
								z + 1.0,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.attack_wooden_door")),
								SoundSource.BLOCKS,
								1.0F,
								1.0F,
								false
							);
						}
					}
				}

				if ((entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_BreakDoorAmount) : 0) >= 3
					&& (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_BreakDoorTimer) : 0) <= 0) {
					if (entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_BreakDoorAmount, 0);
					}

					if (!world.isClientSide()) {
						BlockPos _posxx = BlockPos.containing(x, y, z + 1.0);
						Block.dropResources(world.getBlockState(_posxx), world, BlockPos.containing(x, y + 0.5, z + 1.0), null);
						world.destroyBlock(_posxx, false);
						if (world instanceof Level _levelxxxxxxx) {
							if (!_levelxxxxxxx.isClientSide()) {
								_levelxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + 1.0, z + 1.0),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.attack_wooden_door")),
									SoundSource.BLOCKS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxx.playLocalSound(
									x,
									y + 1.0,
									z + 1.0,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.attack_wooden_door")),
									SoundSource.BLOCKS,
									1.0F,
									1.0F,
									false
								);
							}
						}

						if (world instanceof Level _levelxxxxxxxx) {
							if (!_levelxxxxxxxx.isClientSide()) {
								_levelxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + 1.0, z + 1.0),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.break_wooden_door")),
									SoundSource.BLOCKS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxx.playLocalSound(
									x,
									y + 1.0,
									z + 1.0,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.break_wooden_door")),
									SoundSource.BLOCKS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					}
				}
			} else if (BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("door")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("trap")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("copper")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("iron")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("metal")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("reinforced")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("silver")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("gold")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("platinum")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("platinum")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("cobalt")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("zinc")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("lead")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("titanium")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("nickel")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("brass")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("bronze")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("tungsten")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("aluminum")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("chromium")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("granite")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("diorite")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("andesite")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("stone")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("deepslate")
				&& !BuiltInRegistries.BLOCK.getKey(world.getBlockState(BlockPos.containing(x, y, z - 1.0)).getBlock()).toString().toLowerCase().contains("quartz")) {
				if ((entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_BreakDoorAmount) : 0) < 3
					&& (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_BreakDoorTimer) : 0) <= 0) {
					if (entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData()
							.set(
								GrowlingManEntity.DATA_BreakDoorAmount,
								(entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_BreakDoorAmount) : 0)
									+ 1
							);
					}

					if (entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_BreakDoorTimer, 20);
					}

					if (!world.isClientSide() && world instanceof Level _levelxxxxxxxxx) {
						if (!_levelxxxxxxxxx.isClientSide()) {
							_levelxxxxxxxxx.playSound(
								null,
								BlockPos.containing(x, y + 1.0, z - 1.0),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.attack_wooden_door")),
								SoundSource.BLOCKS,
								1.0F,
								1.0F
							);
						} else {
							_levelxxxxxxxxx.playLocalSound(
								x,
								y + 1.0,
								z - 1.0,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.attack_wooden_door")),
								SoundSource.BLOCKS,
								1.0F,
								1.0F,
								false
							);
						}
					}
				}

				if ((entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_BreakDoorAmount) : 0) >= 3
					&& (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_BreakDoorTimer) : 0) <= 0) {
					if (entity instanceof GrowlingManEntity _datEntSetI) {
						_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_BreakDoorAmount, 0);
					}

					if (!world.isClientSide()) {
						BlockPos _posxxx = BlockPos.containing(x, y, z - 1.0);
						Block.dropResources(world.getBlockState(_posxxx), world, BlockPos.containing(x, y + 0.5, z - 1.0), null);
						world.destroyBlock(_posxxx, false);
						if (world instanceof Level _levelxxxxxxxxxx) {
							if (!_levelxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + 1.0, z - 1.0),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.attack_wooden_door")),
									SoundSource.BLOCKS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxx.playLocalSound(
									x,
									y + 1.0,
									z - 1.0,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.attack_wooden_door")),
									SoundSource.BLOCKS,
									1.0F,
									1.0F,
									false
								);
							}
						}

						if (world instanceof Level _levelxxxxxxxxxxx) {
							if (!_levelxxxxxxxxxxx.isClientSide()) {
								_levelxxxxxxxxxxx.playSound(
									null,
									BlockPos.containing(x, y + 1.0, z - 1.0),
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.break_wooden_door")),
									SoundSource.BLOCKS,
									1.0F,
									1.0F
								);
							} else {
								_levelxxxxxxxxxxx.playLocalSound(
									x,
									y + 1.0,
									z - 1.0,
									(SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("entity.zombie.break_wooden_door")),
									SoundSource.BLOCKS,
									1.0F,
									1.0F,
									false
								);
							}
						}
					}
				}
			}

			if ((entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_BreakDoorTimer) : 0) > 0
				&& entity instanceof GrowlingManEntity _datEntSetI) {
				_datEntSetI.getEntityData()
					.set(
						GrowlingManEntity.DATA_BreakDoorTimer,
						(entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_BreakDoorTimer) : 0) - 1
					);
			}
		}
	}
}
