package net.voxla.growlingman.fabric;

import io.netty.buffer.Unpooled;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.voxla.growlingman.GrowlingManMod;
import net.voxla.growlingman.network.GrowlingManModVariables;
import net.voxla.growlingman.port.platform.Platform;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The Midnight Lurker on Fabric. Entries are queued and registered by {@link #registerAll()} in NeoForge's registry
 * order; the player variables and NeoForge's persistent data are Fabric data attachments.
 */
public final class FabricPlatform implements Platform {
	private record Pending(ResourceLocation id, Supplier<?> factory) {
	}

	record Clientbound<T extends CustomPacketPayload>(CustomPacketPayload.Type<T> type, Consumer<T> handler) {
	}

	private static final Map<ResourceKey<?>, List<Pending>> PENDING = new LinkedHashMap<>();
	/** Registered on the client by the client entry point */
	static final List<Clientbound<?>> CLIENTBOUND = new ArrayList<>();
	static MinecraftServer server;

	/** NeoForge's getPersistentData(): the entity's / block entity's own tag, saved with it */
	static final AttachmentType<CompoundTag> PERSISTENT_DATA = AttachmentRegistry.<CompoundTag>builder().persistent(CompoundTag.CODEC).initializer(CompoundTag::new)
			.buildAndRegister(ResourceLocation.fromNamespaceAndPath(GrowlingManMod.MODID, "persistent_data"));
	static final AttachmentType<GrowlingManModVariables.PlayerVariables> PLAYER_VARIABLES = AttachmentRegistry.<GrowlingManModVariables.PlayerVariables>builder()
			.persistent(CompoundTag.CODEC.xmap(tag -> {
				GrowlingManModVariables.PlayerVariables variables = new GrowlingManModVariables.PlayerVariables();
				variables.deserializeNBT(null, tag);
				return variables;
			}, variables -> variables.serializeNBT(null))).initializer(GrowlingManModVariables.PlayerVariables::new)
			.buildAndRegister(GrowlingManModVariables.PLAYER_VARIABLES_ID);

	/** Registers the queued entries: attributes, data components, then vanilla order, then modded registries */
	@SuppressWarnings({"unchecked", "rawtypes"})
	static void registerAll() {
		Set<ResourceLocation> order = new LinkedHashSet<>();
		order.add(Registries.ATTRIBUTE.location());
		order.add(Registries.DATA_COMPONENT_TYPE.location());
		List<ResourceLocation> modded = new ArrayList<>();
		// the registry of registries in registration order (its keySet() has no order)
		for (Registry<?> each : BuiltInRegistries.REGISTRY) {
			ResourceLocation id = each.key().location();
			if (id.getNamespace().equals("minecraft"))
				order.add(id);
			else
				modded.add(id);
		}
		order.addAll(modded);
		for (ResourceLocation registryId : order) {
			List<Pending> entries = PENDING.remove(ResourceKey.createRegistryKey(registryId));
			if (entries == null)
				continue;
			Registry registry = BuiltInRegistries.REGISTRY.get(registryId);
			for (Pending entry : entries)
				Registry.register(registry, entry.id(), entry.factory().get());
		}
		if (!PENDING.isEmpty())
			throw new IllegalStateException("Entries for registries that do not exist: " + PENDING.keySet());
	}

	@Override
	public <R, T extends R> void register(ResourceKey<? extends Registry<R>> registry, ResourceLocation id, Supplier<? extends T> factory) {
		PENDING.computeIfAbsent(registry, key -> new ArrayList<>()).add(new Pending(id, factory));
	}

	@Override
	public Path gameDir() {
		return FabricLoader.getInstance().getGameDir();
	}

	@Override
	public Path configDir() {
		return FabricLoader.getInstance().getConfigDir();
	}

	@Override
	@Nullable
	public MinecraftServer currentServer() {
		return server;
	}

	@Override
	public CompoundTag persistentData(Entity entity) {
		return entity.getAttachedOrCreate(PERSISTENT_DATA);
	}

	@Override
	public CompoundTag persistentData(BlockEntity blockEntity) {
		return blockEntity.getAttachedOrCreate(PERSISTENT_DATA);
	}

	@Override
	public GrowlingManModVariables.PlayerVariables playerVariables(Entity entity) {
		return entity.getAttachedOrCreate(PLAYER_VARIABLES);
	}

	@Override
	public void setPlayerVariables(Entity entity, GrowlingManModVariables.PlayerVariables variables) {
		entity.setAttached(PLAYER_VARIABLES, variables);
	}

	@Override
	public <T extends CustomPacketPayload> void registerClientbound(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, Consumer<T> handler) {
		PayloadTypeRegistry.playS2C().register(type, codec);
		CLIENTBOUND.add(new Clientbound<>(type, handler));
	}

	@Override
	public <T extends CustomPacketPayload> void registerServerbound(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, ServerPlayer> handler) {
		PayloadTypeRegistry.playC2S().register(type, codec);
		// Fabric API calls server receivers on the server thread
		ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> handler.accept(payload, context.player()));
	}

	@Override
	public void sendToServer(CustomPacketPayload payload) {
		net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(payload);
	}

	@Override
	public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
		ServerPlayNetworking.send(player, payload);
	}

	@Override
	public void sendToPlayersInDimension(ServerLevel level, CustomPacketPayload payload) {
		for (ServerPlayer player : level.players())
			ServerPlayNetworking.send(player, payload);
	}

	@Override
	public void sendToAllPlayers(CustomPacketPayload payload) {
		if (server != null)
			for (ServerPlayer player : server.getPlayerList().getPlayers())
				ServerPlayNetworking.send(player, payload);
	}

	@Override
	public <M extends AbstractContainerMenu> MenuType<M> menuType(MenuFactory<M> factory) {
		// the extra data travels as bytes and is read back with the client's registries
		return new ExtendedScreenHandlerType<>((int id, Inventory inventory, byte[] data) -> factory.create(id, inventory,
				new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(data), inventory.player.registryAccess())), ByteBufCodecs.BYTE_ARRAY);
	}

	@Override
	public void openMenu(ServerPlayer player, MenuProvider provider, Consumer<RegistryFriendlyByteBuf> extraData) {
		RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), player.registryAccess());
		extraData.accept(buf);
		byte[] data = new byte[buf.readableBytes()];
		buf.readBytes(data);
		player.openMenu(new ExtendedScreenHandlerFactory<byte[]>() {
			@Override
			public byte[] getScreenOpeningData(ServerPlayer opener) {
				return data;
			}

			@Override
			public Component getDisplayName() {
				return provider.getDisplayName();
			}

			@Override
			public AbstractContainerMenu createMenu(int id, Inventory inventory, Player opener) {
				return provider.createMenu(id, inventory, opener);
			}
		});
	}

	@Override
	public ServerPlayer fakePlayer(ServerLevel level) {
		return FakePlayer.get(level);
	}
}
