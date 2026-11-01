package net.voxla.growlingman.init;

import net.minecraft.core.registries.Registries;

import net.minecraft.world.level.block.Block;
import net.voxla.growlingman.port.platform.Registrar;
import net.minecraft.core.registries.BuiltInRegistries;
import net.voxla.growlingman.port.platform.RegistryEntry;
import net.voxla.growlingman.block.RegenBlockBlock;

public class GrowlingManModBlocks {
	public static final Registrar<Block> REGISTRY = Registrar.create(Registries.BLOCK, "growling_man");
	public static final RegistryEntry<Block, Block> REGEN_BLOCK = REGISTRY.register("regen_block", () -> new RegenBlockBlock());
}
