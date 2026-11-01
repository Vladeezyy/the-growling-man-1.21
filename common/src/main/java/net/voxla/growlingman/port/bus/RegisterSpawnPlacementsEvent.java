package net.voxla.growlingman.port.bus;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.levelgen.Heightmap;

public class RegisterSpawnPlacementsEvent extends Event {
	private final Sink sink;

	public RegisterSpawnPlacementsEvent(Sink sink) {
		this.sink = sink;
	}

	public <T extends Mob> void register(EntityType<T> type, SpawnPlacementType placement, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> predicate, Operation operation) {
		this.sink.register(type, placement, heightmap, predicate, operation);
	}

	public enum Operation {
		REPLACE, OR, AND
	}

	public interface Sink {
		<T extends Mob> void register(EntityType<T> type, SpawnPlacementType placement, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> predicate, Operation operation);
	}
}
