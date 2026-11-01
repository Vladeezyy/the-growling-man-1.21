package net.voxla.growlingman.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.voxla.growlingman.GrowlingManMod;
import net.voxla.growlingman.port.platform.Platform;
import net.voxla.growlingman.procedures.HoldBreathKeyOnKeyPressedProcedure;
import net.voxla.growlingman.procedures.HoldBreathKeyOnKeyReleasedProcedure;

public record ClearMindKeyMessage(int type, int pressedms) implements CustomPacketPayload {
	public static final Type<ClearMindKeyMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GrowlingManMod.MODID, "key_clear_mind"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ClearMindKeyMessage> STREAM_CODEC = StreamCodec.of(
		(RegistryFriendlyByteBuf buffer, ClearMindKeyMessage message) -> {
			buffer.writeInt(message.type);
			buffer.writeInt(message.pressedms);
		},
		(RegistryFriendlyByteBuf buffer) -> new ClearMindKeyMessage(buffer.readInt(), buffer.readInt())
	);

	@Override
	public Type<ClearMindKeyMessage> type() {
		return TYPE;
	}

	public static void handleData(ClearMindKeyMessage message, ServerPlayer player) {
		pressAction(player, message.type, message.pressedms);
	}

	public static void pressAction(Player entity, int type, int pressedms) {
		Level world = entity.level();
		if (world.hasChunkAt(entity.blockPosition())) {
			if (type == 0) {
				HoldBreathKeyOnKeyPressedProcedure.execute(entity);
			}

			if (type == 1) {
				HoldBreathKeyOnKeyReleasedProcedure.execute(entity);
			}
		}
	}

	/** Called once at init */
	public static void register() {
		Platform.INSTANCE.registerServerbound(TYPE, STREAM_CODEC, ClearMindKeyMessage::handleData);
	}
}
