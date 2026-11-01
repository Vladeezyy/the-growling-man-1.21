package net.voxla.growlingman.init;

import net.minecraft.core.registries.Registries;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.voxla.growlingman.port.platform.Registrar;
import net.minecraft.core.registries.BuiltInRegistries;
import net.voxla.growlingman.port.platform.RegistryEntry;
import net.voxla.growlingman.block.entity.RegenBlockBlockEntity;

public class GrowlingManModBlockEntities {
	public static final Registrar<BlockEntityType<?>> REGISTRY = Registrar.create(Registries.BLOCK_ENTITY_TYPE, "growling_man");
	public static final RegistryEntry<BlockEntityType<?>, BlockEntityType<?>> REGEN_BLOCK = register("regen_block", GrowlingManModBlocks.REGEN_BLOCK, RegenBlockBlockEntity::new);

	private static RegistryEntry<BlockEntityType<?>, BlockEntityType<?>> register(String registryname, RegistryEntry<Block, Block> block, BlockEntitySupplier<?> supplier) {
		return REGISTRY.register(registryname, () -> new BlockEntityType<>(supplier, java.util.Set.of(block.get())));
	}
}
