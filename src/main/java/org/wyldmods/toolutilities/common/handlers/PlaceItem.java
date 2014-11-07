package org.wyldmods.toolutilities.common.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import org.wyldmods.toolutilities.ToolUtilities;
import org.wyldmods.toolutilities.common.recipe.ToolUpgrade;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PlaceItem
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event)
    {
        if (event.isCanceled())
        { // Serverside
            return;
        }

        if (event.action != Action.RIGHT_CLICK_BLOCK)
        { // Right clicking only (and on a block)
            return;
        }

        ItemStack current = event.entityPlayer.inventory.getCurrentItem();
        if (current != null && current.getItem() instanceof ItemTool && checkIfCanPlace(current))
        {
            EntityPlayer player = event.entityPlayer;

            int currentSlot = player.inventory.currentItem;
            int newSlot = (currentSlot == 0) ? 8 : currentSlot + 1; // Loops over the top.

            if (newSlot > 8 || player.inventory.getStackInSlot(newSlot) == null)
            {
                return;
            }

            player.inventory.currentItem = newSlot;
            ItemStack useStack = player.inventory.getStackInSlot(newSlot);

            if (!event.world.isRemote)
            {
                // Inspiration from TiC and TorchTools here
                EntityPlayerMP playerMP = (EntityPlayerMP) event.entityPlayer;
                
                
                // Use item
                if (canUseItem(useStack))
                {
                    playerMP.theItemInWorldManager.activateBlockOrUseItem(player, event.world, useStack, event.x, event.y, event.z, event.face, 0.5F, 0.5F, 0.5F); // Assume looking halfway at block //
                                                                                                                                                                   // until // ray tracing
                }
                if (useStack.stackSize <= 0)
                {
                    useStack = null;
                }

                // And update (TorchTools)
                playerMP.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(0, newSlot + 36, useStack));

                // And end
                event.setCanceled(true);
            }
            else if (canUseItem(useStack))
            {
                player.swingItem();
            }

            player.inventory.currentItem = currentSlot;

        }
    }

    private boolean canUseItem(ItemStack useStack)
    {
        return !(useStack.getItem() instanceof ItemTool) && !ToolUtilities.blacklistedItems.contains(useStack.getItem());
    }

    private boolean checkIfCanPlace(ItemStack stack)
    {
        return ToolUpgrade.PLACE.isOn(stack);
    }

}
