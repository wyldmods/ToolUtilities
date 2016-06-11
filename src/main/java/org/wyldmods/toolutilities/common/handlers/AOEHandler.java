package org.wyldmods.toolutilities.common.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.wyldmods.toolutilities.common.Config;
import org.wyldmods.toolutilities.common.recipe.ToolUpgrade;
import org.wyldmods.toolutilities.common.util.DirectionHelper;

import com.mojang.authlib.GameProfile;

public class AOEHandler
{
    /* ==== Mining ==== */

    @SubscribeEvent
    public void breakSpeed(BreakSpeed event)
    {
        ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();
        IBlockState state = event.getEntityPlayer().worldObj.getBlockState(event.getPos());
        if (stack != null && !event.getEntityPlayer().isSneaking() && canHarvestBlock(event.getEntityPlayer(), state, state, event.getPos()))
        {
            if (ToolUpgrade.THREExONE.isOn(stack))
            {
                event.setNewSpeed(event.getOriginalSpeed() * Config.speedMult3x1);
            }
            if (ToolUpgrade.THREExTHREE.isOn(stack))
            {
                event.setNewSpeed(event.getOriginalSpeed() * Config.speedMult3x3);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void mineAOE(BreakEvent event)
    {
        BlockPos pos = event.getPos();
        EntityPlayer player = event.getPlayer();
        if (player != null && !player.isSneaking())
        {
            ItemStack current = player.getHeldItemMainhand();
            if (current != null && !event.getWorld().isRemote)
            {
            	
                if (canHarvestBlock(player, event.getState(), event.getState(), pos))
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
        RayTraceResult mop = DirectionHelper.raytraceFromEntity(event.getWorld(), event.getPlayer(), false, 4.5D);
        
        if (mop == null) // something is NaN, bail out!
        {
            return;
        }
        
        if (mop.sideHit.getAxis().isHorizontal())
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
        RayTraceResult mop = DirectionHelper.raytraceFromEntity(event.getWorld(), event.getPlayer(), false, 4.5D);
        if (mop == null)
            return;
        
        mineOutEverything(DirectionHelper.get3x3MiningCoordinates(mop), event);
        
    }

    private void do3x3Hoe(BreakEvent event)
    {
        if (canHoeHarvest(event.getState()))
        {
            mineGrass(DirectionHelper.mineArrayTop, event);
        }
    }

    public void mineOutEverything(int[][] locations, BreakEvent event)
    {
        EntityPlayer player = event.getPlayer();
        ItemStack current = player.getHeldItemMainhand();

        for (int i = 0; i < locations.length; i++)
        {
            BlockPos pos = event.getPos().add(locations[i][0], locations[i][1], locations[i][2]);

            IBlockState miningBlock = event.getWorld().getBlockState(pos);
            if (canHarvestBlock(player, event.getState(), miningBlock, pos))
            {
                if (!((ItemTool) current.getItem()).onBlockStartBreak(current, pos, player))
                {
                    mineBlock(event.getWorld(), pos, player, miningBlock);
                    ((ItemTool) current.getItem()).onBlockDestroyed(current, event.getWorld(), miningBlock, pos, player);
                    player.addExhaustion((float) 0.025);
                }
            }
        }
    }

    

    public void mineBlock(World world, BlockPos pos, EntityPlayer player, IBlockState block)
    {
        // Workaround for dropping experience
        boolean silktouch = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.SILK_TOUCH, player) > 0;
        int fortune = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FORTUNE, player);
        int exp = block.getBlock().getExpDrop(block, world, pos, fortune);
        TileEntity te = world.getTileEntity(pos);
        
        block.getBlock().onBlockHarvested(world, pos, block, player);
        if (block.getBlock().removedByPlayer(block, world, pos, player, true))
        {
            block.getBlock().onBlockDestroyedByPlayer(world, pos, block);
            block.getBlock().harvestBlock(world, player, pos, block, te, player.getHeldItemMainhand());
            // Workaround for dropping experience
            if (!silktouch)
                block.getBlock().dropXpOnBlockBreak(world, pos, exp);

            if (world.isRemote)
            {
                INetHandler handler = FMLClientHandler.instance().getClientPlayHandler();
                if (handler != null && handler instanceof NetHandlerPlayClient)
                {
                    NetHandlerPlayClient handlerClient = (NetHandlerPlayClient) handler;
                    handlerClient.sendPacket(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, pos, Minecraft.getMinecraft().objectMouseOver.sideHit));
                    // ??? handlerClient.sendPacket(new CPacketPlayerDigging(Action.START_DESTROY_, pos, Minecraft.getMinecraft().objectMouseOver.sideHit));
                }
            }
            else if (Config.noisyBlocks)
            {
                world.playEvent(2001, pos, Block.getStateId(block));
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
        	//toolClasses = MekanismCompat.addMekanismToolToClasses(toolClasses);
        }
    }

    private boolean canHarvestBlock(EntityPlayer player, IBlockState orig, IBlockState state, BlockPos pos)
    {
        ItemStack current = player.getHeldItem(EnumHand.MAIN_HAND);
        if (current == null)
            return false;

        String toolClass = getToolClass(current.getItem().getClass());
        if (toolClass == null)
            return false;

        float hardness = state.getBlockHardness(player.worldObj, pos);
        float digSpeed = ((ItemTool) current.getItem()).getStrVsBlock(current, state);
       
        // It works. It just does.
        return (digSpeed > 1.0F && state.getBlock().getHarvestLevel(state) <= ((ItemTool) current.getItem()).getHarvestLevel(current, toolClass) && hardness >= 0 && orig
                .getBlockHardness(player.worldObj, pos) >= hardness - 1.5);
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
        ItemStack current = player.getHeldItemMainhand();

        for (int i = 0; i < blocks.length; i++)
        {
            BlockPos pos = event.getPos().add(blocks[i][0], blocks[i][1], blocks[i][2]);
            IBlockState currentBlock = event.getWorld().getBlockState(pos);
            if (canHoeHarvest(currentBlock))
            {
                mineBlock(event.getWorld(), pos, player, currentBlock);
                current.damageItem(1, player);
                if (current.getItemDamage() >= current.getMaxDamage())
                {
                    return;
                }
            }
        }
    }

    public static Material[] validHoeMaterials = { Material.PLANTS, Material.VINE };

    public boolean canHoeHarvest(IBlockState iBlockState)
    {
        for (Material i : validHoeMaterials)
        {
            if (i == iBlockState.getMaterial())
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
        if (!event.getEntityPlayer().worldObj.isRemote)
        {
            EntityPlayer fakePlayer = FakePlayerFactory.get((WorldServer) event.getEntityPlayer().worldObj, new GameProfile(null, "ToolUtils-SwordDummy"));
            if (event.getEntityPlayer() == fakePlayer) return;
            ItemStack current = event.getEntityPlayer().getHeldItemMainhand();
            if (current != null && ToolUpgrade.SWORD_AOE.isOn(current))
            {
                copy(event.getEntityPlayer(), fakePlayer);
                AxisAlignedBB bb = event.getTarget().getEntityBoundingBox();
                bb = bb.expand(2, 2, 2);
                List<EntityLivingBase> list = event.getEntity().worldObj.getEntitiesWithinAABB(EntityLivingBase.class, bb);
                for (EntityLivingBase entity : list)
                {
                    if (entity != event.getTarget() && entity != event.getEntityPlayer())
                    {
                        fakePlayer.attackTargetEntityWithCurrentItem(entity);
                        current.getItem().hitEntity(current, entity, event.getEntityPlayer());
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
        to.inventory.mainInventory[to.inventory.currentItem] = from.getHeldItemMainhand();
        to.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(from.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
    }
}
