package net.voxla.growlingman.fabric.mixin;

import net.minecraft.world.entity.player.Player;

import net.voxla.growlingman.port.bus.GMBus;
import net.voxla.growlingman.port.bus.PlayerTickEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** NeoForge's PlayerTickEvent.Post (both sides) */
@Mixin(Player.class)
abstract class PlayerMixin {
	@Inject(method = "tick", at = @At("TAIL"))
	private void growling_man$tick(CallbackInfo ci) {
		GMBus.post(new PlayerTickEvent.Post((Player) (Object) this));
	}
}
