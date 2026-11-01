package net.voxla.growlingman.fabric.mixin.client;

import net.minecraft.client.Camera;

import net.voxla.growlingman.port.bus.GMBus;
import net.voxla.growlingman.port.bus.ViewportEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** NeoForge's ViewportEvent.ComputeFov: the field of view the camera computed (26.1), changed by the listeners */
@Mixin(Camera.class)
abstract class CameraMixin {
	@Inject(method = "calculateFov", at = @At("RETURN"), cancellable = true)
	private void growling_man$computeFov(float partialTick, CallbackInfoReturnable<Float> cir) {
		ViewportEvent.ComputeFov event = new ViewportEvent.ComputeFov((Camera) (Object) this, partialTick, cir.getReturnValue(), true);
		GMBus.post(event);
		if (event.getFOV() != cir.getReturnValue())
			cir.setReturnValue((float) event.getFOV());
	}
}
