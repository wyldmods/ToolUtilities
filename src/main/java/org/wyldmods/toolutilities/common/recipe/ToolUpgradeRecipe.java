package org.wyldmods.toolutilities.common.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ToolUpgradeRecipe
{
    public final Class<? extends Item> input;
    public final ItemStack modifier;
    public final ToolUpgrade upgrade;
    public final int cost;

    private ToolUpgradeRecipe(Class<? extends Item> input, ItemStack modifier, ToolUpgrade upgrade, int cost)
    {
        this.input = input;
        this.modifier = modifier;
        this.upgrade = upgrade;
        this.cost = cost;
    }

    private static List<ToolUpgradeRecipe> recipes = new ArrayList<ToolUpgradeRecipe>();

    public static void addUpgradeRecipe(Class<? extends Item> input, ItemStack modifier, ToolUpgrade upgrade)
    {
        addUpgradeRecipe(input, modifier, upgrade, 5);
    }
    
    public static void addUpgradeRecipe(Class<? extends Item> input, ItemStack modifier, ToolUpgrade upgrade, int cost, boolean configOption)
    {
        if (configOption)
        {
            addUpgradeRecipe(input, modifier, upgrade, cost);
        }
    }
    
    public static void addUpgradeRecipe(Class<? extends Item> input, ItemStack modifier, ToolUpgrade upgrade, int cost)
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
                if (matches(recipe, input, modifier))
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
            if (matches(recipe, input, modifier))
            {
                return recipe;
            }
        }

        return null;
    }
    
    public static ItemStack getModifierFor(ItemStack input, ToolUpgrade upgrade)
    {
        if (input == null || upgrade == null)
        {
            return null;
        }

        for (ToolUpgradeRecipe recipe : recipes)
        {
            if (matches(recipe, input, upgrade))
            {
                return recipe.modifier.copy();
            }
        }

        return null;
    }
    
    public static boolean recipeExistsFor(ItemStack stack, ToolUpgrade upgrade)
    {
        if (stack == null || upgrade == null)
        {
            return false;
        }
        
        for (ToolUpgradeRecipe recipe : recipes)
        {
            if (recipe.input.isAssignableFrom(stack.getItem().getClass()) && recipe.upgrade == upgrade)
            {
                return true;
            }
        }
        return false;
    }
    
    private static boolean matches(ToolUpgradeRecipe recipe, ItemStack input, ItemStack modifier)
    {
        if (recipe.input.isAssignableFrom(input.getItem().getClass()))
        {
            int damage = recipe.modifier.getItemDamage();
            if ((damage == OreDictionary.WILDCARD_VALUE || damage == modifier.getItemDamage()) && recipe.modifier.getItem() == modifier.getItem() && recipe.modifier.stackSize == modifier.stackSize)
            {
                return !recipe.upgrade.isOn(input) && recipe.upgrade.hasPreReqs(input) && !Arrays.asList(recipe.upgrade.getToolBlacklist()).contains(input.getItem().getUnlocalizedName());
            }
        }
        return false;
    }
    
    private static boolean matches(ToolUpgradeRecipe recipe, ItemStack input, ToolUpgrade upgrade)
    {
        if (recipe.input.isAssignableFrom(input.getItem().getClass()))
        {
            if (upgrade == recipe.upgrade)
            {
                return !recipe.upgrade.isOn(input) && recipe.upgrade.hasPreReqs(input);
            }
        }
        return false;
    }

    public static void clear()
    {
        recipes.clear();
    }
}
