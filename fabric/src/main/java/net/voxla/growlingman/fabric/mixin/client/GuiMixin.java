package net.voxla.growlingman.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffectInstance;

import net.voxla.growlingman.port.EffectVisibility;
import net.voxla.growlingman.port.bus.GMBus;
import net.voxla.growlingman.port.bus.RenderGuiEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** NeoForge's RenderGuiEvent.Pre (before every HUD layer) and isVisibleInGui of the mod's effects */
@Mixin(Gui.class)
abstract class GuiMixin {
	@Inject(method = "render", at = @At("HEAD"))
	private void growling_man$renderPre(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		GMBus.post(new RenderGuiEvent.Pre(guiGraphics));
	}

	@ModifyExpressionValue(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;showIcon()Z"))
	private boolean growling_man$visibleInGui(boolean showIcon, @Local MobEffectInstance instance) {
		return showIcon && !(instance.getEffect().value() instanceof EffectVisibility visibility && !visibility.isVisibleInGui(instance));
	}
}
