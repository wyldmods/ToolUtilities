package org.wyldmods.toolutilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PlaceItem {
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.isCanceled() || event.world.isRemote) { //Serverside
			return;
		}
		
		if (event.action != event.action.RIGHT_CLICK_BLOCK) { //Right clicking only (and on a block)
			return;
		}
		
		ItemStack current = event.entityPlayer.inventory.getCurrentItem();
		if (current != null && current.getItem() instanceof ItemTool && checkIfCanPlace(current)) 
		{
			//Inspiration from TiC and TorchTools here
			EntityPlayer player = event.entityPlayer;
			EntityPlayerMP playerMP = (EntityPlayerMP) event.entityPlayer;
			
			int currentSlot = player.inventory.currentItem;
			int newSlot = (currentSlot==0) ? 8 : currentSlot+1; //Loops over the top.
			
			if (newSlot > 8 || player.inventory.getStackInSlot(newSlot)==null) {
				return;
			}
			
			player.inventory.currentItem = newSlot;
			ItemStack useStack = player.inventory.getStackInSlot(newSlot);
			//Use item
			if (!(useStack.getItem() instanceof ItemTool) && !ToolUtilities.blacklistedItems.contains(useStack.getItem()))
			{
				playerMP.theItemInWorldManager.activateBlockOrUseItem(player, event.world, useStack, event.x, event.y, event.z, event.face, 0.5F,0.5F,0.5F); //Assume looking halfway at block until ray tracing
			}
			if (useStack.stackSize <= 0) {
				useStack = null;
			}
			player.inventory.currentItem = currentSlot;
			
			//And update (TorchTools)
			playerMP.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(0, newSlot+36, useStack));
			
			
			//And end
			event.setCanceled(true);
			
		}
		
	}
	
	private boolean checkIfCanPlace(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		
		if (tag != null)
		{
			return tag.getBoolean(ToolUtilities.MODID+"canPlace");
		}
		else
		{
			return false;
		}
	}

}
