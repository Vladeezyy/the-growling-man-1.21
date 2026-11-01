package net.voxla.growlingman.fabric.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import net.voxla.growlingman.port.bus.GMBus;
import net.voxla.growlingman.port.bus.RenderGuiEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 26.2: the HUD is its own class. NeoForge's RenderGuiEvent.Pre (before every HUD layer) */
@Mixin(Hud.class)
abstract class HudMixin {
	@Inject(method = "extractRenderState", at = @At("HEAD"))
	private void growling_man$renderPre(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		GMBus.post(new RenderGuiEvent.Pre(guiGraphics));
	}
}
