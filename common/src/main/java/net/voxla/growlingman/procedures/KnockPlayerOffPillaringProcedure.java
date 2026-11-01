package net.voxla.growlingman.procedures;

import java.util.Comparator;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.voxla.growlingman.entity.GrowlingManEntity;

public class KnockPlayerOffPillaringProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         double fromZ = 0.0;
         double magnitude = 0.0;
         double fromX = 0.0;
         double theta = 0.0;
         double OffHandBlockCount = 0.0;
         double MainHandBlockCount = 0.0;
         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(12.5), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiterator instanceof Player
               && (entityiterator instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof BlockItem
               && entityiterator.getY() <= entity.getY() + 1.5
               && entityiterator.getDeltaMovement().y() == 0.0) {
               MainHandBlockCount = (entityiterator instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getCount();
            }

            if (entityiterator instanceof Player
               && (entityiterator instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() instanceof BlockItem
               && entityiterator.getY() <= entity.getY() + 1.5
               && entityiterator.getDeltaMovement().y() == 0.0) {
               OffHandBlockCount = (entityiterator instanceof LivingEntity _livEntx ? _livEntx.getOffhandItem() : ItemStack.EMPTY).getCount();
            }

            if ((
                  entityiterator instanceof Player
                        && (entityiterator instanceof LivingEntity _livEntxxx ? _livEntxxx.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof BlockItem
                        && entityiterator.getY() > entity.getY() + 1.5
                        && entityiterator.getDeltaMovement().y() > 0.0
                        && (entityiterator instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).getCount() != MainHandBlockCount
                     || entityiterator instanceof Player
                        && (entityiterator instanceof LivingEntity _livEntx ? _livEntx.getOffhandItem() : ItemStack.EMPTY).getItem() instanceof BlockItem
                        && entityiterator.getY() > entity.getY() + 1.5
                        && entityiterator.getDeltaMovement().y() > 0.0
                        && (entityiterator instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getCount() != OffHandBlockCount
               )
               && (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_SmackPlayerOffPillar) : 0) == 0
               && entity instanceof GrowlingManEntity _datEntSetI) {
               _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_SmackPlayerOffPillar, 40);
            }

            if ((entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_SmackPlayerOffPillar) : 0) > 35
               && entityiterator instanceof Player) {
               magnitude = 1.0;
               theta = Math.atan(Math.abs(entity.getX() - entityiterator.getX()) / Math.abs(entity.getZ() - entityiterator.getZ()));
               fromX = magnitude * Math.sin(theta);
               fromZ = magnitude * Math.cos(theta);
               if (entityiterator.getX() < entity.getX()) {
                  fromX *= -1.0;
               }

               if (entityiterator.getZ() < entity.getZ()) {
                  fromZ *= -1.0;
               }

               entityiterator.setDeltaMovement(new Vec3(entity.getDeltaMovement().x() + fromX, 0.3, entity.getDeltaMovement().z() + fromZ));
               entityiterator.hurt(
                  new DamageSource(
                     world.registryAccess()
                        .registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("growling_man:gm_damage")))
                  ),
                  5.0F
               );
            }
         }

         if ((entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_SmackPlayerOffPillar) : 0) > 0
            && entity instanceof GrowlingManEntity _datEntSetI) {
            _datEntSetI.getEntityData()
               .set(
                  GrowlingManEntity.DATA_SmackPlayerOffPillar,
                  (entity instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_SmackPlayerOffPillar) : 0)
                     - 1
               );
         }
      }
   }
}
