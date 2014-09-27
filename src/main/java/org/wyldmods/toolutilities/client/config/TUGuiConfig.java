package org.wyldmods.toolutilities.client.config;

import static org.wyldmods.toolutilities.common.Config.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;

import org.wyldmods.toolutilities.ToolUtilities;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

public class TUGuiConfig extends GuiConfig
{
    public TUGuiConfig(GuiScreen parentScreen)
    {
        super(parentScreen, getConfigElements(parentScreen), ToolUtilities.MODID, false, false, StatCollector.translateToLocal(ToolUtilities.LOCALIZING + ".config.title"));
    }

    @SuppressWarnings("rawtypes")
    private static List<IConfigElement> getConfigElements(GuiScreen parent)
    {
        List<IConfigElement> list = new ArrayList<IConfigElement>();

        list.add(new ConfigElement<ConfigCategory>(getCategory(sectionRightClick)));
        list.add(new ConfigElement<ConfigCategory>(getCategory(sectionAreaMining)));
        list.add(new ConfigElement<ConfigCategory>(getCategory(sectionHoeArea)));
        list.add(new ConfigElement<ConfigCategory>(getCategory(sectionSword)));
        list.add(new ConfigElement<ConfigCategory>(getCategory(sectionFeatures)));

        return list;
    }
    
    private static final String prefix = ToolUtilities.LOCALIZING + ".config.";
    private static ConfigCategory getCategory(String name)
    {
        return config.getCategory(name.toLowerCase()).setLanguageKey(prefix + name.toLowerCase().replace(" ", ""));
    }
}
