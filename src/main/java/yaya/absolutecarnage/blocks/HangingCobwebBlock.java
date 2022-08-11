package yaya.absolutecarnage.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class HangingCobwebBlock extends HangingBlock
{
	public HangingCobwebBlock(Settings settings)
	{
		super(settings);
	}
	
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		entity.slowMovement(state, new Vec3d(0.25, 0.05000000074505806, 0.25));
	}
}
