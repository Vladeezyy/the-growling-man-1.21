package net.voxla.growlingman.fabric.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

import net.voxla.growlingman.port.bus.GMBus;
import net.voxla.growlingman.port.bus.ViewportEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** NeoForge's ViewportEvent.ComputeFov: the field of view GameRenderer computed, changed by the listeners */
@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
	@Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
	private void growling_man$computeFov(Camera camera, float partialTick, boolean useFovSetting, CallbackInfoReturnable<Float> cir) {
		ViewportEvent.ComputeFov event = new ViewportEvent.ComputeFov(camera, partialTick, cir.getReturnValue(), useFovSetting);
		GMBus.post(event);
		if (event.getFOV() != cir.getReturnValue())
			cir.setReturnValue((float) event.getFOV());
	}
}
