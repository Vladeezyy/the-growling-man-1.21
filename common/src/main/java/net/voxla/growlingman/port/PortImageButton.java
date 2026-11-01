package net.voxla.growlingman.port;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.GuiGraphics;

import com.mojang.blaze3d.systems.RenderSystem;

/** The 1.20 ImageButton (one texture, hovered / disabled states stacked vertically), which 1.21 replaced by sprites. */
public class PortImageButton extends ImageButton {
	private final ResourceLocation texture;
	private final int xTexStart;
	private final int yTexStart;
	private final int yDiffTex;
	private final int textureWidth;
	private final int textureHeight;

	public PortImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation texture, int textureWidth, int textureHeight, OnPress onPress) {
		super(x, y, width, height, new WidgetSprites(texture, texture), onPress);
		this.texture = texture;
		this.xTexStart = xTexStart;
		this.yTexStart = yTexStart;
		this.yDiffTex = yDiffTex;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		int v = this.yTexStart;
		if (!this.isActive())
			v += this.yDiffTex * 2;
		else if (this.isHoveredOrFocused())
			v += this.yDiffTex;
		RenderSystem.enableDepthTest();
		guiGraphics.blit(this.texture, this.getX(), this.getY(), (float) this.xTexStart, (float) v, this.width, this.height, this.textureWidth, this.textureHeight);
	}
}
