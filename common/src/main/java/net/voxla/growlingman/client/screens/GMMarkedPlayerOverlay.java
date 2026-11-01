package net.voxla.growlingman.client.screens;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.voxla.growlingman.port.bus.Dist;
import net.voxla.growlingman.port.bus.ScreenEvent;
import net.voxla.growlingman.port.bus.EventPriority;
import net.voxla.growlingman.port.bus.SubscribeEvent;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.voxla.growlingman.procedures.ShowMarkConditionProcedure;

@EventBusSubscriber(Dist.CLIENT)
public class GMMarkedPlayerOverlay {
	private static final ResourceLocation MARK_TEXTURE = ResourceLocation.parse("growling_man:textures/screens/player_mark.png");

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void eventHandler(ScreenEvent.Render.Post event) {
		if (event.getScreen() instanceof InventoryScreen invScreen) {
			int guiLeft = invScreen.leftPos;
			int guiTop = invScreen.topPos;
			Player entity = Minecraft.getInstance().player;
			if (entity == null) {
				return;
			}

			if (ShowMarkConditionProcedure.execute(entity)) {
				GuiGraphics guiGraphics = event.getGuiGraphics();
				RenderSystem.disableDepthTest();
				RenderSystem.depthMask(false);
				RenderSystem.enableBlend();
				RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				guiGraphics.blit(MARK_TEXTURE, guiLeft + 126, guiTop + 57, 0.0F, 0.0F, 46, 18, 46, 18);
				RenderSystem.depthMask(true);
				RenderSystem.defaultBlendFunc();
				RenderSystem.enableDepthTest();
				RenderSystem.disableBlend();
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			}
		}
	}
}
