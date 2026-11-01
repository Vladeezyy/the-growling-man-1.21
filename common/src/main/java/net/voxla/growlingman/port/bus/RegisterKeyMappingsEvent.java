package net.voxla.growlingman.port.bus;

import net.minecraft.client.KeyMapping;

import java.util.function.Consumer;

/** Client setup: the mod's key mappings */
public class RegisterKeyMappingsEvent extends Event {
	private final Consumer<KeyMapping> sink;

	public RegisterKeyMappingsEvent(Consumer<KeyMapping> sink) {
		this.sink = sink;
	}

	public void register(KeyMapping key) {
		this.sink.accept(key);
	}
}
