package net.voxla.growlingman.fabric.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.item.ItemStack;

import net.voxla.growlingman.port.bus.GMBus;
import net.voxla.growlingman.port.bus.PlayerEvent;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** NeoForge's PlayerEvent.ItemSmeltedEvent */
@Mixin(FurnaceResultSlot.class)
abstract class FurnaceResultSlotMixin {
	@Shadow
	@Final
	private Player player;

	@Inject(method = "checkTakeAchievements", at = @At("HEAD"))
	private void growling_man$smelted(ItemStack stack, CallbackInfo ci) {
		GMBus.post(new PlayerEvent.ItemSmeltedEvent(this.player, stack));
	}
}
