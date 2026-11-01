package net.voxla.growlingman.client.screens;

import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.voxla.growlingman.port.bus.Dist;
import net.voxla.growlingman.port.bus.RenderGuiEvent;
import net.voxla.growlingman.port.bus.EventPriority;
import net.voxla.growlingman.port.bus.SubscribeEvent;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.voxla.growlingman.network.GrowlingManModVariables;
import net.voxla.growlingman.procedures.ClearMindOverlayDisplayOverlayIngameProcedure;

@EventBusSubscriber(Dist.CLIENT)
public class ClearMindOverlayOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getGuiGraphics().guiWidth();
		int h = event.getGuiGraphics().guiHeight();
		Level world = null;
		double x = 0.0;
		double y = 0.0;
		double z = 0.0;
		Player entity = Minecraft.getInstance().player;
		if (entity != null) {
			world = entity.level();
			x = entity.getX();
			y = entity.getY();
			z = entity.getZ();
		}

		if (ClearMindOverlayDisplayOverlayIngameProcedure.execute(entity)) {
			event.getGuiGraphics().blit(RenderPipelines.GUI_TEXTURED, Identifier.parse("growling_man:textures/screens/cym_overlay.png"), 0, 0, 0.0F, 0.0F, w, h, w, h, ARGB.colorFromFloat(Mth.clamp((float)GrowlingManModVariables.of(entity) .cym_overlay_opacity / 150.0F, 0.0F, 1.0F), 1.0F, 1.0F, 1.0F));
		}

	}
}
