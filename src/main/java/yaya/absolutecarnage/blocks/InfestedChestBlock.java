package yaya.absolutecarnage.blocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import yaya.absolutecarnage.entities.blocks.InfestedChestBlockEntity;
import yaya.absolutecarnage.registries.BlockEntityRegistry;

public class InfestedChestBlock extends ChestBlock
{
	public InfestedChestBlock(Settings settings)
	{
		super(settings, () -> BlockEntityRegistry.INFESTED_CHEST);
	}
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new InfestedChestBlockEntity(pos, state);
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	//no double chests of this type pls thx
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		ChestType chestType = ChestType.SINGLE;
		Direction direction = ctx.getPlayerFacing().getOpposite();
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		return this.getDefaultState().with(FACING, direction).with(CHEST_TYPE, chestType).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}
	
	//no waterlogging pls, don't want to drown the little critters
	@Override
	public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid)
	{
		return false;
	}
}
