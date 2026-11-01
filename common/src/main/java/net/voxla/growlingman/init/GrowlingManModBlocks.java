package net.voxla.growlingman.init;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.voxla.growlingman.block.RegenBlockBlock;

public class GrowlingManModBlocks {
   public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, "growling_man");
   public static final RegistryObject<Block> REGEN_BLOCK = REGISTRY.register("regen_block", () -> new RegenBlockBlock());
}
