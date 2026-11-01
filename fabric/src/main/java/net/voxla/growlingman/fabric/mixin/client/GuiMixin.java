package net.voxla.growlingman.fabric.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

import net.voxla.growlingman.port.bus.GMBus;
import net.voxla.growlingman.port.bus.RenderGuiEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** NeoForge's RenderGuiEvent.Pre (before every HUD layer) */
@Mixin(Gui.class)
abstract class GuiMixin {
	@Inject(method = "render", at = @At("HEAD"))
	private void growling_man$renderPre(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		GMBus.post(new RenderGuiEvent.Pre(guiGraphics));
	}
}
