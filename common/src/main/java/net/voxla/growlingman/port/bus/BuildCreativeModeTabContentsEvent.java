package net.voxla.growlingman.port.bus;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

/** Adds items to the end of an existing creative tab */
public class BuildCreativeModeTabContentsEvent extends Event {
	private final ResourceKey<CreativeModeTab> tabKey;
	private final Consumer<ItemStack> sink;

	public BuildCreativeModeTabContentsEvent(ResourceKey<CreativeModeTab> tabKey, Consumer<ItemStack> sink) {
		this.tabKey = tabKey;
		this.sink = sink;
	}

	public ResourceKey<CreativeModeTab> getTabKey() {
		return this.tabKey;
	}

	public void accept(ItemLike item) {
		this.sink.accept(new ItemStack(item));
	}

	public void accept(ItemStack stack) {
		this.sink.accept(stack);
	}
}
