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
    public static final String sectionSword = "SwordArea";
    public static final String sectionFeatures = "FeaturesAllowed";
    public static final String sectionModules = "Modules";
    public static final String sectionUnbreakable = "Unbreakable";
    public static final String sectionBlacklist = "Blacklist";

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
    
    public static String swordAreaItem;
    public static int swordAreaXP;

    public static float speedMult3x1, speedMult3x3;

    public static boolean allowPlace;
    public static boolean allow3x1Pick, allow3x1Shovel, allow3x1Axe;
    public static boolean allow3x3Pick, allow3x3Shovel;
    public static boolean allow3x3Hoe;
    public static boolean allowSwordAOE;
    public static boolean allowUnbreakable;
    
    public static int unbreakableXP;
    public static String unbreakableItem;
    
    public static boolean mekanismModule;
    public static boolean allowPaxelUpgrades;

    public static boolean railcraftModule;
    
    public static String[] blacklistPlace;
    public static String[] blacklist3x1;
    public static String[] blacklist3x3;
    public static String[] blacklistHoe;
    public static String[] blacklistSword;
    public static String[] blacklistUnbreakable;

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
        speedMult3x1 = config.getFloat("speedMult3x1", sectionAreaMining, 0.4f, 0.01f, 5.0f, "Speed multipler applied to 3x1 tools. To make them mine slower (or faster I guess)");
        speedMult3x3 = config.getFloat("speedMult3x3", sectionAreaMining, 0.2f, 0.01f, 5.0f, "Speed multipler applied to 3x3 tools. To make them mine slower (or faster I guess)");

        hoeAreaXP = config.get(sectionHoeArea, "hoeAreaXPRequired", 10, "Number of levels required for Hoe to cut grass in 3x3").getInt();
        hoeAreaItem = config.get(sectionHoeArea, "hoeAreaItem", "minecraft:wheat_seeds", "Item required for Hoe to cut grass in a 3x3").getString();

        swordAreaItem = config.get(sectionSword, "swordAreaItem", "minecraft:feather", "Item required for the sword AOE upgrade").getString();
        swordAreaXP = config.get(sectionSword, "swordAreaXP", 10, "XP required for the sword AOE upgrade").getInt();
        
        allowPlace = config.get(sectionFeatures, "allowPlace", true).getBoolean();
        allow3x1Pick = config.get(sectionFeatures, "allow3x1Pick", true).getBoolean();
        allow3x1Shovel = config.get(sectionFeatures, "allow3x1Shovel", true).getBoolean();
        allow3x1Axe = config.get(sectionFeatures, "allow3x1Axe", true).getBoolean();
        allow3x3Pick = config.get(sectionFeatures, "allow3x3Pick", true).getBoolean();
        allow3x3Shovel = config.get(sectionFeatures, "allow3x3Shovel", true).getBoolean();
        allow3x3Hoe = config.get(sectionFeatures, "allow3x3Hoe", true).getBoolean();
        allowSwordAOE = config.get(sectionFeatures, "allowSwordAOE", true).getBoolean();
        //allowUnbreakable = config.get(sectionFeatures, "allowUnbreakable", true).getBoolean();
        
        //unbreakableItem = config.get(sectionUnbreakable, "unbreakableItem", "minecraft:obisidian", "Item required for having 'unbreakable' tools").getString();
        //unbreakableXP = config.get(sectionUnbreakable, "unbreakableXP", 10, "Number of levels required for having 'unbreakable' tools").getInt();
        
        mekanismModule = config.get(sectionModules, "mekanism", true).getBoolean();
        allowPaxelUpgrades = config.get(sectionModules,"mekanismPaxel", true, "Allow Mekanism Paxels to receive upgrades").getBoolean();

        railcraftModule = config.get(sectionModules,"railcraft",true,"Allow Crowbars to receive Sword upgrades").getBoolean();
        
        blacklistPlace = config.get(sectionRightClick,"toolBlacklist", "", "Unlocalized name of tools for blacklisting (Comma separated)").getString().split(",");
        blacklist3x1 = config.get(sectionAreaMining,"3x1blacklist", "", "Unlocalized name of tools for blacklisting (Comma separated)").getString().split(",");
        blacklist3x3 = config.get(sectionAreaMining,"3x3blacklist", "", "Unlocalized name of tools for blacklisting (Comma separated)").getString().split(",");
        blacklistHoe = config.get(sectionHoeArea,"blacklist", "", "Unlocalized name of tools for blacklisting (Comma separated)").getString().split(",");
        blacklistSword = config.get(sectionSword,"blacklist", "", "Unlocalized name of tools for blacklisting (Comma separated)").getString().split(",");
        //blacklistUnbreakable = config.get(sectionUnbreakable,"blacklist", "", "Unlocalized name of tools for blacklisting (Comma separated)").getString().split(",");
        
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
