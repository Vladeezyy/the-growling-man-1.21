package net.voxla.growlingman.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;

import net.voxla.growlingman.port.EffectVisibility;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

/** isVisibleInInventory of the mod's effects: hidden ones are left out of the inventory's effect list */
@Mixin(EffectRenderingInventoryScreen.class)
abstract class EffectRenderingInventoryScreenMixin {
	@ModifyExpressionValue(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getActiveEffects()Ljava/util/Collection;"))
	private Collection<MobEffectInstance> growling_man$visibleInInventory(Collection<MobEffectInstance> effects) {
		if (effects.stream().noneMatch(e -> e.getEffect().value() instanceof EffectVisibility))
			return effects;
		return effects.stream().filter(e -> !(e.getEffect().value() instanceof EffectVisibility visibility) || visibility.isVisibleInInventory(e)).toList();
	}
}
