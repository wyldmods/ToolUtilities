package org.wyldmods.toolutilities.common;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.apache.commons.lang3.ArrayUtils;
import org.wyldmods.toolutilities.ToolUtilities;
import org.wyldmods.toolutilities.common.util.NBTHelper;

public enum ToolUpgrade
{
    PLACE("canPlace"),
    THREExONE("aoe1", 2),
    THREExTHREE("aoe2", 1);
    
    public final String nbtKey;
    public final int[] blacklist;
    private final String tooltip;
    ToolUpgrade(String nbtKey, int... blacklist)
    {
        this.nbtKey = ToolUtilities.MODID + nbtKey;
        this.blacklist = blacklist;
        this.tooltip = ToolUtilities.LOCALIZING + ".tooltip.upgrade." + this.name().toLowerCase();
    }
    
    public String getTooltip()
    {
        return StatCollector.translateToLocal(tooltip);
    }
    
    public static boolean hasUpgrade(ItemStack stack, ToolUpgrade upgrade)
    {
        return stack.stackTagCompound != null && stack.stackTagCompound.getBoolean(upgrade.nbtKey);
    }
    
    public boolean isOn(ItemStack stack)
    {
        return hasUpgrade(stack, this);
    }
    
    public void apply(ItemStack stack)
    {
        if (!canApply(stack, this))
        {
            for (int i : this.blacklist)
            {
                values()[i].remove(stack);
            }
        }
        NBTHelper.getTag(stack).setBoolean(nbtKey, true);
    }
    
    public void remove(ItemStack stack)
    {
        NBTHelper.getTag(stack).setBoolean(nbtKey, false);
    }
    
    /**
     * If applying the upgrade to the passed stack would have any conflicts on the upgrade's blacklist
     */
    public static boolean canApply(ItemStack stack, ToolUpgrade upgrade)
    {
        for (ToolUpgrade u : values())
        {
            if (u.isOn(stack))
            {
                int[] blacklist = u.blacklist;
                for (int i : blacklist)
                {
                    if (i == upgrade.ordinal())
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }
    
    public static ToolUpgrade[] getUpgradesOn(ItemStack stack)
    {
        ToolUpgrade[] ret = new ToolUpgrade[0];
        for (ToolUpgrade upgrade : values())
        {
            if (upgrade.isOn(stack))
            {
                ret = ArrayUtils.add(ret, upgrade);
            }
        }
        return ret;
    }
}
