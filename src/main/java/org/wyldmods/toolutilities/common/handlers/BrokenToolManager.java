package org.wyldmods.toolutilities.common.handlers;

import java.util.Iterator;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.wyldmods.toolutilities.ToolUtilities;
import org.wyldmods.toolutilities.common.recipe.ToolUpgrade;

public class BrokenToolManager {

	@SubscribeEvent
	public void handleAnvilEvent(AnvilUpdateEvent event)
	{
		if (event.getLeft() != null && event.getLeft().getItem() == ToolUtilities.brokenTool)
		{
			ItemStack repairedTool = ItemStack.loadItemStackFromNBT(event.getLeft().getTagCompound());

			event.setCost(repairedTool.getRepairCost() + event.getRight().getRepairCost());
			System.out.println(event.getCost());
			if (repairedTool.getItem().getIsRepairable(repairedTool, event.getRight()))
			{
				ItemStack repairStack = repairedTool.copy();
				Map map = EnchantmentHelper.getEnchantments(repairStack);
				int k = Math.min(repairedTool.getItemDamage(), repairedTool.getMaxDamage() / 4);
				int i=0;
				for (int l = 0; k > 0 && l < event.getRight().stackSize; ++l)
				{
					int i1 = repairedTool.getItemDamage() - k;
					repairStack.setItemDamage(i1);
					i += Math.max(1, k / 100) + map.size();
					k = Math.min(repairStack.getItemDamage(), repairStack.getMaxDamage() / 4);
				}
				
				/* TODO
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
				*/
				event.setOutput(repairStack);				
			}
		}
	}


	@SubscribeEvent
	public void brokenToolSpawn(BreakEvent event)
	{
		ItemStack currentStack = event.getPlayer().getHeldItemMainhand();
		if (currentStack != null && ToolUpgrade.hasUpgrade(currentStack, ToolUpgrade.UNBREAKABLE))
		{
			ItemStack brokenStack = new ItemStack(ToolUtilities.brokenTool);
			NBTTagCompound oldTool = currentStack.writeToNBT(new NBTTagCompound());
			brokenStack.setTagCompound(oldTool);
			event.getPlayer().inventory.addItemStackToInventory(brokenStack);
		}
	}
}
