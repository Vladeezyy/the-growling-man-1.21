package net.voxla.growlingman.network;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent.Context;
import net.voxla.growlingman.GrowlingManMod;

@EventBusSubscriber(bus = Bus.MOD)
public class GrowlingManModVariables {
   public static final Capability<GrowlingManModVariables.PlayerVariables> PLAYER_VARIABLES_CAPABILITY = CapabilityManager.get(
      new CapabilityToken<GrowlingManModVariables.PlayerVariables>() {}
   );

   @SubscribeEvent
   public static void init(FMLCommonSetupEvent event) {
      GrowlingManMod.addNetworkMessage(
         GrowlingManModVariables.SavedDataSyncMessage.class,
         GrowlingManModVariables.SavedDataSyncMessage::buffer,
         GrowlingManModVariables.SavedDataSyncMessage::new,
         GrowlingManModVariables.SavedDataSyncMessage::handler
      );
      GrowlingManMod.addNetworkMessage(
         GrowlingManModVariables.PlayerVariablesSyncMessage.class,
         GrowlingManModVariables.PlayerVariablesSyncMessage::buffer,
         GrowlingManModVariables.PlayerVariablesSyncMessage::new,
         GrowlingManModVariables.PlayerVariablesSyncMessage::handler
      );
   }

   @SubscribeEvent
   public static void init(RegisterCapabilitiesEvent event) {
      event.register(GrowlingManModVariables.PlayerVariables.class);
   }

   @EventBusSubscriber
   public static class EventBusVariableHandlers {
      @SubscribeEvent
      public static void onPlayerLoggedInSyncPlayerVariables(PlayerLoggedInEvent event) {
         if (!event.getEntity().level().isClientSide()) {
            ((GrowlingManModVariables.PlayerVariables)event.getEntity()
                  .getCapability(GrowlingManModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                  .orElse(new GrowlingManModVariables.PlayerVariables()))
               .syncPlayerVariables(event.getEntity());
         }
      }

      @SubscribeEvent
      public static void onPlayerRespawnedSyncPlayerVariables(PlayerRespawnEvent event) {
         if (!event.getEntity().level().isClientSide()) {
            ((GrowlingManModVariables.PlayerVariables)event.getEntity()
                  .getCapability(GrowlingManModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                  .orElse(new GrowlingManModVariables.PlayerVariables()))
               .syncPlayerVariables(event.getEntity());
         }
      }

      @SubscribeEvent
      public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerChangedDimensionEvent event) {
         if (!event.getEntity().level().isClientSide()) {
            ((GrowlingManModVariables.PlayerVariables)event.getEntity()
                  .getCapability(GrowlingManModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                  .orElse(new GrowlingManModVariables.PlayerVariables()))
               .syncPlayerVariables(event.getEntity());
         }
      }

      @SubscribeEvent
      public static void clonePlayer(Clone event) {
         event.getOriginal().revive();
         GrowlingManModVariables.PlayerVariables original = (GrowlingManModVariables.PlayerVariables)event.getOriginal()
            .getCapability(GrowlingManModVariables.PLAYER_VARIABLES_CAPABILITY, null)
            .orElse(new GrowlingManModVariables.PlayerVariables());
         GrowlingManModVariables.PlayerVariables clone = (GrowlingManModVariables.PlayerVariables)event.getEntity()
            .getCapability(GrowlingManModVariables.PLAYER_VARIABLES_CAPABILITY, null)
            .orElse(new GrowlingManModVariables.PlayerVariables());
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
      }

      @SubscribeEvent
      public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
         if (!event.getEntity().level().isClientSide()) {
            SavedData mapdata = GrowlingManModVariables.MapVariables.get(event.getEntity().level());
            SavedData worlddata = GrowlingManModVariables.WorldVariables.get(event.getEntity().level());
            if (mapdata != null) {
               GrowlingManMod.PACKET_HANDLER
                  .send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)event.getEntity()), new GrowlingManModVariables.SavedDataSyncMessage(0, mapdata));
            }

