package org.wyldmods.toolutilities.common;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

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
	
	
	public static void doConfig(File file) {
		Configuration config = new Configuration(file);
		config.load();
		
		XPAmount = config.get("RightClick", "XPRequired", 10, "Number of levels required for Right Click Placing").getInt();
		rightClickItem = config.get("RightClick", "Item","minecraft:torch", "Item required for Right Click Placing").getString();
		enableRightClick = config.get("RightClick","Enable",true,"Enable Right Click Placing").getBoolean();
		
		areaXPAmount = config.get("AreaMining","areaXPRequired", 30, "Number of levels required for 1x3 Mining").getInt();
		nineXPAmount = config.get("AreaMining","1x9XPRequired", 30, "Number of levels required for 3x3 Mining").getInt();
		areaItem = config.get("AreaMining","1x3Item", "minecraft:coal", "Item required for 1x3 AoE Mining").getString();
		nineItem = config.get("AreaMining","3x3Item","minecraft:coal","Item required for 3x3 AoE Mining").getString();
		enableColumn = config.get("AreaMining","Enable1x3",true,"Enable 1x3 mining upgrade").getBoolean();
		enableNine = config.get("AreaMining","Enable3x3",true,"Enable 3x3 mining ugprade").getBoolean();
		noisyBlocks = config.get("AreaMining","noisyBlock",true,"When AOE blocks are broken, should they play a sound and make particles").getBoolean();
		
		blacklist = config.get("RightClick","blacklist","","Blacklisted items for Right Click Placing").getString();
		
		config.save();
	}
}
