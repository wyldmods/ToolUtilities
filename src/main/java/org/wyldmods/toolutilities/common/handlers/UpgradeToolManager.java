package org.wyldmods.toolutilities.common.handlers;

import net.minecraft.item.ItemTool;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import org.wyldmods.toolutilities.common.ToolUpgrade;
import org.wyldmods.toolutilities.common.recipe.ToolUpgradeRecipe;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class UpgradeToolManager
{
    @SubscribeEvent
    public void handleAnvilEvent(AnvilUpdateEvent event)
    {
        if (ToolUpgradeRecipe.isValidInput(event.left, event.right))
        {
            ToolUpgradeRecipe recipe = ToolUpgradeRecipe.getOutputFor(event.left, event.right);
            if (recipe != null)
            {
                event.output = event.left.copy();
                recipe.upgrade.apply(event.output);
                event.cost = recipe.cost;
            }
        }
    }

    @SubscribeEvent
    public void handleTooltipEvent(ItemTooltipEvent event)
    {
        if (event.itemStack.getItem() instanceof ItemTool)
        {
            for (ToolUpgrade upgrade : ToolUpgrade.getUpgradesOn(event.itemStack))
            {
                event.toolTip.add(upgrade.getTooltip());
            }
        }
    }
}
