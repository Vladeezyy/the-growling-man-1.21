package net.voxla.growlingman.fabric.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import net.voxla.growlingman.port.bus.ItemTossEvent;
import net.voxla.growlingman.port.bus.GMBus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** NeoForge's ItemTossEvent (1.21.2+: the server player creates the dropped item) */
@Mixin(ServerPlayer.class)
abstract class ServerPlayerMixin {
	@Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"))
	private void midnightlurker$drop(ItemStack stack, boolean randomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
		if (cir.getReturnValue() != null)
			GMBus.post(new ItemTossEvent(cir.getReturnValue(), (ServerPlayer) (Object) this));
	}
}
