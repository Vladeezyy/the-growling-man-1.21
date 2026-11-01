package net.voxla.growlingman.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.voxla.growlingman.port.bus.Dist;
import net.voxla.growlingman.port.bus.RenderGuiEvent;
import net.voxla.growlingman.port.bus.EventPriority;
import net.voxla.growlingman.port.bus.SubscribeEvent;
import net.voxla.growlingman.port.bus.EventBusSubscriber;
import net.voxla.growlingman.procedures.DevAnimalKillCooldownthingProcedure;
import net.voxla.growlingman.procedures.DevAnimalsKilledProcedure;
import net.voxla.growlingman.procedures.DevBadActTimeProcedure;
import net.voxla.growlingman.procedures.DevEventsTimerProcedure;
import net.voxla.growlingman.procedures.DevFirstDayPassedProcedure;
import net.voxla.growlingman.procedures.DevMarkedTimerProcedure;
import net.voxla.growlingman.procedures.DevSpawnChanceProcedure;
import net.voxla.growlingman.procedures.DevSpawnTimeProcedure;
import net.voxla.growlingman.procedures.GMDevOverlayDisplayOverlayIngameProcedure;
import net.voxla.growlingman.procedures.MindClearageProcedure;

@EventBusSubscriber(Dist.CLIENT)
public class GMDevOverlayOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getGuiGraphics().guiWidth();
		int h = event.getGuiGraphics().guiHeight();
		Level world = null;
		double x = 0.0;
		double y = 0.0;
		double z = 0.0;
		Player entity = Minecraft.getInstance().player;
		if (entity != null) {
			world = entity.level();
			x = entity.getX();
			y = entity.getY();
			z = entity.getZ();
		}

		if (GMDevOverlayDisplayOverlayIngameProcedure.execute(world)) {
			event.getGuiGraphics().drawString(Minecraft.getInstance().font, MindClearageProcedure.execute(entity), 6, 17, -16724839, false);
			event.getGuiGraphics().drawString(Minecraft.getInstance().font, DevSpawnTimeProcedure.execute(world), 6, 35, -65434, false);
			event.getGuiGraphics().drawString(Minecraft.getInstance().font, DevSpawnChanceProcedure.execute(world), 6, 53, -3407821, false);
			event.getGuiGraphics().drawString(Minecraft.getInstance().font, DevEventsTimerProcedure.execute(entity), 6, 71, -26368, false);
			event.getGuiGraphics().drawString(Minecraft.getInstance().font, DevMarkedTimerProcedure.execute(entity), 6, 89, -10079233, false);
			event.getGuiGraphics().drawString(Minecraft.getInstance().font, DevAnimalsKilledProcedure.execute(entity), 6, 107, -3355648, false);
			event.getGuiGraphics().drawString(Minecraft.getInstance().font, DevAnimalKillCooldownthingProcedure.execute(entity), 6, 125, -6697984, false);
			event.getGuiGraphics().drawString(Minecraft.getInstance().font, DevBadActTimeProcedure.execute(entity), 6, 143, -3407668, false);
			event.getGuiGraphics().drawString(Minecraft.getInstance().font, DevFirstDayPassedProcedure.execute(world), 6, 161, -3355444, false);
		}
	}
}
