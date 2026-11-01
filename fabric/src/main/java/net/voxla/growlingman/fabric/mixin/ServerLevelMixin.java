package net.voxla.growlingman.fabric.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import net.voxla.growlingman.port.GMEntities;
import net.voxla.growlingman.port.bus.EntityTickEvent;
import net.voxla.growlingman.port.bus.GMBus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** NeoForge's EntityTickEvent.Pre, for this mod's entities only (its only listener handles those) */
@Mixin(ServerLevel.class)
abstract class ServerLevelMixin {
	@Inject(method = "tickNonPassenger", at = @At("HEAD"))
	private void growling_man$entityTick(Entity entity, CallbackInfo ci) {
		if (GMEntities.ours(entity))
			GMBus.post(new EntityTickEvent.Pre(entity));
	}
}
