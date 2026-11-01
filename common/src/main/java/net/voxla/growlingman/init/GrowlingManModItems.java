package net.voxla.growlingman.init;

import net.minecraft.core.registries.Registries;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.voxla.growlingman.port.platform.Registrar;
import net.minecraft.core.registries.BuiltInRegistries;
import net.voxla.growlingman.port.platform.RegistryEntry;

public class GrowlingManModItems {
	public static final Registrar<Item> REGISTRY = Registrar.create(Registries.ITEM, "growling_man");
	public static final RegistryEntry<Item, Item> REGEN_BLOCK = block(GrowlingManModBlocks.REGEN_BLOCK);

	private static RegistryEntry<Item, Item> block(RegistryEntry<Block, Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem((Block)block.get(), new Properties()));
	}
}
