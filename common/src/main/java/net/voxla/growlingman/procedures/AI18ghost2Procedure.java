package net.voxla.growlingman.procedures;

import java.util.Comparator;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.voxla.growlingman.entity.GrowlingManEntity;
import net.voxla.growlingman.network.GrowlingManModVariables;

public class AI18ghost2Procedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         boolean CanBeSeen = false;
         if (world.isClientSide()) {
            if (!entity.isInvisible()) {
               entity.setInvisible(true);
            }

            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(25.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiterator instanceof Player
                  && ((GrowlingManModVariables.PlayerVariables)entityiterator.getCapability(GrowlingManModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                           .orElse(new GrowlingManModVariables.PlayerVariables()))
                        .gm_inv_ap
                     > 0.0
                  && entityiterator.getDisplayName()
                     .getString()
                     .equals(entity instanceof GrowlingManEntity _datEntS ? _datEntS.getEntityData().get(GrowlingManEntity.DATA_GhostTarget) : "")) {
                  if (entity.isInvisible()) {
                     entity.setInvisible(false);
                  }
                  break;
               }
            }
         }

         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(15.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiteratorx instanceof Player
               && entityiteratorx.getDisplayName()
                  .getString()
                  .equals(entity instanceof GrowlingManEntity _datEntSx ? _datEntSx.getEntityData().get(GrowlingManEntity.DATA_GhostTarget) : "")) {
               entity.lookAt(
                  Anchor.EYES,
                  new Vec3(
                     entityiteratorx.getX(),
                     entityiteratorx.getY() + entityiteratorx.getBbHeight() - entityiteratorx.getBbHeight() / 4.0F,
                     entityiteratorx.getZ()
                  )
               );
               if (((GrowlingManModVariables.PlayerVariables)entityiteratorx.getCapability(GrowlingManModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                           .orElse(new GrowlingManModVariables.PlayerVariables()))
                        .gm_inv_ap
                     < 10.0
                  && !world.isClientSide()
                  && (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_Transparency) : 0) > 0
                  && entity instanceof GrowlingManEntity _datEntSetI) {
                  _datEntSetI.getEntityData()
                     .set(
                        GrowlingManEntity.DATA_Transparency,
                        (entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_Transparency) : 0) - 40
                     );
               }

               if ((entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_Transparency) : 0) <= 0
                  && ((GrowlingManModVariables.PlayerVariables)entityiteratorx.getCapability(GrowlingManModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                           .orElse(new GrowlingManModVariables.PlayerVariables()))
                        .gm_inv_ap
                     == 0.0
                  && !entity.level().isClientSide()) {
                  entity.discard();
               }
               break;
            }
         }

         if (world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 30.0, 30.0, 30.0), e -> true).isEmpty() && !entity.level().isClientSide()) {
            entity.discard();
         }
      }
   }
}
