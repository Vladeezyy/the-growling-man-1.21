package net.voxla.growlingman.port;

import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.constant.dataticket.DataTicket;

import net.minecraft.resources.Identifier;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * GeckoLib 5 models see a render state, not the entity: the texture name the entity syncs (its TEXTURE data) is copied
 * into the state when it is extracted, and turned into textures/entities/<name>.png when the model asks for it.
 */
public final class GeoTextures {
	public static final DataTicket<String> TEXTURE = DataTicket.create("growling_man_texture", String.class);
	private static final Map<String, Identifier> IDS = new ConcurrentHashMap<>();

	private GeoTextures() {
	}

	public static void set(GeoRenderState renderState, String texture) {
		renderState.addGeckolibData(TEXTURE, texture);
	}

	public static Identifier get(GeoRenderState renderState) {
		String texture = renderState.getOrDefaultGeckolibData(TEXTURE, "growling_man");
		return IDS.computeIfAbsent(texture, t -> Identifier.fromNamespaceAndPath("growling_man", "textures/entities/" + t + ".png"));
	}
}
