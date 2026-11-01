package net.voxla.growlingman.entity.model;

import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.Identifier;

import net.voxla.growlingman.port.GeoTextures;
import net.voxla.growlingman.entity.GrowlingManEntity;

import javax.annotation.Nullable;

public class GrowlingManModel extends GeoModel<GrowlingManEntity> {
	@Override
	public Identifier getAnimationResource(GrowlingManEntity entity) {
		return Identifier.fromNamespaceAndPath("growling_man", "growling_man");
	}

	@Override
	public Identifier getModelResource(GeoRenderState renderState) {
		return Identifier.fromNamespaceAndPath("growling_man", "growling_man");
	}

	@Override
	public Identifier getTextureResource(GeoRenderState renderState) {
		return GeoTextures.get(renderState);
	}

	@Override
	public void addAdditionalStateData(GrowlingManEntity entity, @Nullable Object relatedObject, GeoRenderState renderState) {
		GeoTextures.set(renderState, entity.getTexture());
	}
}
