package org.wyldmods.toolutilities.common.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.wyldmods.toolutilities.ToolUtilities;
import org.wyldmods.toolutilities.common.recipe.ToolUpgrade;

public class PlaceItem
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerInteractEvent(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.isCanceled() || event.getHand() == EnumHand.OFF_HAND)
        {
            return;
        }
        
        ItemStack current = event.getEntityPlayer().getHeldItemMainhand();
        if (current != null && current.getItem() instanceof ItemTool && checkIfCanPlace(current))
        {
            EntityPlayer player = event.getEntityPlayer();

            int currentSlot = player.inventory.currentItem;
            int newSlot = (currentSlot == 0) ? 8 : currentSlot + 1; // Loops over the top.

            if (newSlot > 8 || player.inventory.getStackInSlot(newSlot) == null)
            {
                return;
            }

            player.inventory.currentItem = newSlot;
            ItemStack useStack = player.inventory.getStackInSlot(newSlot);

            if (!event.getWorld().isRemote)
            {
                // Inspiration from TiC and TorchTools here
                EntityPlayerMP playerMP = (EntityPlayerMP) event.getEntityPlayer();
                
                // Use item
                if (canUseItem(useStack))
                {
                    playerMP.interactionManager.processRightClickBlock(player, event.getWorld(), useStack, event.getHand(), event.getPos(), event.getFace(), 0.5F, 0.5F, 0.5F); // Assume looking halfway at block until ray tracing
                }
                if (useStack.stackSize <= 0)
                {
                    useStack = null;
                }

                // And update (TorchTools)
                playerMP.connection.sendPacket(new SPacketSetSlot(0, newSlot + 36, useStack));

                // And end
                event.setCanceled(true);
                player.swingArm(event.getHand());
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
