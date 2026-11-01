package net.voxla.growlingman.port;

import net.voxla.growlingman.port.bus.EntityLeaveLevelEvent;
import net.voxla.growlingman.port.bus.EntityJoinLevelEvent;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.voxla.growlingman.port.bus.SubscribeEvent;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.registries.BuiltInRegistries;

import net.voxla.growlingman.GrowlingManMod;

import java.util.function.Predicate;
import java.util.WeakHashMap;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.IdentityHashMap;
import java.util.Collections;
import java.util.ArrayList;

/**
 * Entity searches of the 1.20.1 procedures without the area walk. They look for players or for this mod's entities in
 * boxes up to 1000 blocks wide ("is any Lurker within 350 blocks?"), up to a few hundred times per natural spawn attempt
 * and every tick: a vanilla getEntitiesOfClass walks every entity section in such a box. Here players come from the
 * level's player list and the mod's entities from a per-level set kept by join / leave events (there are only a few),
 * and the same test as vanilla's (bounding box intersects the box, then the predicate) picks the result.
 */
@EventBusSubscriber
public final class GMEntities {
	private static final Map<Level, Set<Entity>> BY_LEVEL = Collections.synchronizedMap(new WeakHashMap<>());
	private static final String ENTITY_PACKAGE = "net.voxla.growlingman.entity.";

	private static final java.util.Map<net.minecraft.world.entity.EntityType<?>, Boolean> OURS = new java.util.concurrent.ConcurrentHashMap<>();

	/** True for this mod's entity types (cached per type) */
	public static boolean ours(Entity entity) {
		return OURS.computeIfAbsent(entity.getType(), type -> BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace().equals(GrowlingManMod.MODID));
	}

	private static Set<Entity> set(Level level) {
		return BY_LEVEL.computeIfAbsent(level, l -> Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap<>())));
	}

	@SubscribeEvent
	public static void onJoin(EntityJoinLevelEvent event) {
		if (ours(event.getEntity()))
			set(event.getLevel()).add(event.getEntity());
	}

	@SubscribeEvent
	public static void onLeave(EntityLeaveLevelEvent event) {
		if (ours(event.getEntity()))
			set(event.getLevel()).remove(event.getEntity());
	}

	/** {@code world.getEntitiesOfClass(type, box, filter)} for players and this mod's entities; anything else asks the level. */
	public static <T extends Entity> List<T> of(LevelAccessor world, Class<T> type, AABB box, Predicate<? super T> filter) {
		List<T> found = new ArrayList<>();
		if (type == Player.class) {
			for (Player player : world.players())
				if (type.isInstance(player) && player.getBoundingBox().intersects(box) && filter.test(type.cast(player)))
					found.add(type.cast(player));
			return found;
		}
		if (world instanceof Level level && type.getName().startsWith(ENTITY_PACKAGE)) {
			Entity[] candidates;
			Set<Entity> set = set(level);
			synchronized (set) {
				candidates = set.toArray(new Entity[0]);
			}
			for (Entity entity : candidates)
				if (type.isInstance(entity) && !entity.isRemoved() && entity.getBoundingBox().intersects(box) && filter.test(type.cast(entity)))
					found.add(type.cast(entity));
			return found;
		}
		return world.getEntitiesOfClass(type, box, filter);
	}

	private GMEntities() {
	}
}
