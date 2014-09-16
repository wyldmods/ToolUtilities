package org.wyldmods.toolutilities.common.handlers;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import org.wyldmods.toolutilities.common.Config;
import org.wyldmods.toolutilities.common.ToolUpgrade;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class AOEHoe {
	
	public static Material[] validMaterials = {Material.plants, Material.vine};

	@SubscribeEvent
	public void mineAOE(BreakEvent event) 
	{
		EntityPlayer player = event.getPlayer();
		if (player != null)
		{
			ItemStack current = player.getCurrentEquippedItem();
			if (current != null && !event.world.isRemote)
			{
				if (current.getItem() instanceof ItemHoe)
				{
					if (ToolUpgrade.hasUpgrade(current, ToolUpgrade.HOExTHREE) && canHarvest(event.block))
					{
						int[][] blocksToMine = { {-1, 0, -1}, {-1, 0, 0}, {-1, 0, 1}, {0, 0, -1}, {0, 0, 1}, {1, 0, -1}, {1, 0, 0}, {1, 0, 1}};
						mineGrass(blocksToMine, event);	
					}
				}
			}
		}
	}

	//Returns the direction the player is facing (0-3).
	public int getDirection(EntityPlayer player)
	{
		return MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	}
	
	public void mineGrass(int[][] blocks, BreakEvent event)
	{
		EntityPlayer player = event.getPlayer();
		ItemStack current = player.getCurrentEquippedItem();
		
		for (int i=0; i<blocks.length; i++)
		{
			Block currentBlock = event.world.getBlock(event.x+blocks[i][0], event.y+blocks[i][1], event.z+blocks[i][2]);
			int currentMeta = event.world.getBlockMetadata(event.x+blocks[i][0], event.y+blocks[i][1], event.z+blocks[i][2]);
			if (canHarvest(currentBlock))
			{
				mineBlock(event.world, event.x+blocks[i][0], event.y+blocks[i][1], event.z+blocks[i][2], currentMeta, player, currentBlock);
				current.damageItem(1,  player);
				if (current.getItemDamage()>=current.getMaxDamage())
				{
					return;
				}
			}
		}
	}
	
	public boolean canHarvest(Block block) 
	{
		for (Material i : validMaterials)
		{
			if (i==block.getMaterial())
			{
				return true;
			}
		}
		return false;
	}
	
	public void mineBlock(World world, int x, int y, int z, int meta, EntityPlayer player, Block block)
    {
        // Workaround for dropping experience
        boolean silktouch = EnchantmentHelper.getSilkTouchModifier(player);
        int fortune = EnchantmentHelper.getFortuneModifier(player);
        int exp = block.getExpDrop(world, meta, fortune);

        block.onBlockHarvested(world, x, y, z, meta, player);
        if (block.removedByPlayer(world, player, x, y, z, true))
        {
            block.onBlockDestroyedByPlayer(world, x, y, z, meta);
            block.harvestBlock(world, player, x, y, z, meta);
            // Workaround for dropping experience
            if (!silktouch)
                block.dropXpOnBlockBreak(world, x, y, z, exp);

            if (world.isRemote)
            {
                INetHandler handler = FMLClientHandler.instance().getClientPlayHandler();
                if (handler != null && handler instanceof NetHandlerPlayClient)
                {
                    NetHandlerPlayClient handlerClient = (NetHandlerPlayClient) handler;
                    handlerClient.addToSendQueue(new C07PacketPlayerDigging(0, x, y, z, Minecraft.getMinecraft().objectMouseOver.sideHit));
                    handlerClient.addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, Minecraft.getMinecraft().objectMouseOver.sideHit));
                }
            }
            else if (Config.noisyBlocks)
            {
                world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
            }
        }
    }

}
