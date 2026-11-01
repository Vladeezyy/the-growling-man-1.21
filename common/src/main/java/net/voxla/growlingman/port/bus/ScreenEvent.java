package net.voxla.growlingman.port.bus;

import net.minecraft.client.gui.GuiGraphicsExtractor;
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
		private final GuiGraphicsExtractor guiGraphics;

		protected Render(Screen screen, GuiGraphicsExtractor guiGraphics) {
			super(screen);
			this.guiGraphics = guiGraphics;
		}

		public GuiGraphicsExtractor getGuiGraphics() {
			return this.guiGraphics;
		}

		/** After the screen has drawn itself */
		public static class Post extends Render {
			public Post(Screen screen, GuiGraphicsExtractor guiGraphics) {
				super(screen, guiGraphics);
			}
		}
	}
}
