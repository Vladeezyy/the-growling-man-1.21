package net.voxla.growlingman.block.entity;

import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ForgeCapabilities;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.items.IItemHandler;
import net.voxla.growlingman.init.GrowlingManModBlockEntities;

public class RegenBlockBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
	private NonNullList<ItemStack> stacks = NonNullList.withSize(0, ItemStack.EMPTY);
	private final LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.values());

	public RegenBlockBlockEntity(BlockPos position, BlockState state) {
		super((BlockEntityType)GrowlingManModBlockEntities.REGEN_BLOCK.get(), position, state);
	}

	public void load(CompoundTag compound) {
		super.load(compound);
		if (!this.tryLoadLootTable(compound)) {
			this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		}

		ContainerHelper.loadAllItems(compound, this.stacks);
	}

	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		if (!this.trySaveLootTable(compound)) {
			ContainerHelper.saveAllItems(compound, this.stacks);
		}
	}

	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public CompoundTag getUpdateTag() {
		return this.saveWithFullMetadata(world.registryAccess());
	}

	public int getContainerSize() {
		return this.stacks.size();
	}

	public boolean isEmpty() {
		for (ItemStack itemstack : this.stacks) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public Component getDefaultName() {
		return Component.literal("regen_block");
	}

	public int getMaxStackSize() {
		return 1;
	}

	public AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return ChestMenu.threeRows(id, inventory);
	}

	public Component getDisplayName() {
		return Component.literal("Regen Block");
	}

	protected NonNullList<ItemStack> getItems() {
		return this.stacks;
	}

	protected void setItems(NonNullList<ItemStack> stacks) {
		this.stacks = stacks;
	}

	public boolean canPlaceItem(int index, ItemStack stack) {
		return true;
	}

	public int[] getSlotsForFace(Direction side) {
		return IntStream.range(0, this.getContainerSize()).toArray();
	}

	public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
		return this.canPlaceItem(index, stack);
	}

	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return true;
	}

	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		return !this.remove && facing != null && capability == ForgeCapabilities.ITEM_HANDLER
			? this.handlers[facing.ordinal()].cast()
			: super.getCapability(capability, facing);
	}

	public void setRemoved() {
		super.setRemoved();

		for (LazyOptional<? extends IItemHandler> handler : this.handlers) {
			handler.invalidate();
		}
	}
}
