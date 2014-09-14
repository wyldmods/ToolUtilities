package org.wyldmods.toolutilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = ToolUtilities.MODID, name = ToolUtilities.MODID, version="1.0")
public class ToolUtilities {
	
	public static final String MODID = "ToolUtilities";
	private Logger logger;
	
	@Mod.Instance(MODID)
	public static ToolUtilities instance;
	
	@SidedProxy(clientSide = "com.insane.toolutilities.client.ClientProxy", serverSide = "com.insane.toolutilities.CommonProxy")
	public static CommonProxy proxy;
	
	public static ItemStack rightClickItem;
	public static ItemStack areaItem;
	public static ItemStack nineItem;
	
	public static ArrayList<Item> blacklistedItems = new ArrayList<Item>();
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		
		File file = new File(event.getModConfigurationDirectory()+"/ToolUtilities.cfg");
		try 
		{
			file.createNewFile();
		} 
		catch(IOException e) 
		{
			logger.info("Unable to create configuration file for TorchUtilities");
		}
		
		Config.doConfig(file);
		
		MinecraftForge.EVENT_BUS.register(new PlaceItem());
		MinecraftForge.EVENT_BUS.register(new UpgradeToolManager());
		MinecraftForge.EVENT_BUS.register(new AOEMining());
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
		rightClickItem=getStackFromString(Config.rightClickItem);
		areaItem=getStackFromString(Config.areaItem);
		logger.info("Column item: "+areaItem.getUnlocalizedName());
		nineItem=getStackFromString(Config.nineItem);
		
		String[] seperatedItems = Config.blacklist.split(",");
		for (int i=0; i<seperatedItems.length; i++)
		{
			blacklistedItems.add(getStackFromString(seperatedItems[i]).getItem());
		}
		
		
	}
	
	private ItemStack getStackFromString(String input) {
		ItemStack outputStack;
		
		String[] info = input.split(":");
		if (info.length < 2) {
			logger.info("Issue with Right Click Item. Format: modid:item:damage. Reverting to default.");
			
			outputStack = new ItemStack(Items.water_bucket, 1);
		}
		else 
		{
			Item possible = GameRegistry.findItem(info[0],info[1]);
			if (possible == null) {
				//Try block instead
				Block possibleBlock = GameRegistry.findBlock(info[0],info[1]);
				if (possibleBlock == null) 
				{
					logger.info("Issue(2) with Config Item. Format: modid:item:damage. Reverting to default.");
					outputStack = new ItemStack(Items.water_bucket,1);
				}
				else
				{
					if (info.length == 3)
					{
						outputStack = new ItemStack(possibleBlock,1,Integer.parseInt(info[2]));
					}
					else
					{
						outputStack = new ItemStack(possibleBlock,1);
					}
				}
			}
			else
			{
				if (info.length == 3) 
				{
					outputStack = new ItemStack(possible,1,Integer.parseInt(info[2]));
				}
				else 
				{
					outputStack = new ItemStack(possible,1);
				}
			}
			
			if (outputStack == null) 
			{
				logger.info("Issue(3) with Config Item. Format: modid:item:damage. Reverting to default.");
				outputStack = new ItemStack(Items.water_bucket,1);
			}
		}
		return outputStack;
		
		
		
	}
	

}
