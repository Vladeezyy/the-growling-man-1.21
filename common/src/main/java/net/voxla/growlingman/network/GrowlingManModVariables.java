package net.voxla.growlingman.network;

import java.util.LinkedHashSet;
import java.util.Set;
import net.voxla.growlingman.port.bus.EventPriority;
import net.voxla.growlingman.port.bus.GMBus;
import net.voxla.growlingman.port.bus.ServerTickEvent;
import net.voxla.growlingman.port.bus.PlayerEvent;
import net.voxla.growlingman.port.bus.FMLCommonSetupEvent;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.voxla.growlingman.port.bus.SubscribeEvent;

import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;

import net.voxla.growlingman.GrowlingManMod;
import net.voxla.growlingman.port.PortUtil;
import net.voxla.growlingman.port.platform.Platform;


public class GrowlingManModVariables {
	/** port/PerfScene: sync packets sent */
	public static int syncs;

	// MCreator syncs after every write of a variable, up to several times per tick for one player and every tick for
	// the spawn timers: the syncs asked for during a server tick are sent once, at its end, with the latest values
	private static final Set<ServerPlayer> PENDING_PLAYERS = new LinkedHashSet<>();
	private static final Set<ServerLevel> PENDING_WORLDS = new LinkedHashSet<>();
	private static MapVariables pendingMap;

	private static void flushSyncs(ServerTickEvent.Post event) {
		for (ServerPlayer player : PENDING_PLAYERS)
			if (!player.isRemoved() && !player.hasDisconnected()) {
				syncs++;
				Platform.INSTANCE.sendToPlayer(player, new PlayerVariablesSyncMessage(of(player)));
			}
		PENDING_PLAYERS.clear();
		for (ServerLevel level : PENDING_WORLDS) {
			syncs++;
			Platform.INSTANCE.sendToPlayersInDimension(level, new SavedDataSyncMessage(1, WorldVariables.get(level)));
		}
		PENDING_WORLDS.clear();
		if (pendingMap != null) {
			syncs++;
			Platform.INSTANCE.sendToAllPlayers(new SavedDataSyncMessage(0, pendingMap));
			pendingMap = null;
		}
	}

	/** The id of the player variables attachment (NeoForge and Fabric), as in 1.0.1 */
	public static final ResourceLocation PLAYER_VARIABLES_ID = ResourceLocation.fromNamespaceAndPath(GrowlingManMod.MODID, "player_variables");

	/** The player variables of an entity (NeoForge's {@code getData(PLAYER_VARIABLES)}) */
	public static PlayerVariables of(Entity entity) {
		return Platform.INSTANCE.playerVariables(entity);
	}

	/** Both loaders call this once at init */
	public static void registerPackets() {
		Platform.INSTANCE.registerClientbound(SavedDataSyncMessage.TYPE, SavedDataSyncMessage.STREAM_CODEC, SavedDataSyncMessage::handleData);
		Platform.INSTANCE.registerClientbound(PlayerVariablesSyncMessage.TYPE, PlayerVariablesSyncMessage.STREAM_CODEC, PlayerVariablesSyncMessage::handleData);
		GMBus.add(ServerTickEvent.Post.class, EventPriority.LOWEST, GrowlingManModVariables::flushSyncs);
	}

	@EventBusSubscriber
	public static class EventBusVariableHandlers {
		@SubscribeEvent
		public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
			if (event.getEntity() instanceof ServerPlayer player)
				of(player).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
			if (event.getEntity() instanceof ServerPlayer player)
				of(player).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (event.getEntity() instanceof ServerPlayer player)
				of(player).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void clonePlayer(PlayerEvent.Clone event) {
			PlayerVariables original = of(event.getOriginal());
			PlayerVariables clone = new PlayerVariables();
			clone.IsMarked = original.IsMarked;
			if (!event.isWasDeath()) {
				clone.GMEventsTimer = original.GMEventsTimer;
				clone.ScreenShakeTimer = original.ScreenShakeTimer;
				clone.DarkenOverlay = original.DarkenOverlay;
				clone.HoldingBreathTimer = original.HoldingBreathTimer;
				clone.IsHoldingBreath = original.IsHoldingBreath;
				clone.cym_overlay_opacity = original.cym_overlay_opacity;
				clone.mtl_overlay_opacity = original.mtl_overlay_opacity;
				clone.gm_inv_opacity = original.gm_inv_opacity;
				clone.AnimalsKilled = original.AnimalsKilled;
				clone.AnimalsKilledResetWait = original.AnimalsKilledResetWait;
				clone.BadActionTimer = original.BadActionTimer;
				clone.RaidTime = original.RaidTime;
				clone.gm_inv_ap = original.gm_inv_ap;
				clone.InvNumb = original.InvNumb;
				clone.AnimalsKilledShortTime = original.AnimalsKilledShortTime;
			}
			Platform.INSTANCE.setPlayerVariables(event.getEntity(), clone);
		}

		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
			if (event.getEntity() instanceof ServerPlayer player) {
				SavedData mapdata = MapVariables.get(event.getEntity().level());
				SavedData worlddata = WorldVariables.get(event.getEntity().level());
				if (mapdata != null)
					Platform.INSTANCE.sendToPlayer(player, new SavedDataSyncMessage(0, mapdata));
				if (worlddata != null)
					Platform.INSTANCE.sendToPlayer(player, new SavedDataSyncMessage(1, worlddata));
			}
		}

