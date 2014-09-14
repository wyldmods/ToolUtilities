package com.insane.toolutilities;

import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class UpgradeToolManager {


	@SubscribeEvent
	public void handleAnvilEvent(AnvilUpdateEvent event) {
		if (event.left == null || event.right == null) {
			return;
		}

		if (event.left.getItem() instanceof ItemTool)
		{
			//Right Click Ability
			if (ItemStack.areItemStacksEqual(event.right, ToolUtilities.rightClickItem) && Config.enableRightClick)
			{
				ItemStack result = new ItemStack(event.left.getItem(),1, event.left.getItemDamage());
				if (event.left.stackTagCompound != null) {
					result.stackTagCompound = (NBTTagCompound) event.left.stackTagCompound.copy();
				}
				else {
					result.stackTagCompound = new NBTTagCompound();
				}

				if (event.left.stackTagCompound != null 
						&& event.left.stackTagCompound.getBoolean(ToolUtilities.MODID+"canPlace")) //Already enchanted!
				{
					return;
				}

				result.stackTagCompound.setBoolean(ToolUtilities.MODID+"canPlace", true);
				event.output = result;
				event.cost = Config.XPAmount;
				return;
			}
		}

		if (event.left.getItem() instanceof ItemPickaxe || event.left.getItem() instanceof ItemSpade)
		{
			//AoE Mining Section
			if (ItemStack.areItemStacksEqual(event.right, ToolUtilities.areaItem) && Config.enableColumn)
			{
				ItemStack result = new ItemStack(event.left.getItem(),1, event.left.getItemDamage());
				if (event.left.stackTagCompound != null) {
					result.stackTagCompound = (NBTTagCompound) event.left.stackTagCompound.copy();
				}
				else
				{
					result.stackTagCompound = new NBTTagCompound();
				}

				if (event.left.stackTagCompound!=null) //Potentially enchanted
				{
					if (event.left.stackTagCompound.getByte(ToolUtilities.MODID+"aoe")==0)
					{ //Not already AOE
						result.stackTagCompound.setByte(ToolUtilities.MODID+"aoe",(byte) 1);
						event.output = result;
						event.cost = Config.areaXPAmount;
						return;
					}
				}
				if (event.left.stackTagCompound==null)
				{
					result.stackTagCompound.setByte(ToolUtilities.MODID+"aoe",(byte) 1);
					event.output = result;
					event.cost = Config.areaXPAmount;
					return;
				}
			}
			if (ItemStack.areItemStacksEqual(event.right,ToolUtilities.nineItem) && Config.enableNine)
			{
				ItemStack result = new ItemStack(event.left.getItem(),1, event.left.getItemDamage());
				if (event.left.stackTagCompound != null) {
					result.stackTagCompound = (NBTTagCompound) event.left.stackTagCompound.copy();
				}
				else
				{
					result.stackTagCompound = new NBTTagCompound();
				}
				if (event.left.stackTagCompound != null && event.left.stackTagCompound.getByte(ToolUtilities.MODID+"aoe")==1) 
				{
					//Only 1x3, upgrade to 3x3
					result.stackTagCompound.setByte(ToolUtilities.MODID+"aoe",(byte) 2);
					event.output = result;
					event.cost = Config.nineXPAmount;
					return;
				}
				else
				{
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public void handleTooltipEvent(ItemTooltipEvent event) {

		if (event.itemStack != null && (event.itemStack.getItem() instanceof ItemTool)) {
			if (event.itemStack.hasTagCompound()) 
			{
				if (event.itemStack.stackTagCompound.getBoolean(ToolUtilities.MODID+"canPlace"))
				{
					event.toolTip.add("Can place items on right click.");
				}
				if (event.itemStack.stackTagCompound.getByte(ToolUtilities.MODID+"aoe")==1)
				{
					event.toolTip.add("Mines in a 1x3.");
				}
				if (event.itemStack.stackTagCompound.getByte(ToolUtilities.MODID+"aoe")==2)
				{
					event.toolTip.add("Mines in a 3x3.");
				}
			}
		}

		return;

	}

}
