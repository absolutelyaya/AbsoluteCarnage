package yaya.absolutecarnage.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Map;

public class WallDecal extends Block
{
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	private static final Map<Direction, VoxelShape> SHAPE;
	
	public WallDecal(Settings settings)
	{
		super(settings);
	}
	
	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
	{
		Direction direction = state.get(FACING);
		BlockPos blockPos = pos.offset(direction.getOpposite());
		BlockState blockState = world.getBlockState(blockPos);
		return blockState.isSideSolidFullSquare(world, blockPos, direction);
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
	{
		return direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : state;
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return SHAPE.get(state.get(FACING));
	}
	
	static
	{
		SHAPE = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH,
				Block.createCuboidShape(0.0D, 4.0D, 5.0D, 16.0D, 12.0D, 16.0D),
				Direction.SOUTH, Block.createCuboidShape(0.0D, 4.0D, 0.0D, 16.0D, 12.0D, 11.0D),
				Direction.WEST, Block.createCuboidShape(5.0D, 4.0D, 0.0D, 16.0D, 12.0D, 16.0D),
				Direction.EAST, Block.createCuboidShape(0.0D, 4.0D, 0.0D, 11.0D, 12.0D, 16.0D)));
	}
}
