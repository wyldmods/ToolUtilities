package org.wyldmods.toolutilities.client.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import org.wyldmods.toolutilities.ToolUtilities;

import static org.wyldmods.toolutilities.common.Config.*;

public class TUGuiConfig extends GuiConfig
{
    public TUGuiConfig(GuiScreen parentScreen)
    {
        super(parentScreen, getConfigElements(parentScreen), ToolUtilities.MODID, false, false, I18n.format(ToolUtilities.LOCALIZING + ".config.title"));
    }

    private static List<IConfigElement> getConfigElements(GuiScreen parent)
    {
        List<IConfigElement> list = new ArrayList<IConfigElement>();

        list.add(new ConfigElement(getCategory(sectionRightClick)));
        list.add(new ConfigElement(getCategory(sectionAreaMining)));
        list.add(new ConfigElement(getCategory(sectionHoeArea)));
        list.add(new ConfigElement(getCategory(sectionSword)));
        list.add(new ConfigElement(getCategory(sectionFeatures)));

        return list;
    }
    
    private static final String prefix = ToolUtilities.LOCALIZING + ".config.";
    private static ConfigCategory getCategory(String name)
    {
        return config.getCategory(name.toLowerCase()).setLanguageKey(prefix + name.toLowerCase().replace(" ", ""));
    }
}
