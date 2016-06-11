package org.wyldmods.toolutilities.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
public class BrokenTool extends Item {

	public BrokenTool()
	{
		super();
		this.setMaxStackSize(1);
		this.setUnlocalizedName("brokenTool");
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		return 1;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return true;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null)
			return I18n.translateToLocal("brokentool.null");
		else
		{
            ItemStack oldStack = ItemStack.loadItemStackFromNBT(tag);
            return I18n.translateToLocal("broken") + " " + I18n.translateToLocal(oldStack.getItem().getUnlocalizedName(oldStack) + ".name");
        }
	}
}