            if (worlddata != null) {
               GrowlingManMod.PACKET_HANDLER
                  .send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)event.getEntity()), new GrowlingManModVariables.SavedDataSyncMessage(1, worlddata));
            }
         }
      }

      @SubscribeEvent
      public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
         if (!event.getEntity().level().isClientSide()) {
            SavedData worlddata = GrowlingManModVariables.WorldVariables.get(event.getEntity().level());
            if (worlddata != null) {
               GrowlingManMod.PACKET_HANDLER
                  .send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)event.getEntity()), new GrowlingManModVariables.SavedDataSyncMessage(1, worlddata));
            }
         }
      }
   }

   public static class MapVariables extends SavedData {
      public static final String DATA_NAME = "growling_man_mapvars";
      public double GMSpawnTimer = 0.0;
      public boolean gmdev_overlay = false;
      public double GMCanKillPlayerTimer = 0.0;
      static GrowlingManModVariables.MapVariables clientSide = new GrowlingManModVariables.MapVariables();

      public static GrowlingManModVariables.MapVariables load(CompoundTag tag) {
         GrowlingManModVariables.MapVariables data = new GrowlingManModVariables.MapVariables();
         data.read(tag);
         return data;
      }

      public void read(CompoundTag nbt) {
         this.GMSpawnTimer = nbt.getDouble("GMSpawnTimer");
         this.gmdev_overlay = nbt.getBoolean("gmdev_overlay");
         this.GMCanKillPlayerTimer = nbt.getDouble("GMCanKillPlayerTimer");
      }

      public CompoundTag save(CompoundTag nbt) {
         nbt.putDouble("GMSpawnTimer", this.GMSpawnTimer);
         nbt.putBoolean("gmdev_overlay", this.gmdev_overlay);
         nbt.putDouble("GMCanKillPlayerTimer", this.GMCanKillPlayerTimer);
         return nbt;
      }

      public void syncData(LevelAccessor world) {
         this.setDirty();
         if (world instanceof Level && !world.isClientSide()) {
            GrowlingManMod.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new GrowlingManModVariables.SavedDataSyncMessage(0, this));
         }
      }

      public static GrowlingManModVariables.MapVariables get(LevelAccessor world) {
         return world instanceof ServerLevelAccessor serverLevelAcc
            ? (GrowlingManModVariables.MapVariables)serverLevelAcc.getLevel()
               .getServer()
               .getLevel(Level.OVERWORLD)
               .getDataStorage()
               .computeIfAbsent(e -> load(e), GrowlingManModVariables.MapVariables::new, "growling_man_mapvars")
            : clientSide;
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

      public void syncPlayerVariables(Entity entity) {
         if (entity instanceof ServerPlayer serverPlayer) {
            GrowlingManMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new GrowlingManModVariables.PlayerVariablesSyncMessage(this));
         }
      }

      public Tag writeNBT() {
         CompoundTag nbt = new CompoundTag();
         nbt.putDouble("GMEventsTimer", this.GMEventsTimer);
         nbt.putDouble("ScreenShakeTimer", this.ScreenShakeTimer);
         nbt.putDouble("DarkenOverlay", this.DarkenOverlay);
         nbt.putDouble("HoldingBreathTimer", this.HoldingBreathTimer);
         nbt.putBoolean("IsHoldingBreath", this.IsHoldingBreath);
         nbt.putDouble("IsMarked", this.IsMarked);
         nbt.putDouble("cym_overlay_opacity", this.cym_overlay_opacity);
         nbt.putDouble("mtl_overlay_opacity", this.mtl_overlay_opacity);
         nbt.putDouble("gm_inv_opacity", this.gm_inv_opacity);
         nbt.putDouble("AnimalsKilled", this.AnimalsKilled);
         nbt.putDouble("AnimalsKilledResetWait", this.AnimalsKilledResetWait);
         nbt.putDouble("BadActionTimer", this.BadActionTimer);
         nbt.putDouble("RaidTime", this.RaidTime);
         nbt.putDouble("gm_inv_ap", this.gm_inv_ap);
         nbt.putDouble("InvNumb", this.InvNumb);
         nbt.putDouble("AnimalsKilledShortTime", this.AnimalsKilledShortTime);
         return nbt;
      }

      public void readNBT(Tag tag) {
         CompoundTag nbt = (CompoundTag)tag;
         this.GMEventsTimer = nbt.getDouble("GMEventsTimer");
         this.ScreenShakeTimer = nbt.getDouble("ScreenShakeTimer");
         this.DarkenOverlay = nbt.getDouble("DarkenOverlay");
         this.HoldingBreathTimer = nbt.getDouble("HoldingBreathTimer");
         this.IsHoldingBreath = nbt.getBoolean("IsHoldingBreath");
         this.IsMarked = nbt.getDouble("IsMarked");
         this.cym_overlay_opacity = nbt.getDouble("cym_overlay_opacity");
         this.mtl_overlay_opacity = nbt.getDouble("mtl_overlay_opacity");
         this.gm_inv_opacity = nbt.getDouble("gm_inv_opacity");
         this.AnimalsKilled = nbt.getDouble("AnimalsKilled");
         this.AnimalsKilledResetWait = nbt.getDouble("AnimalsKilledResetWait");
         this.BadActionTimer = nbt.getDouble("BadActionTimer");
         this.RaidTime = nbt.getDouble("RaidTime");
         this.gm_inv_ap = nbt.getDouble("gm_inv_ap");
         this.InvNumb = nbt.getDouble("InvNumb");
         this.AnimalsKilledShortTime = nbt.getDouble("AnimalsKilledShortTime");
      }
   }

   @EventBusSubscriber
   private static class PlayerVariablesProvider implements ICapabilitySerializable<Tag> {
      private final GrowlingManModVariables.PlayerVariables playerVariables = new GrowlingManModVariables.PlayerVariables();
      private final LazyOptional<GrowlingManModVariables.PlayerVariables> instance = LazyOptional.of(() -> this.playerVariables);

      @SubscribeEvent
      public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
         if (event.getObject() instanceof Player && !(event.getObject() instanceof FakePlayer)) {
            event.addCapability(new ResourceLocation("growling_man", "player_variables"), new GrowlingManModVariables.PlayerVariablesProvider());
         }
      }

      public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
         return cap == GrowlingManModVariables.PLAYER_VARIABLES_CAPABILITY ? this.instance.cast() : LazyOptional.empty();
      }

      public Tag serializeNBT() {
         return this.playerVariables.writeNBT();
      }

      public void deserializeNBT(Tag nbt) {
         this.playerVariables.readNBT(nbt);
      }
   }

   public static class PlayerVariablesSyncMessage {
      private final GrowlingManModVariables.PlayerVariables data;

      public PlayerVariablesSyncMessage(FriendlyByteBuf buffer) {
         this.data = new GrowlingManModVariables.PlayerVariables();
         this.data.readNBT(buffer.readNbt());
      }

      public PlayerVariablesSyncMessage(GrowlingManModVariables.PlayerVariables data) {
         this.data = data;
      }

      public static void buffer(GrowlingManModVariables.PlayerVariablesSyncMessage message, FriendlyByteBuf buffer) {
         buffer.writeNbt((CompoundTag)message.data.writeNBT());
      }

      public static void handler(GrowlingManModVariables.PlayerVariablesSyncMessage message, Supplier<Context> contextSupplier) {
         Context context = contextSupplier.get();
         context.enqueueWork(
            () -> {
               if (!context.getDirection().getReceptionSide().isServer()) {
                  GrowlingManModVariables.PlayerVariables variables = (GrowlingManModVariables.PlayerVariables)Minecraft.getInstance()
                     .player
                     .getCapability(GrowlingManModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                     .orElse(new GrowlingManModVariables.PlayerVariables());
                  variables.GMEventsTimer = message.data.GMEventsTimer;
                  variables.ScreenShakeTimer = message.data.ScreenShakeTimer;
                  variables.DarkenOverlay = message.data.DarkenOverlay;
                  variables.HoldingBreathTimer = message.data.HoldingBreathTimer;
                  variables.IsHoldingBreath = message.data.IsHoldingBreath;
                  variables.IsMarked = message.data.IsMarked;
                  variables.cym_overlay_opacity = message.data.cym_overlay_opacity;
                  variables.mtl_overlay_opacity = message.data.mtl_overlay_opacity;
                  variables.gm_inv_opacity = message.data.gm_inv_opacity;
                  variables.AnimalsKilled = message.data.AnimalsKilled;
                  variables.AnimalsKilledResetWait = message.data.AnimalsKilledResetWait;
                  variables.BadActionTimer = message.data.BadActionTimer;
                  variables.RaidTime = message.data.RaidTime;
                  variables.gm_inv_ap = message.data.gm_inv_ap;
                  variables.InvNumb = message.data.InvNumb;
                  variables.AnimalsKilledShortTime = message.data.AnimalsKilledShortTime;
               }
            }
         );
         context.setPacketHandled(true);
      }
   }

   public static class SavedDataSyncMessage {
      private final int type;
      private SavedData data;

      public SavedDataSyncMessage(FriendlyByteBuf buffer) {
         this.type = buffer.readInt();
         CompoundTag nbt = buffer.readNbt();
         if (nbt != null) {
            this.data = (SavedData)(this.type == 0 ? new GrowlingManModVariables.MapVariables() : new GrowlingManModVariables.WorldVariables());
            if (this.data instanceof GrowlingManModVariables.MapVariables mapVariables) {
               mapVariables.read(nbt);
            } else if (this.data instanceof GrowlingManModVariables.WorldVariables worldVariables) {
               worldVariables.read(nbt);
            }
         }
      }

      public SavedDataSyncMessage(int type, SavedData data) {
         this.type = type;
         this.data = data;
      }

      public static void buffer(GrowlingManModVariables.SavedDataSyncMessage message, FriendlyByteBuf buffer) {
         buffer.writeInt(message.type);
         if (message.data != null) {
            buffer.writeNbt(message.data.save(new CompoundTag()));
         }
      }

      public static void handler(GrowlingManModVariables.SavedDataSyncMessage message, Supplier<Context> contextSupplier) {
         Context context = contextSupplier.get();
         context.enqueueWork(() -> {
            if (!context.getDirection().getReceptionSide().isServer() && message.data != null) {
               if (message.type == 0) {
                  GrowlingManModVariables.MapVariables.clientSide = (GrowlingManModVariables.MapVariables)message.data;
               } else {
                  GrowlingManModVariables.WorldVariables.clientSide = (GrowlingManModVariables.WorldVariables)message.data;
               }
            }
         });
         context.setPacketHandled(true);
      }
   }

   public static class WorldVariables extends SavedData {
      public static final String DATA_NAME = "growling_man_worldvars";
      public boolean HasOneDayPassed = false;
      public boolean IsDaytime = false;
      public double GMSpawnChance = 0.0;
      static GrowlingManModVariables.WorldVariables clientSide = new GrowlingManModVariables.WorldVariables();

      public static GrowlingManModVariables.WorldVariables load(CompoundTag tag) {
         GrowlingManModVariables.WorldVariables data = new GrowlingManModVariables.WorldVariables();
         data.read(tag);
         return data;
      }

      public void read(CompoundTag nbt) {
         this.HasOneDayPassed = nbt.getBoolean("HasOneDayPassed");
         this.IsDaytime = nbt.getBoolean("IsDaytime");
         this.GMSpawnChance = nbt.getDouble("GMSpawnChance");
      }

      public CompoundTag save(CompoundTag nbt) {
         nbt.putBoolean("HasOneDayPassed", this.HasOneDayPassed);
         nbt.putBoolean("IsDaytime", this.IsDaytime);
         nbt.putDouble("GMSpawnChance", this.GMSpawnChance);
         return nbt;
      }

      public void syncData(LevelAccessor world) {
         this.setDirty();
         if (world instanceof Level level && !level.isClientSide()) {
            GrowlingManMod.PACKET_HANDLER.send(PacketDistributor.DIMENSION.with(level::dimension), new GrowlingManModVariables.SavedDataSyncMessage(1, this));
         }
      }

      public static GrowlingManModVariables.WorldVariables get(LevelAccessor world) {
         return world instanceof ServerLevel level
            ? (GrowlingManModVariables.WorldVariables)level.getDataStorage()
               .computeIfAbsent(e -> load(e), GrowlingManModVariables.WorldVariables::new, "growling_man_worldvars")
            : clientSide;
      }
   }
}
