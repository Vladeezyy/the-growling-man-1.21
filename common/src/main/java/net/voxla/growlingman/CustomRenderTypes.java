package net.voxla.growlingman;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/** The see-through render type of the Growling Man (1.0.1's settings); extends RenderType for its protected shards */
public class CustomRenderTypes extends RenderType {
	// 1.0.1 created a new render type every frame; one per texture is enough
	private static final Map<ResourceLocation, RenderType> GROWLING_ENTITY = new HashMap<>();

	private CustomRenderTypes(String name, com.mojang.blaze3d.vertex.VertexFormat format, Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
		super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
	}

	public static RenderType growlingEntity(ResourceLocation texture) {
		return GROWLING_ENTITY.computeIfAbsent(texture, t -> RenderType.create(
				"growling_entity",
				DefaultVertexFormat.NEW_ENTITY,
				Mode.QUADS,
				256,
				true,
				true,
				CompositeState.builder()
						.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
						.setTextureState(new TextureStateShard(t, false, false))
						.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
						.setCullState(NO_CULL)
						.setDepthTestState(LEQUAL_DEPTH_TEST)
						.setLightmapState(LIGHTMAP)
						.setOverlayState(OVERLAY)
						.createCompositeState(true)));
	}
}
