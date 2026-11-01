package net.voxla.growlingman.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.voxla.growlingman.CustomRenderTypes;
import net.voxla.growlingman.entity.GrowlingManEntity;
import net.voxla.growlingman.entity.model.GrowlingManModel;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class GrowlingManRenderer extends GeoEntityRenderer<GrowlingManEntity> {
	private static final ResourceLocation NOTHING_TEXTURE = ResourceLocation.parse("growling_man:textures/entities/nothing.png");
	private static final Map<String, ResourceLocation> TEXTURE_CACHE = new ConcurrentHashMap<>();
	private static final String[] PRELOAD_TEXTURES = new String[]{"growling_man", "growling_man_face"};

	public GrowlingManRenderer(Context renderManager) {
		super(renderManager, new GrowlingManModel());
		this.addRenderLayer(
			new GeoRenderLayer<GrowlingManEntity>(this) {
				public void render(
					PoseStack poseStack,
					GrowlingManEntity entity,
					BakedGeoModel model,
					RenderType renderType,
					MultiBufferSource bufferSource,
					VertexConsumer buffer,
					float partialTick,
					int packedLight,
					int packedOverlay
				) {
					if (!entity.isInvisible()) {
						float alpha = ((Integer)entity.getEntityData().get(GrowlingManEntity.DATA_Transparency)).intValue() / 360.0F;
						ResourceLocation texture = GrowlingManRenderer.this.getMainTexture(entity);
						VertexConsumer textureBuffer = bufferSource.getBuffer(CustomRenderTypes.growlingEntity(texture));
						this.getRenderer()
							.reRender(
								model,
								poseStack,
								bufferSource,
								entity,
								RenderType.entityTranslucent(texture),
								textureBuffer,
								partialTick,
								packedLight,
								LivingEntityRenderer.getOverlayCoords(entity, 0.0F),
								FastColor.ARGB32.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F)
							);
					}
				}
			}
		);
	}

	private ResourceLocation getMainTexture(GrowlingManEntity entity) {
		String texName = (String)entity.getEntityData().get(GrowlingManEntity.TEXTURE);
		if (texName == null || texName.isEmpty()) {
			texName = "growling_man";
		}

		return TEXTURE_CACHE.computeIfAbsent(texName, name -> ResourceLocation.parse("growling_man:textures/entities/" + name + ".png"));
	}

	public ResourceLocation getTextureLocation(GrowlingManEntity entity) {
		return NOTHING_TEXTURE;
	}

	public RenderType getRenderType(GrowlingManEntity entity, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return entity.isInvisible() ? RenderType.entityTranslucentCull(NOTHING_TEXTURE) : RenderType.entityTranslucentCull(this.getTextureLocation(entity));
	}

	protected float getShadowRadius(GrowlingManEntity entity) {
		return entity.getEntityData().get(GrowlingManEntity.DATA_AI) != 17 && entity.getEntityData().get(GrowlingManEntity.DATA_AI) != 18 ? 0.0F : 0.5F;
	}

	static {
		for (String name : PRELOAD_TEXTURES) {
			TEXTURE_CACHE.put(name, ResourceLocation.parse("growling_man:textures/entities/" + name + ".png"));
		}
	}
}
