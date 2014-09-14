package org.wyldmods.toolutilities.common.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.oredict.OreDictionary;

import org.wyldmods.toolutilities.common.ToolUpgrade;

public class ToolUpgradeRecipe
{
    public final Class<? extends ItemTool> input;
    public final ItemStack modifier;
    public final ToolUpgrade upgrade;
    public final int cost;
    
    private ToolUpgradeRecipe(Class<? extends ItemTool> input, ItemStack modifier, ToolUpgrade upgrade, int cost)
    {
        this.input = input;
        this.modifier = modifier;
        this.upgrade = upgrade;
        this.cost = cost;
    }

    private static List<ToolUpgradeRecipe> recipes = new ArrayList<ToolUpgradeRecipe>();

    public static void addUpgradeRecipe(Class<? extends ItemTool> input, ItemStack modifier, ToolUpgrade upgrade)
    {
        addUpgradeRecipe(input, modifier, upgrade, 5);
    }
    
    public static void addUpgradeRecipe(Class<? extends ItemTool> input, ItemStack modifier, ToolUpgrade upgrade, int cost)
    {
        recipes.add(new ToolUpgradeRecipe(input, modifier, upgrade, cost));
    }

    public static boolean isValidInput(ItemStack input, ItemStack modifier)
    {
        if (input == null || modifier == null)
        {
            return false;
        }

        for (ToolUpgradeRecipe recipe : recipes)
        {
            if (recipe.input.isAssignableFrom(input.getItem().getClass()))
            {
                int damage = recipe.modifier.getItemDamage();
                if ((damage == OreDictionary.WILDCARD_VALUE || damage == modifier.getItemDamage()) && recipe.modifier.getItem() == modifier.getItem())
                {
                    return true; 
                }
            }
        }
        
        return false;
    }
    
    public static ToolUpgradeRecipe getOutputFor(ItemStack input, ItemStack modifier)
    {
        if (input == null || modifier == null)
        {
            return null;
        }

        for (ToolUpgradeRecipe recipe : recipes)
        {
            if (recipe.input.isAssignableFrom(input.getItem().getClass()))
            {
                int damage = recipe.modifier.getItemDamage();
                if ((damage == OreDictionary.WILDCARD_VALUE || damage == modifier.getItemDamage()) && recipe.modifier.getItem() == modifier.getItem())
                {
                    return recipe;
                }
            }
        }
        
        return null;
    }
}
