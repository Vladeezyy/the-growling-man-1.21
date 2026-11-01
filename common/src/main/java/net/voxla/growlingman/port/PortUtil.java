package net.voxla.growlingman.port;

import net.voxla.growlingman.port.platform.Platform;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Holder;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/** Helpers for 1.20 calls that have no direct 1.21 equivalent. */
public final class PortUtil {
	/** The client level's registries (set by the client entry point; null on a dedicated server) */
	public static Supplier<RegistryAccess> clientRegistries = () -> null;

	/** The client player (set by the client entry point; null on a dedicated server) */
	public static Supplier<Entity> clientPlayer = () -> null;

	private PortUtil() {
	}

	/** NeoForge's {@code net.voxla.growlingman.port.PortUtil.data(entity)}: the entity's own saved tag, on both loaders */
	public static CompoundTag data(Entity entity) {
		return Platform.INSTANCE.persistentData(entity);
	}

	/** NeoForge's {@code net.voxla.growlingman.port.PortUtil.data(blockEntity)} */
	public static CompoundTag data(net.minecraft.world.level.block.entity.BlockEntity blockEntity) {
		return Platform.INSTANCE.persistentData(blockEntity);
	}

	/** 1.20 {@code stack.hurtAndBreak(amount, random, player, onBreak)} / {@code if (stack.hurt(...)) shrink}: 1.21 needs a ServerLevel. */
	public static void hurtAndBreak(ItemStack stack, int amount, @Nullable LivingEntity entity) {
		if (stack.isEmpty() || !stack.isDamageableItem())
			return;
		ServerLevel level = null;
		if (entity != null && entity.level() instanceof ServerLevel serverLevel)
			level = serverLevel;
		if (level == null) {
			MinecraftServer server = Platform.INSTANCE.currentServer();
			if (server == null)
				return;
			level = server.overworld();
		}
		stack.hurtAndBreak(amount, level, entity instanceof net.minecraft.server.level.ServerPlayer player ? player : null, item -> {
		});
	}

	/** 1.20 {@code if (stack.hurt(amount, random, player)) ...}: damages the stack and tells whether it broke (1.21 already shrank it). */
	public static boolean hurtAndBreakReports(ItemStack stack, int amount, @Nullable LivingEntity entity) {
		int before = stack.getCount();
		hurtAndBreak(stack, amount, entity);
		return stack.getCount() < before;
	}

	/** 1.20 {@code AbstractArrow.setKnockback}: 1.21 takes knockback from the weapon's enchantments only. */
	public static void setKnockback(AbstractArrow arrow, int knockback) {
		((ArrowKnockback) arrow).deepvoid$setKnockback(knockback);
	}

	/** 1.20 {@code LivingEntity#getExperienceReward()}: 1.21 needs the server level and the killer. */
	public static int experienceReward(LivingEntity entity) {
		return entity.level() instanceof ServerLevel serverLevel ? entity.getExperienceReward(serverLevel, entity.getLastAttacker()) : 0;
	}

	/** 1.21 enchantments live in a datapack registry: look them up on whichever side we are. */
	@Nullable
	public static Holder<Enchantment> enchantment(ResourceKey<Enchantment> key) {
		MinecraftServer server = Platform.INSTANCE.currentServer();
		RegistryAccess registries = server != null ? server.registryAccess() : clientRegistries.get();
		if (registries == null)
			return null;
		HolderLookup.RegistryLookup<Enchantment> lookup = registries.lookup(Registries.ENCHANTMENT).orElse(null);
		return lookup == null ? null : lookup.get(key).orElse(null);
	}

	public static int enchantmentLevel(ItemStack stack, ResourceKey<Enchantment> key) {
		Holder<Enchantment> holder = enchantment(key);
		return holder == null ? 0 : EnchantmentHelper.getItemEnchantmentLevel(holder, stack);
	}

	public static void enchant(ItemStack stack, ResourceKey<Enchantment> key, int level) {
		Holder<Enchantment> holder = enchantment(key);
		if (holder != null)
			stack.enchant(holder, level);
	}

	/** 1.20 MobEffect#removeAttributeModifiers hook (effect expired or removed); called from {@link PortEvents}. */
	public interface EffectRemovedListener {
		void onEffectRemoved(LivingEntity entity, int amplifier);
	}

	public interface ArrowKnockback {
		void deepvoid$setKnockback(int knockback);

		int deepvoid$getKnockback();
	}
}
