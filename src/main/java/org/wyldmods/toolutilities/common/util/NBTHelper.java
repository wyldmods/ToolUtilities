package org.wyldmods.toolutilities.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class NBTHelper
{
    public static NBTTagCompound getTag(ItemStack stack)
    {
        if (stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        
        return stack.getTagCompound();
    }
}
