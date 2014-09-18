package org.wyldmods.toolutilities.common;

import java.io.File;

import org.wyldmods.toolutilities.ToolUtilities;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

public class Config
{
    public static final String sectionRightClick = "RightClick";
    public static final String sectionAreaMining = "AreaMining";
    public static final String sectionHoeArea = "HoeArea";
    public static final String sectionFeatures = "FeaturesAllowed";

    public static int XPAmount;
    public static String rightClickItem;
    public static boolean enableRightClick;

    public static int areaXPAmount;
    public static int nineXPAmount;
    public static String areaItem;
    public static String nineItem;
    public static boolean enableColumn;
    public static boolean enableNine;
    public static boolean noisyBlocks;

    public static String blacklist;

    public static String hoeAreaItem;
    public static int hoeAreaXP;

    public static boolean allowPlace;
    public static boolean allow3x1Pick, allow3x1Shovel, allow3x1Axe;
    public static boolean allow3x3Pick, allow3x3Shovel;
    public static boolean allow3x3Hoe;

    public static Configuration config;

    private static Config INSTANCE;
    public static Config instance() 
    { 
        if (INSTANCE == null)
        { 
            throw new IllegalStateException("Config must be initialized");
        }
        return INSTANCE;
    }
    
    private Config(File file)
    {
        config = new Configuration(file);
        syncConfig();
    }
    
    public static Config init(File file)
    {
        if (INSTANCE != null)
        {
            throw new IllegalStateException("Config already initialized");
        }
        
        INSTANCE = new Config(file);
        return INSTANCE;
    }

    public void syncConfig()
    {
        XPAmount = config.get(sectionRightClick, "XPRequired", 10, "Number of levels required for Right Click Placing").getInt();
        rightClickItem = config.get(sectionRightClick, "Item", "minecraft:torch", "Item required for Right Click Placing").getString();
        enableRightClick = config.get(sectionRightClick, "Enable", true, "Enable Right Click Placing").getBoolean();

        blacklist = config.get(sectionRightClick, "blacklist", "", "Blacklisted items for Right Click Placing").getString();

        areaXPAmount = config.get(sectionAreaMining, "areaXPRequired", 30, "Number of levels required for 1x3 Mining").getInt();
        nineXPAmount = config.get(sectionAreaMining, "1x9XPRequired", 30, "Number of levels required for 3x3 Mining").getInt();
        areaItem = config.get(sectionAreaMining, "1x3Item", "minecraft:coal", "Item required for 1x3 AoE Mining").getString();
        nineItem = config.get(sectionAreaMining, "3x3Item", "minecraft:coal", "Item required for 3x3 AoE Mining").getString();
        enableColumn = config.get(sectionAreaMining, "Enable1x3", true, "Enable 1x3 mining upgrade").getBoolean();
        enableNine = config.get(sectionAreaMining, "Enable3x3", true, "Enable 3x3 mining ugprade").getBoolean();
        noisyBlocks = config.get(sectionAreaMining, "noisyBlock", true, "When AOE blocks are broken, should they play a sound and make particles").getBoolean();

        hoeAreaXP = config.get(sectionHoeArea, "hoeAreaXPRequired", 10, "Number of levels required for Hoe to cut grass in 3x3").getInt();
        hoeAreaItem = config.get(sectionHoeArea, "hoeAreaItem", "minecraft:wheat_seeds", "Item required for Hoe to cut grass in a 3x3").getString();

        allowPlace = config.get(sectionFeatures, "allowPlace", true).getBoolean();
        allow3x1Pick = config.get(sectionFeatures, "allow3x1Pick", true).getBoolean();
        allow3x1Shovel = config.get(sectionFeatures, "allow3x1Shovel", true).getBoolean();
        allow3x1Axe = config.get(sectionFeatures, "allow3x1Axe", true).getBoolean();
        allow3x3Pick = config.get(sectionFeatures, "allow3x3Pick", true).getBoolean();
        allow3x3Shovel = config.get(sectionFeatures, "allow3x3Shovel", true).getBoolean();
        allow3x3Hoe = config.get(sectionFeatures, "allow3x3Hoe", true).getBoolean();

        if (config.hasChanged())
        {
            config.save();
            ToolUtilities.instance.doConfig();
        }
    }
    
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.modID.equals(ToolUtilities.MODID))
        {
            syncConfig();
        }
    }
}
