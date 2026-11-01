package net.voxla.growlingman.port.bus;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public abstract class EntityRenderersEvent extends Event {
	public static class RegisterRenderers extends EntityRenderersEvent {
		private final Sink sink;

		public RegisterRenderers(Sink sink) {
			this.sink = sink;
		}

		public <T extends Entity> void registerEntityRenderer(EntityType<? extends T> type, EntityRendererProvider<T> provider) {
			this.sink.register(type, provider);
		}

		public interface Sink {
			<T extends Entity> void register(EntityType<? extends T> type, EntityRendererProvider<T> provider);
		}
	}

	public static class RegisterLayerDefinitions extends EntityRenderersEvent {
		private final BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> sink;

		public RegisterLayerDefinitions(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> sink) {
			this.sink = sink;
		}

		public void registerLayerDefinition(ModelLayerLocation location, Supplier<LayerDefinition> definition) {
			this.sink.accept(location, definition);
		}
	}
}
