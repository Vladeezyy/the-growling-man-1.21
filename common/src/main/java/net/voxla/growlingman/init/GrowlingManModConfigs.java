package net.voxla.growlingman.init;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.voxla.growlingman.configuration.GrowlingManConfigConfiguration;

@EventBusSubscriber(modid = "growling_man", bus = Bus.MOD)
public class GrowlingManModConfigs {
   @SubscribeEvent
   public static void register(FMLConstructModEvent event) {
      event.enqueueWork(() -> ModLoadingContext.get().registerConfig(Type.COMMON, GrowlingManConfigConfiguration.SPEC, "growling_man_config.toml"));
   }
}
