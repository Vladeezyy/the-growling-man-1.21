package net.voxla.growlingman.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.voxla.growlingman.entity.GrowlingManEntity;
import net.voxla.growlingman.entity.model.GrowlingManModel;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;

/**
 * 1.0.1 drew the model twice: once with an empty texture and once, in a render layer, with the entity's texture in a
 * see-through render type (the same settings as entityTranslucent) at the alpha of DATA_Transparency. GeckoLib 5 draws it
 * once that way; the shadow shows in the ghost states (AI 17 and 18), as before.
 */
public class GrowlingManRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<GrowlingManEntity, R> {
	private static final DataTicket<Boolean> GHOST = DataTicket.create("growling_man_ghost", Boolean.class);

	public GrowlingManRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new GrowlingManModel());
	}

	@Override
	public RenderType getRenderType(R renderState, Identifier texture) {
		return RenderTypes.entityTranslucent(texture);
	}

	@Override
	public int getRenderColor(GrowlingManEntity entity, Void relatedObject, float partialTick) {
		return ARGB.colorFromFloat(entity.getEntityData().get(GrowlingManEntity.DATA_Transparency) / 360.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void addRenderData(GrowlingManEntity entity, Void relatedObject, R renderState, float partialTick) {
		super.addRenderData(entity, relatedObject, renderState, partialTick);
		int ai = entity.getEntityData().get(GrowlingManEntity.DATA_AI);
		renderState.addGeckolibData(GHOST, ai == 17 || ai == 18);
	}

	@Override
	protected float getShadowRadius(R renderState) {
		return renderState.getOrDefaultGeckolibData(GHOST, false) ? 0.5F : 0.0F;
	}
}
