package org.wyldmods.toolutilities.common.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import org.wyldmods.toolutilities.common.Config;
import org.wyldmods.toolutilities.common.compat.MekanismCompat;
import org.wyldmods.toolutilities.common.recipe.ToolUpgrade;
import org.wyldmods.toolutilities.common.util.DirectionHelper;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class AOEHandler
{
    /* ==== Mining ==== */

    @SubscribeEvent
    public void breakSpeed(BreakSpeed event)
    {
        ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
        if (stack != null && !event.entityPlayer.isSneaking() && canHarvestBlock(event.entityPlayer, event.block, event.block, event.metadata, event.x, event.y, event.z))
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
        if (player != null && !player.isSneaking())
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
        MovingObjectPosition mop = DirectionHelper.raytraceFromEntity(event.world, event.getPlayer(), false, 4.5D);
        
        if (mop == null) // something is NaN, bail out!
        {
            return;
        }
        
        if (mop.sideHit != 0 && mop.sideHit != 1)
        {
            int[][] mineArray = { { 0, 1, 0 }, { 0, -1, 0 } };
            mineOutEverything(mineArray, event);
        }
        else //Hit the top or bottom.
        {
        	int playerDir = DirectionHelper.getPlayerDirection(event.getPlayer());
        	mineOutEverything(DirectionHelper.get1x3MiningCoordinatesForTopAndBottom(playerDir), event);
        }
    }

    private void do3x3Mine(BreakEvent event)
    {
        // 3x3 time!
        MovingObjectPosition mop = DirectionHelper.raytraceFromEntity(event.world, event.getPlayer(), false, 4.5D);
        if (mop == null)
            return;
        
        mineOutEverything(DirectionHelper.get3x3MiningCoordinates(mop), event);
        
    }

    private void do3x3Hoe(BreakEvent event)
    {
        if (canHoeHarvest(event.block))
        {
            mineGrass(DirectionHelper.mineArrayTop, event);
        }
    }

    public void mineOutEverything(int[][] locations, BreakEvent event)
    {
        EntityPlayer player = event.getPlayer();
        ItemStack current = player.getCurrentEquippedItem();

        for (int i = 0; i < locations.length; i++)
        {
            int curX = event.x + locations[i][0];
            int curY = event.y + locations[i][1];
            int curZ = event.z + locations[i][2];

            Block miningBlock = event.world.getBlock(curX, curY, curZ);
            int meta = event.world.getBlockMetadata(curX, curY, curZ);
            if (canHarvestBlock(player, event.block, miningBlock, meta, curX, curY, curZ))
            {
                if (!((ItemTool) current.getItem()).onBlockStartBreak(current, curX, curY, curZ, player))
                {
                    mineBlock(event.world, curX, curY, curZ, meta, player, miningBlock);
                    ((ItemTool) current.getItem()).onBlockDestroyed(current, event.world, miningBlock, curX, curY, curZ, player);
                    player.addExhaustion((float) 0.025);
                }
            }
        }
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
        
        if (Loader.isModLoaded("MekanismTools") && Config.mekanismModule)
        {
        	toolClasses = MekanismCompat.addMekanismToolToClasses(toolClasses);
        }
    }

    private boolean canHarvestBlock(EntityPlayer player, Block origBlock, Block block, int meta, int x, int y, int z)
    {
        ItemStack current = player.getCurrentEquippedItem();
        if (current == null)
            return false;

        String toolClass = getToolClass(current.getItem().getClass());
        if (toolClass == null)
            return false;

        float hardness = block.getBlockHardness(player.worldObj, x, y, z);
        float digSpeed = ((ItemTool) current.getItem()).getDigSpeed(current, block, meta);
       
        // It works. It just does.
        return (digSpeed > 1.0F && block.getHarvestLevel(meta) <= ((ItemTool) current.getItem()).getHarvestLevel(current, toolClass) && hardness >= 0 && origBlock
                .getBlockHardness(player.worldObj, x, y, z) >= hardness - 1.5);
    }

    private static String getToolClass(Class<? extends Item> clazz)
    {
        for (Class<? extends ItemTool> toolClass : toolClasses.keySet())
        {
            if (toolClass.isAssignableFrom(clazz))
            {
                return toolClasses.get(toolClass);
            }
        }
        return null;
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

    /* ==== Sword ==== */

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event)
    {
        if (!event.entityPlayer.worldObj.isRemote)
        {
            EntityPlayer fakePlayer = FakePlayerFactory.get((WorldServer) event.entityPlayer.worldObj, new GameProfile(null, "ToolUtils-SwordDummy"));
            if (event.entityPlayer == fakePlayer) return;
            ItemStack current = event.entityPlayer.getCurrentEquippedItem();
            if (current != null && ToolUpgrade.SWORD_AOE.isOn(current))
            {
                copy(event.entityPlayer, fakePlayer);
                AxisAlignedBB bb = event.target.boundingBox;
                bb = bb.expand(2, 2, 2);
                List<EntityLivingBase> list = event.entity.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, bb);
                for (EntityLivingBase entity : list)
                {
                    if (entity != event.target && entity != event.entityPlayer)
                    {
                        fakePlayer.attackTargetEntityWithCurrentItem(entity);
                        current.getItem().hitEntity(current, entity, event.entityPlayer);
                    }
                }
                
                fakePlayer.setLocationAndAngles(0, 0, 0, 0, 0);
            }
        }
    }
    
    private static void copy(EntityPlayer from, EntityPlayer to)
    {
        to.setSprinting(from.isSprinting());
        to.setLocationAndAngles(from.posX, from.posY, from.posZ, from.rotationYaw, from.rotationPitch);
        to.fallDistance = from.fallDistance;
        to.onGround = from.onGround;
        to.inventory.mainInventory[to.inventory.currentItem] = from.getCurrentEquippedItem();
        to.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(from.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
    }
}
