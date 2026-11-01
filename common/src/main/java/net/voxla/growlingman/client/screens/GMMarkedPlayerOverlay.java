package net.voxla.growlingman.client.screens;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.voxla.growlingman.port.bus.Dist;
import net.voxla.growlingman.port.bus.ScreenEvent;
import net.voxla.growlingman.port.bus.EventPriority;
import net.voxla.growlingman.port.bus.SubscribeEvent;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.voxla.growlingman.procedures.ShowMarkConditionProcedure;

@EventBusSubscriber(Dist.CLIENT)
public class GMMarkedPlayerOverlay {
	private static final Identifier MARK_TEXTURE = Identifier.parse("growling_man:textures/screens/player_mark.png");

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
				GuiGraphicsExtractor guiGraphics = event.getGuiGraphics();
				guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MARK_TEXTURE, guiLeft + 126, guiTop + 57, 0.0F, 0.0F, 46, 18, 46, 18);
			}
		}
	}
}
