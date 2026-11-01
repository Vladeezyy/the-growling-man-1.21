package net.voxla.growlingman.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.voxla.growlingman.entity.GrowlingManEntity;

public class GrowlingManBoundingBoxScaleProcedure {
	public static double execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null) {
			return 0.0;
		} else if ((
				!world.getBlockState(BlockPos.containing(x + 1.0, y + 0.0, z)).canOcclude()
						&& !world.getBlockState(BlockPos.containing(x + 1.0, y + 1.0, z)).canOcclude()
						&& (
							world.getBlockState(BlockPos.containing(x + 1.0, y + 2.0, z)).canOcclude()
								|| world.getBlockState(BlockPos.containing(x + 1.0, y + 2.0, z)).is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.parse("minecraft:leaves")))
						)
					|| !world.getBlockState(BlockPos.containing(x, y + 0.0, z)).canOcclude()
						&& !world.getBlockState(BlockPos.containing(x, y + 1.0, z)).canOcclude()
						&& (
							world.getBlockState(BlockPos.containing(x, y + 2.0, z)).canOcclude()
								|| world.getBlockState(BlockPos.containing(x, y + 2.0, z)).is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.parse("minecraft:leaves")))
						)
					|| !world.getBlockState(BlockPos.containing(x + 1.0, y + 2.0, z)).canOcclude()
						&& !world.getBlockState(BlockPos.containing(x + 1.0, y + 1.0, z)).canOcclude()
						&& (
							world.getBlockState(BlockPos.containing(x + 1.0, y + 0.0, z)).canOcclude() && world.getBlockState(BlockPos.containing(x + 1.0, y + 3.0, z)).canOcclude()
								|| world.getBlockState(BlockPos.containing(x + 1.0, y + 0.0, z)).is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.parse("minecraft:leaves")))
									&& world.getBlockState(BlockPos.containing(x + 1.0, y + 3.0, z)).is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.parse("minecraft:leaves")))
						)
			)
			&& !(entity instanceof GrowlingManEntity _datEntL18 && (Boolean)_datEntL18.getEntityData().get(GrowlingManEntity.DATA_HorizontalClimbing))) {
			return 0.55;
		} else if ((
				!world.getBlockState(BlockPos.containing(x - 1.0, y + 0.0, z)).canOcclude()
						&& !world.getBlockState(BlockPos.containing(x - 1.0, y + 1.0, z)).canOcclude()
						&& (
							world.getBlockState(BlockPos.containing(x - 1.0, y + 2.0, z)).canOcclude()
								|| world.getBlockState(BlockPos.containing(x - 1.0, y + 2.0, z)).is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.parse("minecraft:leaves")))
						)
					|| !world.getBlockState(BlockPos.containing(x, y + 0.0, z)).canOcclude()
						&& !world.getBlockState(BlockPos.containing(x, y + 1.0, z)).canOcclude()
						&& (
							world.getBlockState(BlockPos.containing(x, y + 2.0, z)).canOcclude()
								|| world.getBlockState(BlockPos.containing(x, y + 2.0, z)).is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.parse("minecraft:leaves")))
						)
					|| !world.getBlockState(BlockPos.containing(x - 1.0, y + 2.0, z)).canOcclude()
						&& !world.getBlockState(BlockPos.containing(x - 1.0, y + 1.0, z)).canOcclude()
						&& (
							world.getBlockState(BlockPos.containing(x - 1.0, y + 0.0, z)).canOcclude() && world.getBlockState(BlockPos.containing(x - 1.0, y + 3.0, z)).canOcclude()
								|| world.getBlockState(BlockPos.containing(x - 1.0, y + 0.0, z)).is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.parse("minecraft:leaves")))
									&& world.getBlockState(BlockPos.containing(x - 1.0, y + 3.0, z)).is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.parse("minecraft:leaves")))
						)
			)
			&& !(entity instanceof GrowlingManEntity _datEntL37 && (Boolean)_datEntL37.getEntityData().get(GrowlingManEntity.DATA_HorizontalClimbing))) {
			return 0.55;
		} else if ((
				!world.getBlockState(BlockPos.containing(x, y + 0.0, z + 1.0)).canOcclude()
						&& !world.getBlockState(BlockPos.containing(x, y + 1.0, z + 1.0)).canOcclude()
						&& (
							world.getBlockState(BlockPos.containing(x, y + 2.0, z + 1.0)).canOcclude()
								|| world.getBlockState(BlockPos.containing(x, y + 2.0, z + 1.0)).is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.parse("minecraft:leaves")))
						)
					|| !world.getBlockState(BlockPos.containing(x, y + 0.0, z)).canOcclude()
						&& !world.getBlockState(BlockPos.containing(x, y + 1.0, z)).canOcclude()
						&& (
							world.getBlockState(BlockPos.containing(x, y + 2.0, z)).canOcclude()
								|| world.getBlockState(BlockPos.containing(x, y + 2.0, z)).is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.parse("minecraft:leaves")))
						)
					|| !world.getBlockState(BlockPos.containing(x, y + 2.0, z + 1.0)).canOcclude()
						&& !world.getBlockState(BlockPos.containing(x, y + 1.0, z + 1.0)).canOcclude()
						&& (
							world.getBlockState(BlockPos.containing(x, y + 0.0, z + 1.0)).canOcclude() && world.getBlockState(BlockPos.containing(x, y + 3.0, z + 1.0)).canOcclude()
								|| world.getBlockState(BlockPos.containing(x, y + 0.0, z + 1.0)).is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.parse("minecraft:leaves")))
									&& world.getBlockState(BlockPos.containing(x, y + 3.0, z + 1.0)).is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.parse("minecraft:leaves")))
						)
			)
			&& !(entity instanceof GrowlingManEntity _datEntL56 && (Boolean)_datEntL56.getEntityData().get(GrowlingManEntity.DATA_HorizontalClimbing))) {
			return 0.55;
		} else if ((
				!world.getBlockState(BlockPos.containing(x, y + 0.0, z - 1.0)).canOcclude()
						&& !world.getBlockState(BlockPos.containing(x, y + 1.0, z - 1.0)).canOcclude()
						&& (
							world.getBlockState(BlockPos.containing(x, y + 2.0, z - 1.0)).canOcclude()
								|| world.getBlockState(BlockPos.containing(x, y + 2.0, z - 1.0)).is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.parse("minecraft:leaves")))
						)
					|| !world.getBlockState(BlockPos.containing(x, y + 0.0, z)).canOcclude()
						&& !world.getBlockState(BlockPos.containing(x, y + 1.0, z)).canOcclude()
						&& (
							world.getBlockState(BlockPos.containing(x, y + 2.0, z)).canOcclude()
								|| world.getBlockState(BlockPos.containing(x, y + 2.0, z)).is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.parse("minecraft:leaves")))
						)
					|| !world.getBlockState(BlockPos.containing(x, y + 2.0, z - 1.0)).canOcclude()
						&& !world.getBlockState(BlockPos.containing(x, y + 1.0, z - 1.0)).canOcclude()
						&& (
							world.getBlockState(BlockPos.containing(x, y + 0.0, z - 1.0)).canOcclude() && world.getBlockState(BlockPos.containing(x, y + 3.0, z - 1.0)).canOcclude()
								|| world.getBlockState(BlockPos.containing(x, y + 0.0, z - 1.0)).is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.parse("minecraft:leaves")))
									&& world.getBlockState(BlockPos.containing(x, y + 3.0, z - 1.0)).is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.parse("minecraft:leaves")))
						)
			)
			&& !(entity instanceof GrowlingManEntity _datEntL75 && (Boolean)_datEntL75.getEntityData().get(GrowlingManEntity.DATA_HorizontalClimbing))) {
			return 0.55;
		} else {
			return world.getBlockState(BlockPos.containing(x, y, z)).getBlock() != Blocks.WATER
					&& world.getBlockState(BlockPos.containing(x, y, z)).getBlock() != Blocks.WATER
					&& world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() != Blocks.WATER
					&& world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() != Blocks.WATER
					&& !entity.isSwimming()
				? 1.0
				: 0.55;
		}
	}
}
