package org.wyldmods.toolutilities;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Logger;
import org.wyldmods.toolutilities.common.CommonProxy;
import org.wyldmods.toolutilities.common.Config;
import org.wyldmods.toolutilities.common.ToolUpgrade;
import org.wyldmods.toolutilities.common.handlers.AOEMining;
import org.wyldmods.toolutilities.common.handlers.PlaceItem;
import org.wyldmods.toolutilities.common.handlers.UpgradeToolManager;
import org.wyldmods.toolutilities.common.recipe.ToolUpgradeRecipe;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = ToolUtilities.MODID, name = ToolUtilities.MODID, version = "0.0.1")
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

    public static ArrayList<Item> blacklistedItems = new ArrayList<Item>();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        Config.doConfig(event.getSuggestedConfigurationFile());

        MinecraftForge.EVENT_BUS.register(new PlaceItem());
        MinecraftForge.EVENT_BUS.register(new UpgradeToolManager());
        MinecraftForge.EVENT_BUS.register(new AOEMining());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {

        rightClickItem = getStackFromString(Config.rightClickItem);
        areaItem = getStackFromString(Config.areaItem);
        logger.info("Column item: " + areaItem.getUnlocalizedName());
        nineItem = getStackFromString(Config.nineItem);

        String[] seperatedItems = Config.blacklist.split(",");
        for (int i = 0; i < seperatedItems.length; i++)
        {
            blacklistedItems.add(getStackFromString(seperatedItems[i]).getItem());
        }

        // place
        ToolUpgradeRecipe.addUpgradeRecipe(ItemTool.class, rightClickItem, ToolUpgrade.PLACE, Config.XPAmount);

        // 3x1
        ToolUpgradeRecipe.addUpgradeRecipe(ItemPickaxe.class, areaItem, ToolUpgrade.THREExONE, Config.areaXPAmount);
        ToolUpgradeRecipe.addUpgradeRecipe(ItemSpade.class, areaItem, ToolUpgrade.THREExONE, Config.areaXPAmount);

        // 3x3
        ToolUpgradeRecipe.addUpgradeRecipe(ItemPickaxe.class, nineItem, ToolUpgrade.THREExTHREE, Config.nineXPAmount);
        ToolUpgradeRecipe.addUpgradeRecipe(ItemSpade.class, nineItem, ToolUpgrade.THREExTHREE, Config.nineXPAmount);
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
}
