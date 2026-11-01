package net.voxla.growlingman.port.bus;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class RegisterMenuScreensEvent extends Event {
	private final Sink sink;

	public RegisterMenuScreensEvent(Sink sink) {
		this.sink = sink;
	}

	public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> type, MenuScreens.ScreenConstructor<M, U> constructor) {
		this.sink.register(type, constructor);
	}

	public interface Sink {
		<M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> type, MenuScreens.ScreenConstructor<M, U> constructor);
	}
}
