package net.voxla.growlingman.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;

import net.voxla.growlingman.port.bus.ItemFishedEvent;
import net.voxla.growlingman.port.bus.GMBus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/** NeoForge's ItemFishedEvent: the catch of a reeled-in hook, before it spawns */
@Mixin(FishingHook.class)
abstract class FishingHookMixin {
	@Shadow
	public abstract Player getPlayerOwner();

	@ModifyExpressionValue(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"))
	private ObjectArrayList<ItemStack> growling_man$fished(ObjectArrayList<ItemStack> drops) {
		Player player = this.getPlayerOwner();
		if (player != null)
			GMBus.post(new ItemFishedEvent(player, drops));
		return drops;
	}
}
