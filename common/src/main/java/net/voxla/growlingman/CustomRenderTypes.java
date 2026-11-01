package net.voxla.growlingman;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.CullStateShard;
import net.minecraft.client.renderer.RenderStateShard.DepthTestStateShard;
import net.minecraft.client.renderer.RenderStateShard.LightmapStateShard;
import net.minecraft.client.renderer.RenderStateShard.OverlayStateShard;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderStateShard.TransparencyStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;

public class CustomRenderTypes {
   private static final ShaderStateShard ENTITY_TRANSLUCENT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityTranslucentShader);
   private static final TransparencyStateShard TRANSLUCENT_TRANSPARENCY = new TransparencyStateShard("translucent_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
   }, () -> RenderSystem.disableBlend());
   private static final CullStateShard NO_CULL = new CullStateShard(false);
   private static final DepthTestStateShard LEQUAL_DEPTH_TEST = new DepthTestStateShard("lequal_depth_test", 515);
   private static final LightmapStateShard LIGHTMAP = new LightmapStateShard(true);
   private static final OverlayStateShard OVERLAY = new OverlayStateShard(true);

   public static RenderType growlingEntity(ResourceLocation texture) {
      return RenderType.create(
         "growling_entity",
         DefaultVertexFormat.NEW_ENTITY,
         Mode.QUADS,
         256,
         true,
         true,
         CompositeState.builder()
            .setShaderState(ENTITY_TRANSLUCENT_SHADER)
            .setTextureState(new TextureStateShard(texture, false, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setDepthTestState(LEQUAL_DEPTH_TEST)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .createCompositeState(true)
      );
   }
}
