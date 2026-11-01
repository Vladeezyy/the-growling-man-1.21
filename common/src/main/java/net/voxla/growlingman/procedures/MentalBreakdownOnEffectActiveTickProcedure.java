package net.voxla.growlingman.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.BuiltInRegistries;
import net.voxla.growlingman.init.GrowlingManModMobEffects;

public class MentalBreakdownOnEffectActiveTickProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity != null) {
			if ((
					entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder())
						? _livEnt.getEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder()).getAmplifier()
						: 0
				)
				== 0) {
				entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x() / 1.5, entity.getDeltaMovement().y(), entity.getDeltaMovement().z() / 1.5));
			} else if ((
					entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder())
						? _livEnt.getEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder()).getAmplifier()
						: 0
				)
				== 1) {
				entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x() / 2.5, entity.getDeltaMovement().y(), entity.getDeltaMovement().z() / 2.5));
			} else if ((
					entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder())
						? _livEnt.getEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder()).getAmplifier()
						: 0
				)
				== 2) {
				entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x() / 3.5, entity.getDeltaMovement().y(), entity.getDeltaMovement().z() / 3.5));
			} else if ((
					entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder())
						? _livEnt.getEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder()).getAmplifier()
						: 0
				)
				== 3) {
				entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x() / 4.5, entity.getDeltaMovement().y(), entity.getDeltaMovement().z() / 4.5));
			} else if ((
					entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder())
						? _livEnt.getEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder()).getAmplifier()
						: 0
				)
				== 4) {
				entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x() / 5.5, entity.getDeltaMovement().y(), entity.getDeltaMovement().z() / 5.5));
			} else if ((
					entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder())
						? _livEnt.getEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder()).getAmplifier()
						: 0
				)
				== 5) {
				entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x() / 6.5, entity.getDeltaMovement().y(), entity.getDeltaMovement().z() / 6.5));
			} else if ((
					entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder())
						? _livEnt.getEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder()).getAmplifier()
						: 0
				)
				>= 6) {
				entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x() / 7.5, entity.getDeltaMovement().y(), entity.getDeltaMovement().z() / 7.5));
			}

			if (world instanceof ServerLevel _level) {
				_level.sendParticles(ParticleTypes.ASH, x, y + 1.0, z, 2, 0.3, 0.4, 0.3, 0.1);
			}

			if (world.isClientSide()) {
				if (world.getLevelData().getGameTime() % 8L == 1L
					&& (
							entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder())
								? _livEnt.getEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder()).getDuration()
								: 0
						)
						> 100) {
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(
								null,
								BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.gm_heartbeat_fast")),
								SoundSource.PLAYERS,
								0.9F,
								1.0F
							);
						} else {
							_level.playLocalSound(
								x,
								y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
								z,
								(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.gm_heartbeat_fast")),
								SoundSource.PLAYERS,
								0.9F,
								1.0F,
								false
							);
						}
					}
				} else if (world.getLevelData().getGameTime() % 20L == 1L
					&& (
							entity instanceof LivingEntity _livEntx && _livEntx.hasEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder())
								? _livEntx.getEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder()).getDuration()
								: 0
						)
						<= 100
					&& world instanceof Level _levelx) {
					if (!_levelx.isClientSide()) {
						_levelx.playSound(
							null,
							BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.gm_heartbeat_slow")),
							SoundSource.PLAYERS,
							0.4F,
							1.0F
						);
					} else {
						_levelx.playLocalSound(
							x,
							y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
							z,
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.gm_heartbeat_slow")),
							SoundSource.PLAYERS,
							0.4F,
							1.0F,
							false
						);
					}
				}

				if (world.getLevelData().getGameTime() % 21L == 1L
					&& (
							entity instanceof LivingEntity _livEntxx && _livEntxx.hasEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder())
								? _livEntxx.getEffect(GrowlingManModMobEffects.MENTAL_BREAKDOWN.holder()).getDuration()
								: 0
						)
						> 100
					&& world instanceof Level _levelxx) {
					if (!_levelxx.isClientSide()) {
						_levelxx.playSound(
							null,
							BlockPos.containing(x, y + entity.getBbHeight() - entity.getBbHeight() / 4.0F, z),
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.gm_mental_breakdown")),
							SoundSource.PLAYERS,
							1.0F,
							1.0F
						);
					} else {
						_levelxx.playLocalSound(
							x,
							y + entity.getBbHeight() - entity.getBbHeight() / 4.0F,
							z,
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.player.gm_mental_breakdown")),
							SoundSource.PLAYERS,
							1.0F,
							1.0F,
							false
						);
					}
				}
			}
		}
	}
}
