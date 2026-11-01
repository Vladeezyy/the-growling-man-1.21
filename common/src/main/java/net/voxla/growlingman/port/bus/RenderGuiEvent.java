package net.voxla.growlingman.port.bus;

import net.minecraft.client.gui.GuiGraphics;

public abstract class RenderGuiEvent extends Event {
	private final GuiGraphics guiGraphics;

	protected RenderGuiEvent(GuiGraphics guiGraphics) {
		this.guiGraphics = guiGraphics;
	}

	public GuiGraphics getGuiGraphics() {
		return this.guiGraphics;
	}

	/** Before the vanilla HUD layers */
	public static class Pre extends RenderGuiEvent {
		public Pre(GuiGraphics guiGraphics) {
			super(guiGraphics);
		}
	}
}
