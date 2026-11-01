package net.voxla.growlingman.fabric.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import net.voxla.growlingman.port.bus.LivingEntityUseItemEvent;
import net.voxla.growlingman.port.bus.GMBus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** NeoForge's LivingEntityUseItemEvent.Finish, with a copy of the stack before it was used up */
@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {
	@Shadow
	protected ItemStack useItem;

	@Inject(method = "completeUsingItem", at = @At("HEAD"))
	private void growling_man$finishUsing(CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (!this.useItem.isEmpty() && self.isUsingItem())
			GMBus.post(new LivingEntityUseItemEvent.Finish(self, this.useItem.copy()));
	}
}
