package org.wyldmods.toolutilities.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class NBTHelper
{
    public static NBTTagCompound getTag(ItemStack stack)
    {
        if (stack.stackTagCompound == null)
        {
            stack.stackTagCompound = new NBTTagCompound();
        }
        
        return stack.stackTagCompound;
    }
}
