package org.wyldmods.toolutilities.common.compat;

import static org.wyldmods.toolutilities.common.Config.allow3x1Axe;
import static org.wyldmods.toolutilities.common.Config.allow3x1Pick;
import static org.wyldmods.toolutilities.common.Config.allow3x1Shovel;
import static org.wyldmods.toolutilities.common.Config.allow3x3Hoe;
import static org.wyldmods.toolutilities.common.Config.allow3x3Pick;
import static org.wyldmods.toolutilities.common.Config.allow3x3Shovel;
import static org.wyldmods.toolutilities.common.Config.areaXPAmount;
import static org.wyldmods.toolutilities.common.Config.hoeAreaXP;
import static org.wyldmods.toolutilities.common.Config.nineXPAmount;

import java.util.Map;

import mekanism.tools.item.ItemMekanismAxe;
import mekanism.tools.item.ItemMekanismHoe;
import mekanism.tools.item.ItemMekanismPaxel;
import mekanism.tools.item.ItemMekanismPickaxe;
import mekanism.tools.item.ItemMekanismShovel;
import net.minecraft.item.ItemTool;

import org.wyldmods.toolutilities.ToolUtilities;
import org.wyldmods.toolutilities.common.Config;
import org.wyldmods.toolutilities.common.recipe.ToolUpgrade;
import org.wyldmods.toolutilities.common.recipe.ToolUpgradeRecipe;

public class MekanismCompat {
	
	public static void addMekanismRecipes()
	{
		//Mek Pickaxes
		ToolUpgradeRecipe.addUpgradeRecipe(ItemMekanismPickaxe.class, ToolUtilities.areaItem, ToolUpgrade.THREExONE, areaXPAmount, allow3x1Pick);
		ToolUpgradeRecipe.addUpgradeRecipe(ItemMekanismPickaxe.class, ToolUtilities.nineItem, ToolUpgrade.THREExTHREE, nineXPAmount, allow3x3Pick);
		
		//Mek Axes
		ToolUpgradeRecipe.addUpgradeRecipe(ItemMekanismAxe.class, ToolUtilities.areaItem, ToolUpgrade.THREExONE, areaXPAmount, allow3x1Axe);
		
		//Mek Shovels
		ToolUpgradeRecipe.addUpgradeRecipe(ItemMekanismShovel.class, ToolUtilities.areaItem, ToolUpgrade.THREExONE, areaXPAmount, allow3x1Shovel);
		ToolUpgradeRecipe.addUpgradeRecipe(ItemMekanismShovel.class, ToolUtilities.nineItem, ToolUpgrade.THREExTHREE, nineXPAmount, allow3x3Shovel);
		
		//And hoes
		ToolUpgradeRecipe.addUpgradeRecipe(ItemMekanismHoe.class, ToolUtilities.hoeAreaItem, ToolUpgrade.HOExTHREE, hoeAreaXP, allow3x3Hoe);
		
		//Paxels
		if (Config.allowPaxelUpgrades)
		{
			ToolUpgradeRecipe.addUpgradeRecipe(ItemMekanismPaxel.class, ToolUtilities.areaItem, ToolUpgrade.THREExONE, areaXPAmount, allow3x1Pick);
			ToolUpgradeRecipe.addUpgradeRecipe(ItemMekanismPaxel.class, ToolUtilities.nineItem, ToolUpgrade.THREExTHREE, nineXPAmount, allow3x3Pick);
		}
		
	}
	
	public static Map<Class<? extends ItemTool>, String> addMekanismToolToClasses(Map<Class<? extends ItemTool>, String> map)
	{
		map.put(ItemMekanismPickaxe.class, "pickaxe");
    	map.put(ItemMekanismAxe.class, "axe");
    	map.put(ItemMekanismShovel.class, "shovel");
    	
    	if (Config.allowPaxelUpgrades)
    	{
    		map.put(ItemMekanismPaxel.class, "paxel");
    	}
    	
    	return map;
	}

}
