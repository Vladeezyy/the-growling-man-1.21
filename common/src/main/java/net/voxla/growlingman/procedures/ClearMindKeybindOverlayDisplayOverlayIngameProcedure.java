package net.voxla.growlingman.procedures;

import java.util.Comparator;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.voxla.growlingman.entity.GrowlingManEntity;

public class ClearMindKeybindOverlayDisplayOverlayIngameProcedure {
	public static boolean execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null) {
			return false;
		} else {
			if (!world.getEntitiesOfClass(GrowlingManEntity.class, AABB.ofSize(new Vec3(x, y, z), 30.0, 30.0, 30.0), e -> true).isEmpty()
				&& !(new Object() {
						public boolean checkGamemode(Entity _ent) {
							if (_ent instanceof ServerPlayer _serverPlayer) {
								return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
							} else {
								return _ent.level().isClientSide() && _ent instanceof Player _player
									? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()) != null
										&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()).getGameMode() == GameType.CREATIVE
									: false;
							}
						}
					})
					.checkGamemode(entity)
				&& !(new Object() {
						public boolean checkGamemode(Entity _ent) {
							if (_ent instanceof ServerPlayer _serverPlayer) {
								return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
							} else {
								return _ent.level().isClientSide() && _ent instanceof Player _player
									? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()) != null
										&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()).getGameMode() == GameType.SPECTATOR
									: false;
							}
						}
					})
					.checkGamemode(entity)
				&& world.getEntitiesOfClass(GrowlingManEntity.class, AABB.ofSize(new Vec3(x, y, z), 30.0, 30.0, 30.0), e -> true).stream().sorted((new Object() {
					Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
						return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
					}
				}).compareDistOf(x, y, z)).findFirst().orElse(null) != null) {
				Entity var10 = world.getEntitiesOfClass(GrowlingManEntity.class, AABB.ofSize(new Vec3(x, y, z), 30.0, 30.0, 30.0), e -> true)
					.stream()
					.sorted((new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}).compareDistOf(x, y, z))
					.findFirst()
					.orElse(null);
				if ((var10 instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent) : 0) > 0) {
					var10 = world.getEntitiesOfClass(GrowlingManEntity.class, AABB.ofSize(new Vec3(x, y, z), 30.0, 30.0, 30.0), e -> true).stream().sorted((new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}).compareDistOf(x, y, z)).findFirst().orElse(null);
					if ((var10 instanceof GrowlingManEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(GrowlingManEntity.DATA_RunAtRunAwayEvent) : 0) < 155
						)
					 {
						return true;
					}
				}
			}

			return false;
		}
	}
}
