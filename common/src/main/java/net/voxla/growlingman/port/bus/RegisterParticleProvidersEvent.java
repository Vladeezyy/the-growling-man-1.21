package net.voxla.growlingman.port.bus;

import net.minecraft.client.particle.ParticleResources;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public class RegisterParticleProvidersEvent extends Event {
	private final Sink sink;

	public RegisterParticleProvidersEvent(Sink sink) {
		this.sink = sink;
	}

	public <T extends ParticleOptions> void registerSpriteSet(ParticleType<T> type, ParticleResources.SpriteParticleRegistration<T> registration) {
		this.sink.register(type, registration);
	}

	public interface Sink {
		<T extends ParticleOptions> void register(ParticleType<T> type, ParticleResources.SpriteParticleRegistration<T> registration);
	}
}
