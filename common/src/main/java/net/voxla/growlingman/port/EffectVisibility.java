package net.voxla.growlingman.port;

import net.minecraft.world.effect.MobEffectInstance;

/**
 * NeoForge's {@code IClientMobEffectExtensions} visibility for the mod's hidden effects: NeoForge registers them as
 * client extensions, Fabric checks them in the HUD and inventory effect lists (mixins).
 */
public interface EffectVisibility {
	default boolean isVisibleInInventory(MobEffectInstance effect) {
		return true;
	}

	default boolean isVisibleInGui(MobEffectInstance effect) {
		return true;
	}
}
