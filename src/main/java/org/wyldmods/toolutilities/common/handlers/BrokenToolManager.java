package org.wyldmods.toolutilities.common.handlers;

import java.util.Iterator;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import org.wyldmods.toolutilities.ToolUtilities;
import org.wyldmods.toolutilities.common.recipe.ToolUpgrade;
import org.wyldmods.toolutilities.common.recipe.ToolUpgradeRecipe;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class BrokenToolManager {

	@SubscribeEvent
	public void handleAnvilEvent(AnvilUpdateEvent event)
	{
		if (event.left != null && event.left.getItem()==ToolUtilities.brokenTool)
		{
			ItemStack repairedTool = ItemStack.loadItemStackFromNBT(event.left.getTagCompound());

			event.cost=repairedTool.getRepairCost() + event.right.getRepairCost();
			System.out.println(event.cost);
			if (repairedTool.getItem().getIsRepairable(repairedTool, event.right))
			{
				ItemStack repairStack = repairedTool.copy();
				Map map = EnchantmentHelper.getEnchantments(repairStack);
				int k = Math.min(repairedTool.getItemDamageForDisplay(), repairedTool.getMaxDamage() / 4);
				int i=0;
				for (int l = 0; k > 0 && l < event.right.stackSize; ++l)
				{
					int i1 = repairedTool.getItemDamageForDisplay() - k;
					repairStack.setItemDamage(i1);
					i += Math.max(1, k / 100) + map.size();
					k = Math.min(repairStack.getItemDamageForDisplay(), repairStack.getMaxDamage() / 4);
				}
				
				//Guess who's code this is (Hint: Not mine)
				int k1=0;
				int l1=0;
				int k2=0;
				k=0;
				for (Iterator iterator1 = map.keySet().iterator(); iterator1.hasNext(); k2 += k + k1 * l1)
				{
					int i1 = ((Integer)iterator1.next()).intValue();
					Enchantment enchantment = Enchantment.enchantmentsList[i1];
					k1 = ((Integer)map.get(Integer.valueOf(i1))).intValue();
					l1 = 0;
					++k;

					switch (enchantment.getWeight())
					{
					case 1:
						l1 = 8;
						break;
					case 2:
						l1 = 4;
					case 3:
					case 4:
					case 6:
					case 7:
					case 8:
					case 9:
					default:
						break;
					case 5:
						l1 = 2;
						break;
					case 10:
						l1 = 1;
					}
				}

				event.cost = i+k2;
				event.output=repairStack;				
			}
		}
	}


	@SubscribeEvent
	public void brokenToolSpawn(BreakEvent event)
	{
		ItemStack currentStack = event.getPlayer().getCurrentEquippedItem();
		if (currentStack != null && ToolUpgrade.hasUpgrade(currentStack, ToolUpgrade.UNBREAKABLE))
		{
			ItemStack brokenStack = new ItemStack(ToolUtilities.brokenTool);
			NBTTagCompound oldTool = currentStack.writeToNBT(new NBTTagCompound());
			brokenStack.setTagCompound(oldTool);
			event.getPlayer().inventory.addItemStackToInventory(brokenStack);
		}
	}
}
