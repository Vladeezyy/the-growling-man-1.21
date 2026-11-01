package net.voxla.growlingman.client.screens;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent.Render.Post;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.voxla.growlingman.procedures.ShowMarkConditionProcedure;

@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.FORGE)
public class GMMarkedPlayerOverlay {
   private static final ResourceLocation MARK_TEXTURE = new ResourceLocation("growling_man:textures/screens/player_mark.png");

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public static void eventHandler(Post event) {
      if (event.getScreen() instanceof InventoryScreen invScreen) {
         int guiLeft = invScreen.getGuiLeft();
         int guiTop = invScreen.getGuiTop();
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
