package net.voxla.growlingman.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.voxla.growlingman.port.bus.Dist;
import net.voxla.growlingman.port.bus.RenderGuiEvent;
import net.voxla.growlingman.port.bus.EventPriority;
import net.voxla.growlingman.port.bus.SubscribeEvent;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.voxla.growlingman.procedures.ClearMindKeybindOverlayDisplayOverlayIngameProcedure;
import net.voxla.growlingman.procedures.MindClearKeyLangRtnProcedure;

@EventBusSubscriber(Dist.CLIENT)
public class ClearMindKeybindOverlayOverlay {
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

		if (ClearMindKeybindOverlayDisplayOverlayIngameProcedure.execute(world, x, y, z, entity)) {
			Minecraft mc = Minecraft.getInstance();
			GuiGraphics guiGraphics = event.getGuiGraphics();
			PoseStack poseStack = guiGraphics.pose();
			String text = MindClearKeyLangRtnProcedure.execute();
			Font font = mc.font;
			float scale = 1.0F;
			int textWidth = font.width(text);
			float xs = (mc.getWindow().getGuiScaledWidth() - (textWidth * scale - 0.1F)) / 2.0F / scale;
			float ys = 280.0F / scale;
			poseStack.pushPose();
			poseStack.scale(scale, scale, scale);
			guiGraphics.drawString(font, text, (int)xs, (int)ys, 16777215, true);
			poseStack.popPose();
		}
	}
}
