package net.voxla.growlingman.port;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.voxla.growlingman.entity.GrowlingManEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Faster forms of the entity searches of 1.0.1 with the same results. {@code getEntitiesOfClass(Player.class, box)} walks
 * every entity section in boxes up to 600 blocks wide; the players of the level are a short list, tested the same way
 * (bounding box intersects the box).
 */
public final class GMEntities {
	private GMEntities() {
	}

	/** The players whose bounding box intersects {@code box}: what {@code world.getEntitiesOfClass(Player.class, box, e -> true)} returns */
	public static List<Player> players(LevelAccessor world, AABB box) {
		List<Player> found = new ArrayList<>(2);
		for (Player player : world.players())
			if (!player.isRemoved() && player.getBoundingBox().intersects(box))
				found.add(player);
		return found;
	}

	/** {@code players(...)} as entities, for the loops of 1.0.1 over every entity that act on players only */
	public static List<Entity> playersAsEntities(LevelAccessor world, AABB box) {
		return new ArrayList<>(players(world, box));
	}

	/**
	 * Whether a Growling Man is at one of the points 0..159 blocks along the player's view, a point being where the view ray
	 * of that length ends or what it hits first (1.0.1: 160 rays of growing length per player and tick, each cast three
	 * times; the ray of length d ends at the first block hit if that is nearer than d, else at its end point).
	 */
	public static boolean growlingManInView(LevelAccessor world, Entity player) {
		Vec3 eye = player.getEyePosition(1.0F);
		Vec3 view = player.getViewVector(1.0F);
		List<GrowlingManEntity> men = world.getEntitiesOfClass(GrowlingManEntity.class, new AABB(eye, eye).inflate(176.0), e -> true);
		if (men.isEmpty())
			return false;
		BlockHitResult hit = player.level().clip(new ClipContext(eye, eye.add(view.scale(159.0)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
		double hitDistance = hit.getType() == HitResult.Type.MISS ? Double.MAX_VALUE : hit.getLocation().distanceTo(eye);
		BlockPos last = null;
		for (int d = 0; d < 160; d++) {
			BlockPos pos = d < hitDistance ? BlockPos.containing(eye.add(view.scale(d))) : hit.getBlockPos();
			if (pos.equals(last))
				continue;
			last = pos;
			AABB box = AABB.ofSize(new Vec3(pos.getX(), pos.getY(), pos.getZ()), 1.0, 1.0, 1.0);
			for (GrowlingManEntity man : men)
				if (man.getBoundingBox().intersects(box))
					return true;
			if (d >= hitDistance)
				return false;
		}
		return false;
	}
}
