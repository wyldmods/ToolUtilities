package org.wyldmods.toolutilities;

import static org.wyldmods.toolutilities.common.Config.*;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Logger;
import org.wyldmods.toolutilities.common.CommonProxy;
import org.wyldmods.toolutilities.common.Config;
import org.wyldmods.toolutilities.common.compat.MekanismCompat;
import org.wyldmods.toolutilities.common.compat.RailcraftCompat;
import org.wyldmods.toolutilities.common.handlers.AOEHandler;
import org.wyldmods.toolutilities.common.handlers.BrokenToolManager;
import org.wyldmods.toolutilities.common.handlers.PlaceItem;
import org.wyldmods.toolutilities.common.handlers.UpgradeToolManager;
import org.wyldmods.toolutilities.common.items.BrokenTool;
import org.wyldmods.toolutilities.common.recipe.ToolUpgrade;
import org.wyldmods.toolutilities.common.recipe.ToolUpgradeRecipe;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = ToolUtilities.MODID, name = ToolUtilities.MODID, version = "0.0.1", guiFactory = "org.wyldmods.toolutilities.client.config.TUConfigFactory", dependencies="after:MekanismTools;after:Railcraft")
public class ToolUtilities
{
	public static final String MODID = "ToolUtilities";
	public static final String LOCALIZING = "toolUtils";

	private Logger logger;

	@Mod.Instance(MODID)
	public static ToolUtilities instance;

	@SidedProxy(clientSide = "org.wyldmods.toolutilities.client.ClientProxy", serverSide = "org.wyldmods.toolutilities.common.CommonProxy")
	public static CommonProxy proxy;

	public static ItemStack rightClickItem;
	public static ItemStack areaItem;
	public static ItemStack nineItem;
	public static ItemStack hoeAreaItem;
	public static ItemStack swordAreaItem;
	public static ItemStack unbreakableItem;
	
	public static Item brokenTool;

	public static ArrayList<Item> blacklistedItems = new ArrayList<Item>();

	public static Config configHandler;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();

		configHandler = Config.init(event.getSuggestedConfigurationFile());
		FMLCommonHandler.instance().bus().register(configHandler);

		MinecraftForge.EVENT_BUS.register(new PlaceItem());
		MinecraftForge.EVENT_BUS.register(new UpgradeToolManager());
		MinecraftForge.EVENT_BUS.register(new AOEHandler());
		MinecraftForge.EVENT_BUS.register(new BrokenToolManager());
		
		brokenTool = new BrokenTool();
		GameRegistry.registerItem(brokenTool, "brokenTool");
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		doConfig();

		if (Loader.isModLoaded("MekanismTools") && Config.mekanismModule)
		{
			MekanismCompat.addMekanismRecipes();
		}
        if (Loader.isModLoaded("Railcraft") && Config.railcraftModule)
        {
            RailcraftCompat.addRailcraftRecipes();
        }
	}

	public void doConfig()
	{
		rightClickItem = getStackFromString(Config.rightClickItem);
		areaItem = getStackFromString(Config.areaItem);
		logger.info("Column item: " + areaItem.getUnlocalizedName());
		nineItem = getStackFromString(Config.nineItem);
		hoeAreaItem = getStackFromString(Config.hoeAreaItem);
		swordAreaItem = getStackFromString(Config.swordAreaItem);
		//unbreakableItem = getStackFromString(Config.unbreakableItem);

		String[] seperatedItems = blacklist.split(",");
		blacklistedItems.clear();
		for (int i = 0; i < seperatedItems.length; i++)
		{
			blacklistedItems.add(getStackFromString(seperatedItems[i]).getItem());
		}

		ToolUpgradeRecipe.clear();

		// place
		ToolUpgradeRecipe.addUpgradeRecipe(ItemTool.class, rightClickItem, ToolUpgrade.PLACE, XPAmount, allowPlace);

		// 3x1
		ToolUpgradeRecipe.addUpgradeRecipe(ItemPickaxe.class, areaItem, ToolUpgrade.THREExONE, areaXPAmount, allow3x1Pick);
		ToolUpgradeRecipe.addUpgradeRecipe(ItemSpade.class, areaItem, ToolUpgrade.THREExONE, areaXPAmount, allow3x1Shovel);
		ToolUpgradeRecipe.addUpgradeRecipe(ItemAxe.class, areaItem, ToolUpgrade.THREExONE, areaXPAmount, allow3x1Axe);

		// 3x3
		ToolUpgradeRecipe.addUpgradeRecipe(ItemPickaxe.class, nineItem, ToolUpgrade.THREExTHREE, nineXPAmount, allow3x3Pick);
		ToolUpgradeRecipe.addUpgradeRecipe(ItemSpade.class, nineItem, ToolUpgrade.THREExTHREE, nineXPAmount, allow3x3Shovel);

		//Hoe's in AoE for tall grass
		ToolUpgradeRecipe.addUpgradeRecipe(ItemHoe.class, hoeAreaItem, ToolUpgrade.HOExTHREE, hoeAreaXP, allow3x3Hoe);

		ToolUpgradeRecipe.addUpgradeRecipe(ItemSword.class, swordAreaItem, ToolUpgrade.SWORD_AOE, swordAreaXP, allowSwordAOE);
		
		//ToolUpgradeRecipe.addUpgradeRecipe(ItemTool.class, unbreakableItem, ToolUpgrade.UNBREAKABLE, unbreakableXP, allowUnbreakable);
		
		doBlacklist(Config.blacklistPlace,ToolUpgrade.PLACE);
		doBlacklist(Config.blacklist3x1,ToolUpgrade.THREExONE);
		doBlacklist(Config.blacklist3x3,ToolUpgrade.THREExTHREE);
		doBlacklist(Config.blacklistHoe,ToolUpgrade.HOExTHREE);
		doBlacklist(Config.blacklistSword,ToolUpgrade.SWORD_AOE);
	}

	private ItemStack getStackFromString(String input)
	{
		ItemStack outputStack;

		String[] info = input.split(":");
		if (info.length < 2)
		{
			logger.info("Issue with " + input + ". Format: modid:item:damage. Reverting to default.");
			outputStack = new ItemStack(Items.water_bucket, 1);
		}
		else
		{
			Item possible = GameRegistry.findItem(info[0], info[1]);
			if (possible == null)
			{
				// Try block instead
				Block possibleBlock = GameRegistry.findBlock(info[0], info[1]);
				if (possibleBlock == null)
				{
					logger.info("Issue(2) with " + input + ". Format: modid:item:damage. Reverting to default.");
					outputStack = new ItemStack(Items.water_bucket, 1);
				}
				else
				{
					if (info.length == 3)
					{
						outputStack = new ItemStack(possibleBlock, 1, Integer.parseInt(info[2]));
					}
					else
					{
						outputStack = new ItemStack(possibleBlock, 1);
					}
				}
			}
			else
			{
				if (info.length == 3)
				{
					outputStack = new ItemStack(possible, 1, Integer.parseInt(info[2]));
				}
				else
				{
					outputStack = new ItemStack(possible, 1);
				}
			}
		}
		return outputStack;
	}
	
	private void doBlacklist(String[] input, ToolUpgrade upgrade)
	{
		for (String s : input)
		{
			upgrade.addToolBlacklist(getStackFromString(s).getUnlocalizedName());
		}
	}
}
