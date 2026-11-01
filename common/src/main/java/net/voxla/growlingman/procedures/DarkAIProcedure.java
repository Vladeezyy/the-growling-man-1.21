package net.voxla.growlingman.procedures;

import java.util.Comparator;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.voxla.growlingman.entity.GrowlingManEntity;

public class DarkAIProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity != null) {
			if ((entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_ConcealSelf) : 0) == 0
				&& (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_ConcealSelfTimer) : 0) == 0) {
				if (!(entity instanceof GrowlingManEntity _datEntS ? (String)_datEntS.getEntityData().get(GrowlingManEntity.DATA_WalkTarget) : "").equals("")
					&& !(entity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(MobEffects.MOVEMENT_SLOWDOWN))) {
					Vec3 _center = new Vec3(x, y, z);

					for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(160.0), e -> true)
						.stream()
						.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
						.toList()) {
						if (entityiterator.getDisplayName()
								.getString()
								.equals(entity instanceof GrowlingManEntity _datEntSx ? _datEntSx.getEntityData().get(GrowlingManEntity.DATA_WalkTarget) : "")
							&& entity instanceof Mob _entity) {
							_entity.getNavigation().moveTo(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ(), 1.0);
						}
					}
				}

				if (!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 50.0, 50.0, 50.0), e -> true).isEmpty()
					&& entity instanceof LivingEntity _entity
					&& !_entity.level().isClientSide()) {
					_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 3, 200, false, false));
				}
			}

			if ((entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_AIIdleTimer) : 0) < 3600
				&& entity instanceof GrowlingManEntity _datEntSetI) {
				_datEntSetI.getEntityData()
					.set(
						GrowlingManEntity.DATA_AIIdleTimer,
						(entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_AIIdleTimer) : 0) + 1
					);
			}

			if ((entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_AIIdleTimer) : 0) >= 3600) {
				if (entity instanceof GrowlingManEntity _datEntSetI) {
					_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_AIIdleTimer, 0);
				}

				if ((entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer) : 0) == 0
					&& entity instanceof GrowlingManEntity _datEntSetI) {
					_datEntSetI.getEntityData().set(GrowlingManEntity.DATA_DeManifestTimer, 1);
				}
			}

			if (!(entity instanceof GrowlingManEntity animatablex ? animatablex.getTexture() : "null").equals("growling_man_face")
				&& entity instanceof GrowlingManEntity animatable) {
				animatable.setTexture("growling_man_face");
			}

			if (world instanceof ServerLevel _level) {
				_level.sendParticles(
					ParticleTypes.SQUID_INK, x - entity.getLookAngle().x * 1.1, y + 2.0, z - entity.getLookAngle().z * 1.1, 15, 0.7, 1.0, 0.7, 1.0E-4
				);
			}

			if (world instanceof ServerLevel _level) {
				_level.sendParticles(ParticleTypes.SQUID_INK, x, y + 2.0, z, 15, 0.4, 0.4, 0.4, 1.0E-4);
			}
		}
	}
}
