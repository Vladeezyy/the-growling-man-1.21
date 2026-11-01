package net.voxla.growlingman.port.platform;

import net.voxla.growlingman.network.GrowlingManModVariables;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ServiceLoader;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** What the common code needs from the loader; NeoForge and Fabric implement it (found by {@link ServiceLoader}) */
public interface Platform {
	Platform INSTANCE = ServiceLoader.load(Platform.class, Platform.class.getClassLoader()).findFirst().orElseThrow();

	/** Registers {@code factory}'s value under {@code id} (NeoForge: on its register event, Fabric: in registry order at init) */
	<R, T extends R> void register(ResourceKey<? extends Registry<R>> registry, ResourceLocation id, Supplier<? extends T> factory);

	Path gameDir();

	Path configDir();

	@Nullable
	MinecraftServer currentServer();

	/** The entity's own saved tag (NeoForge's {@code getPersistentData()}) */
	CompoundTag persistentData(Entity entity);

	/** The block entity's own saved tag (NeoForge's {@code getPersistentData()}); call {@code setChanged()} after writing */
	CompoundTag persistentData(net.minecraft.world.level.block.entity.BlockEntity blockEntity);

	/** The player variables of an entity (created on first use) */
	GrowlingManModVariables.PlayerVariables playerVariables(Entity entity);

	void setPlayerVariables(Entity entity, GrowlingManModVariables.PlayerVariables variables);

	/** A server-to-client packet; {@code handler} runs on the client thread */
	<T extends CustomPacketPayload> void registerClientbound(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, Consumer<T> handler);

	/** A client-to-server packet; {@code handler} runs on the server thread with the sender */
	<T extends CustomPacketPayload> void registerServerbound(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, ServerPlayer> handler);

	/** Client to server (client thread) */
	void sendToServer(CustomPacketPayload payload);

	void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);

	void sendToPlayersInDimension(ServerLevel level, CustomPacketPayload payload);

	void sendToAllPlayers(CustomPacketPayload payload);

	/** A menu type whose client side reads extra data written by {@link #openMenu} */
	<M extends AbstractContainerMenu> MenuType<M> menuType(MenuFactory<M> factory);

	void openMenu(ServerPlayer player, MenuProvider provider, Consumer<RegistryFriendlyByteBuf> extraData);

	/** A fake player of the level for commands run without an entity */
	ServerPlayer fakePlayer(ServerLevel level);

	interface MenuFactory<M extends AbstractContainerMenu> {
		M create(int id, Inventory inventory, @Nullable FriendlyByteBuf extraData);
	}
}
