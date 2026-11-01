package net.voxla.growlingman.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.voxla.growlingman.entity.GrowlingManEntity;

public class ManifestEventProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity != null) {
			if (!world.isClientSide()
				&& (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_ManifestTimer) : 0) < 38) {
				if ((entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_Transparency) : 0) < 360
					&& entity instanceof GrowlingManEntity _datEntSetI) {
					_datEntSetI.getEntityData()
						.set(
							GrowlingManEntity.DATA_Transparency,
							(entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_Transparency) : 0) + 10
						);
				}

				if ((entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_ManifestTimer) : 0) == 1
					&& !world.isClientSide()
					&& world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(
							null,
							BlockPos.containing(x, y + 1.5, z),
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.growling_man.manifest")),
							SoundSource.HOSTILE,
							2.0F,
							1.0F
						);
					} else {
						_level.playLocalSound(
							x,
							y + 1.5,
							z,
							(SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.growling_man.manifest")),
							SoundSource.HOSTILE,
							2.0F,
							1.0F,
							false
						);
					}
				}

				if ((entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_ManifestTimer) : 0) < 360) {
					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
						_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 3, 200, false, false));
					}

					if (world instanceof ServerLevel _levelx) {
						_levelx.sendParticles(ParticleTypes.SQUID_INK, x, y + 2.0, z, 10, 0.7, 1.0, 0.7, 0.01);
					}
				}

				if (entity instanceof GrowlingManEntity _datEntSetI) {
					_datEntSetI.getEntityData()
						.set(
							GrowlingManEntity.DATA_ManifestTimer,
							(entity instanceof GrowlingManEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(GrowlingManEntity.DATA_ManifestTimer) : 0) + 1
						);
				}
			}
		}
	}
}
