package org.wyldmods.toolutilities.common.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;
import org.wyldmods.toolutilities.ToolUtilities;
import org.wyldmods.toolutilities.common.recipe.ToolUpgrade;
import org.wyldmods.toolutilities.common.recipe.ToolUpgradeRecipe;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
    @SideOnly(Side.CLIENT) // hacky, should have it's own class
    public void handleTooltipEvent(ItemTooltipEvent event)
    {
        // No possible upgrades? Moving on.
        if (getPossibleUpgrades(event.itemStack).length + ToolUpgrade.getUpgradesOn(event.itemStack).length > 0)
        {
            // the lines to inject into the beginning of the tooltip
            List<String> lines = new ArrayList<String>();
            
            // do nothing if either shift key is not down
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            {
                // check if control is down, if it is, continue, if not, add a tooltip for it
                boolean isControlDown = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
                if (!isControlDown && getPossibleUpgrades(event.itemStack).length != 0)
                {
                    lines.add(String.format(EnumChatFormatting.WHITE + locTT("pressControl"), EnumChatFormatting.AQUA + "-" + EnumChatFormatting.ITALIC, "-"
                            + EnumChatFormatting.WHITE));
                }

                // gather the upgrades on the tool and apply them to the tooltip
                ToolUpgrade[] upgradesOn = ToolUpgrade.getUpgradesOn(event.itemStack);
                if (upgradesOn.length > 0)
                {
                    lines.add(EnumChatFormatting.YELLOW + locTT("upgradesOn"));
                    for (ToolUpgrade upgrade : upgradesOn)
                    {
                        lines.add(EnumChatFormatting.WHITE + "  - " + upgrade.getTooltip());
                    }
                }

                // gather the upgrades that can go on the tool and apply them to the tooltip
                ToolUpgrade[] upgradesPossible = getPossibleUpgrades(event.itemStack);
                if (upgradesPossible.length > 0)
                {
                    lines.add(EnumChatFormatting.YELLOW + locTT("upgradesPossible"));
                    for (ToolUpgrade upgrade : upgradesPossible)
                    {
                        lines.add(EnumChatFormatting.WHITE + "  - " + upgrade.getTooltip());
                        
                        // if control is down, add some info about the required item to add this upgrade
                        if (isControlDown)
                        {
                            lines.add(EnumChatFormatting.WHITE  + "      " + EnumChatFormatting.ITALIC + locTT("requiredItem") + EnumChatFormatting.YELLOW + " "
                                    + ToolUpgradeRecipe.getModifierFor(event.itemStack, upgrade).getDisplayName());
                        }
                    }
                    
                    lines.add("  " + EnumChatFormatting.WHITE + EnumChatFormatting.ITALIC + locTT("upgradeInstructions"));
                }
            }
            else
            {
                lines.add(String.format(EnumChatFormatting.WHITE + locTT("pressShift"), EnumChatFormatting.AQUA + "-" + EnumChatFormatting.ITALIC, "-"
                        + EnumChatFormatting.WHITE));
            }

            event.toolTip.addAll(1, lines);
        }
    }

    private ToolUpgrade[] getPossibleUpgrades(ItemStack stack)
    {
        ToolUpgrade[] upgrades = new ToolUpgrade[0];
        for (ToolUpgrade upgrade : ToolUpgrade.values())
        {
            if (upgrade.hasPreReqs(stack) && !upgrade.isOn(stack) && ToolUpgradeRecipe.recipeExistsFor(stack, upgrade) && !Arrays.asList(upgrade.getToolBlacklist()).contains(stack.getItem().getUnlocalizedName()))
            {
                upgrades = ArrayUtils.add(upgrades, upgrade);
            }
        }
        return upgrades;
    }

    private String locTT(String unloc)
    {
        return StatCollector.translateToLocal(ToolUtilities.LOCALIZING + ".tooltip." + unloc);
    }
}
