package net.voxla.growlingman.procedures;

import java.util.Comparator;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.voxla.growlingman.entity.GrowlingManEntity;

public class PlayerCanSeeGMProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         double raytrace_distance = 0.0;
         boolean entity_found = false;
         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(160.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiterator instanceof Player
               && !(new Object() {
                     public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                           return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else {
                           return _ent.level().isClientSide() && _ent instanceof Player _player
                              ? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                 && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE
                              : false;
                        }
                     }
                  })
                  .checkGamemode(entityiterator)
               && !(new Object() {
                     public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                           return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
                        } else {
                           return _ent.level().isClientSide() && _ent instanceof Player _player
                              ? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                 && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR
                              : false;
                        }
                     }
                  })
                  .checkGamemode(entityiterator)) {
               raytrace_distance = 0.0;

               for (int index0 = 0; index0 < 160; index0++) {
                  if (!world.getEntitiesOfClass(
                        GrowlingManEntity.class,
                        AABB.ofSize(
                           new Vec3(
                              entityiterator.level()
                                 .clip(
                                    new ClipContext(
                                       entityiterator.getEyePosition(1.0F),
                                       entityiterator.getEyePosition(1.0F).add(entityiterator.getViewVector(1.0F).scale(raytrace_distance)),
                                       Block.COLLIDER,
                                       Fluid.NONE,
                                       entityiterator
                                    )
                                 )
                                 .getBlockPos()
                                 .getX(),
                              entityiterator.level()
                                 .clip(
                                    new ClipContext(
                                       entityiterator.getEyePosition(1.0F),
                                       entityiterator.getEyePosition(1.0F).add(entityiterator.getViewVector(1.0F).scale(raytrace_distance)),
                                       Block.COLLIDER,
                                       Fluid.NONE,
                                       entityiterator
                                    )
                                 )
                                 .getBlockPos()
                                 .getY(),
                              entityiterator.level()
                                 .clip(
                                    new ClipContext(
                                       entityiterator.getEyePosition(1.0F),
                                       entityiterator.getEyePosition(1.0F).add(entityiterator.getViewVector(1.0F).scale(raytrace_distance)),
                                       Block.COLLIDER,
                                       Fluid.NONE,
                                       entityiterator
                                    )
                                 )
                                 .getBlockPos()
                                 .getZ()
                           ),
                           1.0,
                           1.0,
                           1.0
                        ),
                        e -> true
                     )
                     .isEmpty()) {
                     entity_found = true;
                  } else {
                     entity_found = false;
                     raytrace_distance++;
                  }
               }

               if (!entity_found && world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 10.0, 10.0, 10.0), e -> true).isEmpty()) {
                  if (entity instanceof GrowlingManEntity _datEntL10
                     && (Boolean)_datEntL10.getEntityData().get(GrowlingManEntity.DATA_IsBeingLookedAt)
                     && entity instanceof GrowlingManEntity _datEntSetL) {
                     _datEntSetL.getEntityData().set(GrowlingManEntity.DATA_IsBeingLookedAt, false);
                  }
               } else if (!(entity instanceof GrowlingManEntity _datEntL8 && (Boolean)_datEntL8.getEntityData().get(GrowlingManEntity.DATA_IsBeingLookedAt))
                  && entity instanceof GrowlingManEntity _datEntSetL) {
                  _datEntSetL.getEntityData().set(GrowlingManEntity.DATA_IsBeingLookedAt, true);
               }
            }
         }

         if (!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 150.0, 150.0, 150.0), e -> true).isEmpty() && entity instanceof GrowlingManEntity) {
            _center = new Vec3(x, y, z);

            for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(75.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiteratorx instanceof Player && entity != null && entityiteratorx != null) {
                  Vec3 lookVec = entityiteratorx.getLookAngle().normalize();
                  Vec3 toTarget = entity.position().subtract(entityiteratorx.position()).normalize();
                  double dot = lookVec.dot(toTarget);
                  boolean inFOV = dot > 0.2;
                  boolean canSee = entityiteratorx instanceof LivingEntity livingEntity && livingEntity.hasLineOfSight(entity);
                  if (inFOV && canSee) {
                     if (!(entity instanceof GrowlingManEntity _datEntL16 && (Boolean)_datEntL16.getEntityData().get(GrowlingManEntity.DATA_IsBeingSeen))
                        && entity instanceof GrowlingManEntity _datEntSetL) {
                        _datEntSetL.getEntityData().set(GrowlingManEntity.DATA_IsBeingSeen, true);
                     }
                     break;
                  }

                  if (entity instanceof GrowlingManEntity _datEntL18
                     && (Boolean)_datEntL18.getEntityData().get(GrowlingManEntity.DATA_IsBeingSeen)
                     && entity instanceof GrowlingManEntity _datEntSetL) {
                     _datEntSetL.getEntityData().set(GrowlingManEntity.DATA_IsBeingSeen, false);
                  }
               }
            }
         }

         if (world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 200.0, 200.0, 200.0), e -> true).isEmpty()
            && entity instanceof GrowlingManEntity _datEntL22
            && (Boolean)_datEntL22.getEntityData().get(GrowlingManEntity.DATA_IsBeingLookedAt)
            && entity instanceof GrowlingManEntity _datEntSetL) {
            _datEntSetL.getEntityData().set(GrowlingManEntity.DATA_IsBeingLookedAt, false);
         }

         if (world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 150.0, 150.0, 150.0), e -> true).isEmpty()
            && entity instanceof GrowlingManEntity _datEntL25
            && (Boolean)_datEntL25.getEntityData().get(GrowlingManEntity.DATA_IsBeingSeen)
            && entity instanceof GrowlingManEntity _datEntSetL) {
            _datEntSetL.getEntityData().set(GrowlingManEntity.DATA_IsBeingSeen, false);
         }
      }
   }
}
