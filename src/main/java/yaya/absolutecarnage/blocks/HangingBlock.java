package yaya.absolutecarnage.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class HangingBlock extends Block
{
	public HangingBlock(Settings settings)
	{
		super(settings);
	}
	
	public PistonBehavior getPistonBehavior(BlockState state)
	{
		return PistonBehavior.DESTROY;
	}
	
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return Block.sideCoversSmallSquare(world, pos.up(), Direction.DOWN);
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
	{
		return !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : state;
	}
}
