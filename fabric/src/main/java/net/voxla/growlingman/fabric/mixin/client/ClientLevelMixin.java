package net.voxla.growlingman.fabric.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

import net.voxla.growlingman.port.GMEntities;
import net.voxla.growlingman.port.bus.EntityTickEvent;
import net.voxla.growlingman.port.bus.GMBus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** NeoForge's EntityTickEvent.Pre on the client, for this mod's entities only */
@Mixin(ClientLevel.class)
abstract class ClientLevelMixin {
	@Inject(method = "tickNonPassenger", at = @At("HEAD"))
	private void growling_man$entityTick(Entity entity, CallbackInfo ci) {
		if (GMEntities.ours(entity))
			GMBus.post(new EntityTickEvent.Pre(entity));
	}
}
