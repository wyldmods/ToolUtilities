package org.wyldmods.toolutilities.common.handlers;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import org.wyldmods.toolutilities.common.Config;
import org.wyldmods.toolutilities.common.recipe.ToolUpgrade;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class AOEHandler
{
    @SubscribeEvent
    public void breakSpeed(BreakSpeed event)
    {
        ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
        if (stack != null)
        {
            if (ToolUpgrade.THREExONE.isOn(stack))
            {
                event.newSpeed *= Config.speedMult3x1;
            }
            if (ToolUpgrade.THREExTHREE.isOn(stack))
            {
                event.newSpeed *= Config.speedMult3x3;
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void mineAOE(BreakEvent event)
    {
        int x = event.x, y = event.y, z = event.z;
        EntityPlayer player = event.getPlayer();
        if (player != null)
        {
            ItemStack current = player.getCurrentEquippedItem();
            if (current != null && !event.world.isRemote)
            {
                if (canHarvestBlock(player, event.block, event.block, event.blockMetadata, x, y, z))
                {
                    if (ToolUpgrade.THREExONE.isOn(current))
                    {
                        do3x1Mine(event);
                    }

                    if (ToolUpgrade.THREExTHREE.isOn(current))
                    {
                        do3x3Mine(event);
                    }
                }

                if (ToolUpgrade.HOExTHREE.isOn(current))
                {
                    do3x3Hoe(event);
                }
            }
        }
    }

    private void do3x1Mine(BreakEvent event)
    {
        MovingObjectPosition mop = raytraceFromEntity(event.world, event.getPlayer(), false, 4.5D);
        if (mop.sideHit != 0 && mop.sideHit != 1)
        {
            int[][] mineArray = { { 0, 1, 0 }, { 0, -1, 0 } };
            mineOutEverything(mineArray, event);
        }
    }

    private void do3x3Mine(BreakEvent event)
    {
        // 3x3 time!
        MovingObjectPosition mop = raytraceFromEntity(event.world, event.getPlayer(), false, 4.5D);
        if (mop == null)
            return;
        switch (mop.sideHit)
        {
        case 0: // Bottom
            int[][] mineArrayBottom = { { 1, 0, 1 }, { 1, 0, 0 }, { 1, 0, -1 }, { 0, 0, 1 }, { 0, 0, -1 }, { -1, 0, 1 }, { -1, 0, 0 }, { -1, 0, -1 } };
            mineOutEverything(mineArrayBottom, event);
            break;
        case 1: // Top
            int[][] mineArrayTop = { { 1, 0, 1 }, { 1, 0, 0 }, { 1, 0, -1 }, { 0, 0, 1 }, { 0, 0, -1 }, { -1, 0, 1 }, { -1, 0, 0 }, { -1, 0, -1 } };
            mineOutEverything(mineArrayTop, event);
            break;
        case 2: // South
            int[][] mineArraySouth = { { -1, 1, 0 }, { -1, 0, 0 }, { -1, -1, 0 }, { 0, 1, 0 }, { 0, -1, 0 }, { 1, 1, 0 }, { 1, 0, 0 }, { 1, -1, 0 } };
            mineOutEverything(mineArraySouth, event);
            break;
        case 3: // North
            int[][] mineArrayNorth = { { -1, 1, 0 }, { -1, 0, 0 }, { -1, -1, 0 }, { 0, 1, 0 }, { 0, -1, 0 }, { 1, 1, 0 }, { 1, 0, 0 }, { 1, -1, 0 } };
            mineOutEverything(mineArrayNorth, event);
            break;
        case 4: // East
            int[][] mineArrayEast = { { 0, 1, 1 }, { 0, 0, 1 }, { 0, -1, 1 }, { 0, 1, 0 }, { 0, -1, 0 }, { 0, 1, -1 }, { 0, 0, -1 }, { 0, -1, -1 } };
            mineOutEverything(mineArrayEast, event);
            break;
        case 5: // West
            int[][] mineArrayWest = { { 0, 1, 1 }, { 0, 0, 1 }, { 0, -1, 1 }, { 0, 1, 0 }, { 0, -1, 0 }, { 0, 1, -1 }, { 0, 0, -1 }, { 0, -1, -1 } };
            mineOutEverything(mineArrayWest, event);
            break;
        }
    }

    private void do3x3Hoe(BreakEvent event)
    {
        if (canHoeHarvest(event.block))
        {
            int[][] blocksToMine = { { -1, 0, -1 }, { -1, 0, 0 }, { -1, 0, 1 }, { 0, 0, -1 }, { 0, 0, 1 }, { 1, 0, -1 }, { 1, 0, 0 }, { 1, 0, 1 } };
            mineGrass(blocksToMine, event);
        }
    }

    public void mineOutEverything(int[][] locations, BreakEvent event)
    {
        EntityPlayer player = event.getPlayer();
        ItemStack current = player.getCurrentEquippedItem();

        for (int i = 0; i < locations.length; i++)
        {
            Block miningBlock = event.world.getBlock(event.x + locations[i][0], event.y + locations[i][1], event.z + locations[i][2]);
            int meta = event.world.getBlockMetadata(event.x + locations[i][0], event.y + locations[i][1], event.z + locations[i][2]);
            if (canHarvestBlock(player, event.block, miningBlock, meta, event.x, event.y, event.z))
            {
                if (!((ItemTool) current.getItem()).onBlockStartBreak(current, event.x + locations[i][0], event.y + locations[i][1], event.z + locations[i][2], player))
                {
                    mineBlock(event.world, event.x + locations[i][0], event.y + locations[i][1], event.z + locations[i][2],
                            event.world.getBlockMetadata(event.x + locations[i][0], event.y + locations[i][1], event.z + locations[i][2]), player, miningBlock);
                    ((ItemTool) current.getItem()).onBlockDestroyed(current, event.world, miningBlock, event.x + locations[i][0], event.y + locations[i][1], event.z
                            + locations[i][2], player);
                    player.addExhaustion((float) 0.025);
                }
            }
        }
    }

    public static MovingObjectPosition raytraceFromEntity(World world, Entity player, boolean par3, double range)
    {	// 100% borrowed from Tinkers Construct (CC0 license).
        float f = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double) f;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double) f;
        if (!world.isRemote && player instanceof EntityPlayer)
            d1 += 1.62D;
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) f;
        Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = range;
        if (player instanceof EntityPlayerMP)
        {
            d3 = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3 vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
        return world.func_147447_a(vec3, vec31, par3, !par3, par3);
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

    private static Map<Class<? extends ItemTool>, String> toolClasses = new HashMap<Class<? extends ItemTool>, String>();
    static
    {
        toolClasses.put(ItemPickaxe.class, "pickaxe");
        toolClasses.put(ItemSpade.class, "shovel");
        toolClasses.put(ItemAxe.class, "axe");
    }

    private boolean canHarvestBlock(EntityPlayer player, Block origBlock, Block block, int meta, int x, int y, int z)
    {
        ItemStack current = player.getCurrentEquippedItem();

        if (current == null)
            return false;

        String toolClass = toolClasses.get(current.getItem().getClass());

        if (toolClass == null)
            return false;

        float hardness = block.getBlockHardness(player.worldObj, x, y, z);
        float digSpeed = ((ItemTool) current.getItem()).getDigSpeed(current, block, meta);
        // It works. It just does.
        return (digSpeed > 1.0F && block.getHarvestLevel(meta) <= ((ItemTool) current.getItem()).getHarvestLevel(current, toolClass) && hardness > 0 && origBlock
                .getBlockHardness(player.worldObj, x, y, z) >= hardness - 1.5);
    }

    public void mineGrass(int[][] blocks, BreakEvent event)
    {
        EntityPlayer player = event.getPlayer();
        ItemStack current = player.getCurrentEquippedItem();

        for (int i = 0; i < blocks.length; i++)
        {
            Block currentBlock = event.world.getBlock(event.x + blocks[i][0], event.y + blocks[i][1], event.z + blocks[i][2]);
            int currentMeta = event.world.getBlockMetadata(event.x + blocks[i][0], event.y + blocks[i][1], event.z + blocks[i][2]);
            if (canHoeHarvest(currentBlock))
            {
                mineBlock(event.world, event.x + blocks[i][0], event.y + blocks[i][1], event.z + blocks[i][2], currentMeta, player, currentBlock);
                current.damageItem(1, player);
                if (current.getItemDamage() >= current.getMaxDamage())
                {
                    return;
                }
            }
        }
    }

    public static Material[] validHoeMaterials = { Material.plants, Material.vine };

    public boolean canHoeHarvest(Block block)
    {
        for (Material i : validHoeMaterials)
        {
            if (i == block.getMaterial())
            {
                return true;
            }
        }
        return false;
    }
}
