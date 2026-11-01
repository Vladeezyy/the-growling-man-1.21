package net.voxla.growlingman.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.voxla.growlingman.GrowlingManMod;
import net.voxla.growlingman.port.bus.EventPriority;
import net.voxla.growlingman.port.bus.GMBus;
import net.voxla.growlingman.port.bus.PlayerEvent;
import net.voxla.growlingman.port.platform.Platform;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The client tells the server when the player opens or closes their inventory (client/InventoryScreenWatcher). 1.0.1 read
 * {@code Minecraft.getInstance().screen} in the server's player tick: on a dedicated server that class does not exist
 * (NoClassDefFoundError), and in a LAN world every player was checked against the host's screen.
 */
public record InventoryScreenMessage(boolean open) implements CustomPacketPayload {
	public static final Type<InventoryScreenMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(GrowlingManMod.MODID, "inventory_screen"));
	public static final StreamCodec<RegistryFriendlyByteBuf, InventoryScreenMessage> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, InventoryScreenMessage::open, InventoryScreenMessage::new);
	private static final Set<UUID> OPEN = ConcurrentHashMap.newKeySet();

	@Override
	public Type<InventoryScreenMessage> type() {
		return TYPE;
	}

	/** Server side: whether the player has their inventory screen open */
	public static boolean isOpen(Entity entity) {
		return OPEN.contains(entity.getUUID());
	}

	public static void handleData(InventoryScreenMessage message, ServerPlayer player) {
		if (message.open)
			OPEN.add(player.getUUID());
		else
			OPEN.remove(player.getUUID());
	}

	private static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		OPEN.remove(event.getEntity().getUUID());
	}

	/** Called once at init */
	public static void register() {
		Platform.INSTANCE.registerServerbound(TYPE, STREAM_CODEC, InventoryScreenMessage::handleData);
		GMBus.add(PlayerEvent.PlayerLoggedInEvent.class, EventPriority.NORMAL, InventoryScreenMessage::onPlayerLoggedIn);
	}
}
