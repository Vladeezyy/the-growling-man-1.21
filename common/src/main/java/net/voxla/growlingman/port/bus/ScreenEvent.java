package net.voxla.growlingman.port.bus;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public abstract class ScreenEvent extends Event {
	private final Screen screen;

	protected ScreenEvent(Screen screen) {
		this.screen = screen;
	}

	public Screen getScreen() {
		return this.screen;
	}

	public abstract static class Render extends ScreenEvent {
		private final GuiGraphics guiGraphics;

		protected Render(Screen screen, GuiGraphics guiGraphics) {
			super(screen);
			this.guiGraphics = guiGraphics;
		}

		public GuiGraphics getGuiGraphics() {
			return this.guiGraphics;
		}

		/** After the screen has drawn itself */
		public static class Post extends Render {
			public Post(Screen screen, GuiGraphics guiGraphics) {
				super(screen, guiGraphics);
			}
		}
	}
}
