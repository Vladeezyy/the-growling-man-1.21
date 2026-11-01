package net.voxla.growlingman.procedures;

import net.voxla.growlingman.init.GrowlingManModKeyMappings;

public class MindClearKeyLangRtnProcedure {
   public static String execute() {
      return "Hold [§c" + GrowlingManModKeyMappings.CLEAR_MIND_KEY.getTranslatedKeyMessage().getString().toUpperCase() + "§f] To Clear Your Mind";
   }
}
