package net.voxla.growlingman.port.bus;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class RenderGuiEvent extends Event {
	private final GuiGraphicsExtractor guiGraphics;

	protected RenderGuiEvent(GuiGraphicsExtractor guiGraphics) {
		this.guiGraphics = guiGraphics;
	}

	public GuiGraphicsExtractor getGuiGraphics() {
		return this.guiGraphics;
	}

	/** Before the vanilla HUD layers */
	public static class Pre extends RenderGuiEvent {
		public Pre(GuiGraphicsExtractor guiGraphics) {
			super(guiGraphics);
		}
	}
}
