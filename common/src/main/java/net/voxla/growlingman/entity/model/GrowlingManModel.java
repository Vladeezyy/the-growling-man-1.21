package net.voxla.growlingman.entity.model;

import net.minecraft.resources.ResourceLocation;
import net.voxla.growlingman.entity.GrowlingManEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class GrowlingManModel extends GeoModel<GrowlingManEntity> {
   public ResourceLocation getAnimationResource(GrowlingManEntity entity) {
      return new ResourceLocation("growling_man", "animations/growling_man.animation.json");
   }

   public ResourceLocation getModelResource(GrowlingManEntity entity) {
      return new ResourceLocation("growling_man", "geo/growling_man.geo.json");
   }

   public ResourceLocation getTextureResource(GrowlingManEntity entity) {
      return new ResourceLocation("growling_man", "textures/entities/" + entity.getTexture() + ".png");
   }

   public void setCustomAnimations(GrowlingManEntity animatable, long instanceId, AnimationState animationState) {
      CoreGeoBone head = this.getAnimationProcessor().getBone("head");
      if (head != null) {
         EntityModelData entityData = (EntityModelData)animationState.getData(DataTickets.ENTITY_MODEL_DATA);
         head.setRotX(entityData.headPitch() * (float) (Math.PI / 180.0));
         head.setRotY(entityData.netHeadYaw() * (float) (Math.PI / 180.0));
      }
   }
}
