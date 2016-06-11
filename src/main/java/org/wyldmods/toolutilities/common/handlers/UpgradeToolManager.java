package org.wyldmods.toolutilities.common.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;
import org.wyldmods.toolutilities.ToolUtilities;
import org.wyldmods.toolutilities.common.recipe.ToolUpgrade;
import org.wyldmods.toolutilities.common.recipe.ToolUpgradeRecipe;

public class UpgradeToolManager
{
    @SubscribeEvent
    public void handleAnvilEvent(AnvilUpdateEvent event)
    {
        if (ToolUpgradeRecipe.isValidInput(event.getLeft(), event.getRight()))
        {
            ToolUpgradeRecipe recipe = ToolUpgradeRecipe.getOutputFor(event.getLeft(), event.getRight());
            if (recipe != null)
            {
                event.setOutput(event.getLeft().copy());
                recipe.upgrade.apply(event.getOutput());
                event.setCost(recipe.cost);
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT) // hacky, should have it's own class
    public void handleTooltipEvent(ItemTooltipEvent event)
    {
        // No possible upgrades? Moving on.
        if (getPossibleUpgrades(event.getItemStack()).length + ToolUpgrade.getUpgradesOn(event.getItemStack()).length > 0)
        {
            // the lines to inject into the beginning of the tooltip
            List<String> lines = new ArrayList<String>();
            
            // do nothing if either shift key is not down
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            {
                // check if control is down, if it is, continue, if not, add a tooltip for it
                boolean isControlDown = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
                if (!isControlDown && getPossibleUpgrades(event.getItemStack()).length != 0)
                {
                    lines.add(TextFormatting.WHITE + locTT("pressControl", TextFormatting.AQUA + "-" + TextFormatting.ITALIC, "-" + TextFormatting.WHITE));
                }

                // gather the upgrades on the tool and apply them to the tooltip
                ToolUpgrade[] upgradesOn = ToolUpgrade.getUpgradesOn(event.getItemStack());
                if (upgradesOn.length > 0)
                {
                    lines.add(TextFormatting.YELLOW + locTT("upgradesOn"));
                    for (ToolUpgrade upgrade : upgradesOn)
                    {
                        lines.add(TextFormatting.WHITE + "  - " + upgrade.getTooltip());
                    }
                }

                // gather the upgrades that can go on the tool and apply them to the tooltip
                ToolUpgrade[] upgradesPossible = getPossibleUpgrades(event.getItemStack());
                if (upgradesPossible.length > 0)
                {
                    lines.add(TextFormatting.YELLOW + locTT("upgradesPossible"));
                    for (ToolUpgrade upgrade : upgradesPossible)
                    {
                        lines.add(TextFormatting.WHITE + "  - " + upgrade.getTooltip());
                        
                        // if control is down, add some info about the required item to add this upgrade
                        if (isControlDown)
                        {
                            lines.add(TextFormatting.WHITE  + "      " + TextFormatting.ITALIC + locTT("requiredItem") + TextFormatting.YELLOW + " "
                                    + ToolUpgradeRecipe.getModifierFor(event.getItemStack(), upgrade).getDisplayName());
                        }
                    }
                    
                    lines.add("  " + TextFormatting.WHITE + TextFormatting.ITALIC + locTT("upgradeInstructions"));
                }
            }
            else
            {
                lines.add(TextFormatting.WHITE + locTT("pressShift", TextFormatting.AQUA + "-" + TextFormatting.ITALIC, "-" + TextFormatting.WHITE));
            }

            event.getToolTip().addAll(1, lines);
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

    private String locTT(String unloc, Object... args)
    {
        return I18n.format(ToolUtilities.LOCALIZING + ".tooltip." + unloc, args);
    }
}
