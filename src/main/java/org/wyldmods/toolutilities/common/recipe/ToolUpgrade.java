package org.wyldmods.toolutilities.common.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.apache.commons.lang3.ArrayUtils;
import org.wyldmods.toolutilities.ToolUtilities;
import org.wyldmods.toolutilities.common.util.NBTHelper;

public enum ToolUpgrade
{
    PLACE, THREExONE(2), THREExTHREE(1), HOExTHREE, SWORD_AOE, UNBREAKABLE;
    
    static
    {
        THREExTHREE.addPreReq(THREExONE);
    }

    public final String nbtKey;
    private int[] prereqs;
    private int[] blacklist;
    private String tooltip;
    private String[] toolBlacklist;

    ToolUpgrade(int... blacklist)
    {
        this.blacklist = new int[0];
        this.prereqs = new int[0];
        this.toolBlacklist = new String[0];
        
        for (int i : blacklist)
        {
            addBlacklist(i);
        }

        this.nbtKey = ToolUtilities.MODID + ":" + name();
        this.tooltip = ToolUtilities.LOCALIZING + ".upgrade." + this.name().toLowerCase() + ".tooltip";
    }

    public void addPreReq(int index)
    {
        prereqs = ArrayUtils.add(prereqs, index);
    }

    public void addPreReq(ToolUpgrade upgrade)
    {
        addPreReq(upgrade.ordinal());
    }

    public void addBlacklist(int index)
    {
        blacklist = ArrayUtils.add(blacklist, index);
    }

    public void addBlacklist(ToolUpgrade upgrade)
    {
        addBlacklist(upgrade.ordinal());
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
        for (int i : this.blacklist)
        {
            values()[i].remove(stack);
        }

        NBTHelper.getTag(stack).setBoolean(nbtKey, true);
    }

    public void remove(ItemStack stack)
    {
        NBTHelper.getTag(stack).setBoolean(nbtKey, false);
    }

    public boolean isBlacklisted(ItemStack stack)
    {
        for (ToolUpgrade u : getUpgradesOn(stack))
        {
            for (int blacklisted : u.blacklist)
            {
                if (values()[blacklisted] == this)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasPreReqs(ItemStack stack)
    {
        for (int i : prereqs)
        {
            if (!values()[i].isOn(stack))
            {
                return false;
            }
        }
        return true;
    }

    public static boolean canApply(ItemStack stack, ToolUpgrade upgrade)
    {
        return !upgrade.isBlacklisted(stack) && upgrade.hasPreReqs(stack);
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
    
    public String[] getToolBlacklist()
    {
    	return this.toolBlacklist;
    }
    
    public void addToolBlacklist(String tool)
    {
    	this.toolBlacklist=ArrayUtils.add(this.toolBlacklist, tool);
    }
}
