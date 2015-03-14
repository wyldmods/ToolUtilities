package org.wyldmods.toolutilities.common.compat;

import mods.railcraft.common.items.ItemCrowbar;

import org.wyldmods.toolutilities.ToolUtilities;
import org.wyldmods.toolutilities.common.Config;
import org.wyldmods.toolutilities.common.recipe.ToolUpgrade;
import org.wyldmods.toolutilities.common.recipe.ToolUpgradeRecipe;

public class RailcraftCompat {

    public static void addRailcraftRecipes()
    {
        ToolUpgradeRecipe.addUpgradeRecipe(ItemCrowbar.class, ToolUtilities.swordAreaItem, ToolUpgrade.SWORD_AOE, Config.swordAreaXP, Config.allowSwordAOE);
    }
}
