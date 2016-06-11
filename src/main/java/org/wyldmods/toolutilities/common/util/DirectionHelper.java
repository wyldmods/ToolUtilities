package org.wyldmods.toolutilities.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DirectionHelper {

	public static RayTraceResult raytraceFromEntity(World world, Entity player, boolean par3, double range)
	{	// 100% borrowed from Tinkers Construct (CC0 license).
		float f = 1.0F;
		float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
		float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
		double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double) f;
		double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double) f;
		if (!world.isRemote && player instanceof EntityPlayer)
			d1 += 1.62D;
		double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) f;
		Vec3d vec3 = new Vec3d(d0, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		double d3 = range;
		if (player instanceof EntityPlayerMP)
		{
			d3 = ((EntityPlayerMP) player).interactionManager.getBlockReachDistance();
		}
		Vec3d vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
		return world.rayTraceBlocks(vec3, vec31, par3, !par3, par3);
	}


	public static int[][] mineArrayBottom = { { 1, 0, 1 }, { 1, 0, 0 }, { 1, 0, -1 }, { 0, 0, 1 }, { 0, 0, -1 }, { -1, 0, 1 }, { -1, 0, 0 }, { -1, 0, -1 } };
	public static int[][] mineArrayTop = { { 1, 0, 1 }, { 1, 0, 0 }, { 1, 0, -1 }, { 0, 0, 1 }, { 0, 0, -1 }, { -1, 0, 1 }, { -1, 0, 0 }, { -1, 0, -1 } };
	public static int[][] mineArraySouth = { { -1, 1, 0 }, { -1, 0, 0 }, { -1, -1, 0 }, { 0, 1, 0 }, { 0, -1, 0 }, { 1, 1, 0 }, { 1, 0, 0 }, { 1, -1, 0 } };
	public static int[][] mineArrayNorth = { { -1, 1, 0 }, { -1, 0, 0 }, { -1, -1, 0 }, { 0, 1, 0 }, { 0, -1, 0 }, { 1, 1, 0 }, { 1, 0, 0 }, { 1, -1, 0 } };
	public static int[][] mineArrayEast = { { 0, 1, 1 }, { 0, 0, 1 }, { 0, -1, 1 }, { 0, 1, 0 }, { 0, -1, 0 }, { 0, 1, -1 }, { 0, 0, -1 }, { 0, -1, -1 } };
	public static int[][] mineArrayWest = { { 0, 1, 1 }, { 0, 0, 1 }, { 0, -1, 1 }, { 0, 1, 0 }, { 0, -1, 0 }, { 0, 1, -1 }, { 0, 0, -1 }, { 0, -1, -1 } };
	public static int[][] mineDefault = { {0, 0, 0} };

	public static int[][] get3x3MiningCoordinates(RayTraceResult mop)
	{
		switch (mop.sideHit)
		{
		case DOWN:
			return mineArrayBottom;
		case UP:
			return mineArrayTop;
		case SOUTH:
			return mineArraySouth;
		case NORTH:
			return mineArrayNorth;
		case EAST:
			return mineArrayEast;
		case WEST:
			return mineArrayWest;
		default:
			return mineDefault;
		}
	}

	public static int[][] mine1x3North = { {0, 0, 1}, {0, 0, -1} };
	public static int[][] mine1x3East = { {1, 0, 0}, {-1, 0, 0} };
	public static int[][] get1x3MiningCoordinatesForTopAndBottom(int side)
	{
		switch (side)
		{
		case 0:
			return mine1x3North;
		case 1:
			return mine1x3East;
		case 2:
			return mine1x3North;
		case 3:
			return mine1x3East;
		default:
			return mineDefault;
		}
	}

	/*
	 * 0 = South,
	 * 1 = West,
	 * 2 = North,
	 * 3 = East.
	 */
	public static int getPlayerDirection(EntityPlayer p)
	{
		return MathHelper.floor_double((double)(p.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	}

}