		@SubscribeEvent
		public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (event.getEntity() instanceof ServerPlayer player) {
				SavedData worlddata = WorldVariables.get(event.getEntity().level());
				if (worlddata != null)
					Platform.INSTANCE.sendToPlayer(player, new SavedDataSyncMessage(1, worlddata));
			}
		}
	}

	public static class WorldVariables extends SavedData {
		public static final String DATA_NAME = "growling_man_worldvars";

		public boolean HasOneDayPassed = false;
		public boolean IsDaytime = false;
		public double GMSpawnChance = 0.0;

		public static WorldVariables load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
			WorldVariables data = new WorldVariables();
			data.read(tag, lookupProvider);
			return data;
		}

		public void read(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
			HasOneDayPassed = nbt.getBoolean("HasOneDayPassed");
			IsDaytime = nbt.getBoolean("IsDaytime");
			GMSpawnChance = nbt.getDouble("GMSpawnChance");
		}

		@Override
		public CompoundTag save(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
			nbt.putBoolean("HasOneDayPassed", HasOneDayPassed);
			nbt.putBoolean("IsDaytime", IsDaytime);
			nbt.putDouble("GMSpawnChance", GMSpawnChance);
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof ServerLevel level)
				PENDING_WORLDS.add(level);
		}

		static WorldVariables clientSide = new WorldVariables();

		public static WorldVariables get(LevelAccessor world) {
			if (world instanceof ServerLevel level) {
				return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(WorldVariables::new, WorldVariables::load, null), DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public static class MapVariables extends SavedData {
		public static final String DATA_NAME = "growling_man_mapvars";

		public double GMSpawnTimer = 0.0;
		public boolean gmdev_overlay = false;
		public double GMCanKillPlayerTimer = 0.0;

		public static MapVariables load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
			MapVariables data = new MapVariables();
			data.read(tag, lookupProvider);
			return data;
		}

		public void read(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
			GMSpawnTimer = nbt.getDouble("GMSpawnTimer");
			gmdev_overlay = nbt.getBoolean("gmdev_overlay");
			GMCanKillPlayerTimer = nbt.getDouble("GMCanKillPlayerTimer");
		}

		@Override
		public CompoundTag save(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
			nbt.putDouble("GMSpawnTimer", GMSpawnTimer);
			nbt.putBoolean("gmdev_overlay", gmdev_overlay);
			nbt.putDouble("GMCanKillPlayerTimer", GMCanKillPlayerTimer);
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof Level && !world.isClientSide())
				pendingMap = this;
		}

		static MapVariables clientSide = new MapVariables();

		public static MapVariables get(LevelAccessor world) {
			if (world instanceof ServerLevelAccessor serverLevelAcc) {
				return serverLevelAcc.getLevel().getServer().getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(new SavedData.Factory<>(MapVariables::new, MapVariables::load, null), DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public record SavedDataSyncMessage(int dataType, SavedData data) implements CustomPacketPayload {
		public static final Type<SavedDataSyncMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GrowlingManMod.MODID, "saved_data_sync"));
		public static final StreamCodec<RegistryFriendlyByteBuf, SavedDataSyncMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, SavedDataSyncMessage message) -> {
			buffer.writeInt(message.dataType);
			if (message.data != null)
				buffer.writeNbt(message.data.save(new CompoundTag(), buffer.registryAccess()));
		}, (RegistryFriendlyByteBuf buffer) -> {
			int dataType = buffer.readInt();
			CompoundTag nbt = buffer.readNbt();
			SavedData data = null;
			if (nbt != null) {
				data = dataType == 0 ? new MapVariables() : new WorldVariables();
				if (data instanceof MapVariables mapVariables)
					mapVariables.read(nbt, buffer.registryAccess());
				else if (data instanceof WorldVariables worldVariables)
					worldVariables.read(nbt, buffer.registryAccess());
			}
			return new SavedDataSyncMessage(dataType, data);
		});

		@Override
		public Type<SavedDataSyncMessage> type() {
			return TYPE;
		}

		/** Client thread */
		public static void handleData(final SavedDataSyncMessage message) {
			HolderLookup.Provider registries = PortUtil.clientRegistries.get();
			if (message.data != null && registries != null) {
				if (message.dataType == 0)
					MapVariables.clientSide.read(message.data.save(new CompoundTag(), registries), registries);
				else
					WorldVariables.clientSide.read(message.data.save(new CompoundTag(), registries), registries);
			}
		}

	}

	public static class PlayerVariables {
		public double GMEventsTimer = 0.0;
		public double ScreenShakeTimer = 0.0;
		public double DarkenOverlay = 0.0;
		public double HoldingBreathTimer = 0.0;
		public boolean IsHoldingBreath = false;
		public double IsMarked = 0.0;
		public double cym_overlay_opacity = 0.0;
		public double mtl_overlay_opacity = 0.0;
		public double gm_inv_opacity = 0.0;
		public double AnimalsKilled = 0.0;
		public double AnimalsKilledResetWait = 0.0;
		public double BadActionTimer = 0.0;
		public double RaidTime = 0.0;
		public double gm_inv_ap = 0.0;
		public double InvNumb = 0.0;
		public double AnimalsKilledShortTime = 0.0;

		public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
			CompoundTag nbt = new CompoundTag();
			nbt.putDouble("GMEventsTimer", GMEventsTimer);
			nbt.putDouble("ScreenShakeTimer", ScreenShakeTimer);
			nbt.putDouble("DarkenOverlay", DarkenOverlay);
			nbt.putDouble("HoldingBreathTimer", HoldingBreathTimer);
			nbt.putBoolean("IsHoldingBreath", IsHoldingBreath);
			nbt.putDouble("IsMarked", IsMarked);
			nbt.putDouble("cym_overlay_opacity", cym_overlay_opacity);
			nbt.putDouble("mtl_overlay_opacity", mtl_overlay_opacity);
			nbt.putDouble("gm_inv_opacity", gm_inv_opacity);
			nbt.putDouble("AnimalsKilled", AnimalsKilled);
			nbt.putDouble("AnimalsKilledResetWait", AnimalsKilledResetWait);
			nbt.putDouble("BadActionTimer", BadActionTimer);
			nbt.putDouble("RaidTime", RaidTime);
			nbt.putDouble("gm_inv_ap", gm_inv_ap);
			nbt.putDouble("InvNumb", InvNumb);
			nbt.putDouble("AnimalsKilledShortTime", AnimalsKilledShortTime);
			return nbt;
		}

		public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
			GMEventsTimer = nbt.getDouble("GMEventsTimer");
			ScreenShakeTimer = nbt.getDouble("ScreenShakeTimer");
			DarkenOverlay = nbt.getDouble("DarkenOverlay");
			HoldingBreathTimer = nbt.getDouble("HoldingBreathTimer");
			IsHoldingBreath = nbt.getBoolean("IsHoldingBreath");
			IsMarked = nbt.getDouble("IsMarked");
			cym_overlay_opacity = nbt.getDouble("cym_overlay_opacity");
			mtl_overlay_opacity = nbt.getDouble("mtl_overlay_opacity");
			gm_inv_opacity = nbt.getDouble("gm_inv_opacity");
			AnimalsKilled = nbt.getDouble("AnimalsKilled");
			AnimalsKilledResetWait = nbt.getDouble("AnimalsKilledResetWait");
			BadActionTimer = nbt.getDouble("BadActionTimer");
			RaidTime = nbt.getDouble("RaidTime");
			gm_inv_ap = nbt.getDouble("gm_inv_ap");
			InvNumb = nbt.getDouble("InvNumb");
			AnimalsKilledShortTime = nbt.getDouble("AnimalsKilledShortTime");
		}

		public void syncPlayerVariables(Entity entity) {
			if (entity instanceof ServerPlayer serverPlayer)
				PENDING_PLAYERS.add(serverPlayer);
		}
	}

	public record PlayerVariablesSyncMessage(PlayerVariables data) implements CustomPacketPayload {
		public static final Type<PlayerVariablesSyncMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GrowlingManMod.MODID, "player_variables_sync"));
		public static final StreamCodec<RegistryFriendlyByteBuf, PlayerVariablesSyncMessage> STREAM_CODEC = StreamCodec
				.of((RegistryFriendlyByteBuf buffer, PlayerVariablesSyncMessage message) -> buffer.writeNbt(message.data().serializeNBT(buffer.registryAccess())), (RegistryFriendlyByteBuf buffer) -> {
					PlayerVariablesSyncMessage message = new PlayerVariablesSyncMessage(new PlayerVariables());
					message.data.deserializeNBT(buffer.registryAccess(), buffer.readNbt());
					return message;
				});

		@Override
		public Type<PlayerVariablesSyncMessage> type() {
			return TYPE;
		}

		/** Client thread */
		public static void handleData(final PlayerVariablesSyncMessage message) {
			Entity player = PortUtil.clientPlayer.get();
			if (message.data != null && player != null)
				of(player).deserializeNBT(player.registryAccess(), message.data.serializeNBT(player.registryAccess()));
		}

	}
}
