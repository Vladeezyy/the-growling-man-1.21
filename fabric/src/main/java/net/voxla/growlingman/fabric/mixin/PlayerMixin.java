package net.voxla.growlingman.fabric.mixin;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.voxla.growlingman.port.bus.ItemTossEvent;
import net.voxla.growlingman.port.bus.GMBus;
import net.voxla.growlingman.port.bus.PlayerTickEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** NeoForge's PlayerTickEvent.Post (both sides) and ItemTossEvent */
@Mixin(Player.class)
abstract class PlayerMixin {
	@Inject(method = "tick", at = @At("TAIL"))
	private void growling_man$tick(CallbackInfo ci) {
		GMBus.post(new PlayerTickEvent.Post((Player) (Object) this));
	}

	@Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"))
	private void growling_man$drop(ItemStack stack, boolean randomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
		if (cir.getReturnValue() != null)
			GMBus.post(new ItemTossEvent(cir.getReturnValue(), (Player) (Object) this));
	}
}
