package org.wyldmods.toolutilities.common.compat;

import static org.wyldmods.toolutilities.common.Config.swordAreaXP;
import static org.wyldmods.toolutilities.common.Config.allowSwordAOE;

import mods.railcraft.common.items.ItemCrowbar;

import org.wyldmods.toolutilities.ToolUtilities;
import org.wyldmods.toolutilities.common.recipe.ToolUpgrade;
import org.wyldmods.toolutilities.common.recipe.ToolUpgradeRecipe;

public class RailcraftCompat {

    public static void addRailcraftRecipes()
    {
        ToolUpgradeRecipe.addUpgradeRecipe(ItemCrowbar.class, ToolUtilities.swordAreaItem, ToolUpgrade.SWORD_AOE, swordAreaXP, allowSwordAOE);
    }
}
