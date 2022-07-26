package yaya.absolutecarnage.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import yaya.absolutecarnage.registries.BlockRegistry;

import java.util.function.ToIntFunction;

public class DanglingEggBlock extends Block
{
	public static final IntProperty PART = IntProperty.of("part", 1, 3);
	
	public DanglingEggBlock(Settings settings)
	{
		super(settings);
	}
	
	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
	{
		return world.getBlockState(pos.up()).isSideSolidFullSquare(world, pos, Direction.DOWN) ||
					   world.getBlockState(pos.up()).isOf(BlockRegistry.DANGLING_EGG);
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
	{
		BlockState original = state;
		if(state.get(PART) == 2 && !world.getBlockState(pos.up()).isOf(BlockRegistry.DANGLING_EGG))
			state = state.with(PART, 1);
		if(direction == Direction.DOWN || direction == Direction.UP)
		{
			if(!world.getBlockState(pos.down()).isOf(BlockRegistry.DANGLING_EGG))
			{
				if(state.get(PART) == 1)
					state = Blocks.AIR.getDefaultState(); //only 2 long = break
				else
					state = state.with(PART, 3); //bottom
			}
			else
			{
				if(world.getBlockState(pos.up()).isOf(BlockRegistry.DANGLING_EGG))
					state = state.with(PART, 2); //middle
				else
					state = state.with(PART, 1); //top
			}
		}
		state = !canPlaceAt(state, world, pos) ? Blocks.AIR.getDefaultState() : state; //base removed = break
		if(!state.equals(original))
			world.updateNeighbors(pos, this);
		return state;
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(PART);
	}
	
	public static ToIntFunction<BlockState> getLuminanceSupplier(int luminance) {
		return (state) -> state.get(PART) == 3 ? luminance : 0;
	}
}
