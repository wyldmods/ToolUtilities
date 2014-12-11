package org.wyldmods.toolutilities.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

public class BrokenTool extends Item {

	public BrokenTool()
	{
		super();
		this.setMaxStackSize(1);
		this.setUnlocalizedName("brokenTool");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int renderPass)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null)
			return (IIcon) null;
		else
		{
			ItemStack oldStack = ItemStack.loadItemStackFromNBT(tag);
			return oldStack.getItem().getIcon(oldStack, renderPass);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconIndex(ItemStack stack)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null)
			return (IIcon) null;
		else
		{
			ItemStack oldStack = ItemStack.loadItemStackFromNBT(tag);
			return oldStack.getItem().getIconIndex(oldStack);
		}
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
			return StatCollector.translateToLocal("brokentool.null");
		else
		{
			ItemStack oldStack = ItemStack.loadItemStackFromNBT(tag);
			return StatCollector.translateToLocal("broken")+" "+StatCollector.translateToLocal(oldStack.getItem().getUnlocalizedName(oldStack)+".name");
		}
	}


}
