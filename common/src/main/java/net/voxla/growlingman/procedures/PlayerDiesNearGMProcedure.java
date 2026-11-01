package net.voxla.growlingman.procedures;

import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.voxla.growlingman.entity.GrowlingManEntity;

@EventBusSubscriber
public class PlayerDiesNearGMProcedure {
   @SubscribeEvent
   public static void onEntityDeath(LivingDeathEvent event) {
      if (event != null && event.getEntity() != null) {
         execute(
            event,
            event.getEntity().level(),
            event.getEntity().getX(),
            event.getEntity().getY(),
            event.getEntity().getZ(),
            event.getSource(),
            event.getEntity()
         );
      }
   }

   public static void execute(LevelAccessor world, double x, double y, double z, DamageSource damagesource, Entity entity) {
      execute(null, world, x, y, z, damagesource, entity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, DamageSource damagesource, Entity entity) {
      if (damagesource != null && entity != null) {
         if (!world.isClientSide()
            && (entity instanceof Player || entity instanceof ServerPlayer)
            && (
               !world.getEntitiesOfClass(GrowlingManEntity.class, AABB.ofSize(new Vec3(x, y, z), 15.0, 15.0, 15.0), e -> true).isEmpty()
                  || damagesource.is(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("growling_man:gm_damage")))
            )
            && world.getEntitiesOfClass(GrowlingManEntity.class, AABB.ofSize(new Vec3(x, y, z), 15.0, 15.0, 15.0), e -> true).stream().sorted((new Object() {
               Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                  return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
               }
            }).compareDistOf(x, y, z)).findFirst().orElse(null) != null) {
            Entity var11 = world.getEntitiesOfClass(GrowlingManEntity.class, AABB.ofSize(new Vec3(x, y, z), 15.0, 15.0, 15.0), e -> true)
               .stream()
               .sorted((new Object() {
                  Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                     return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                  }
               }).compareDistOf(x, y, z))
               .findFirst()
               .orElse(null);
            if (var11 instanceof GrowlingManEntity _datEntSetI) {
               _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_DeManifestTimer, 1);
            }
         }
      }
   }
}
