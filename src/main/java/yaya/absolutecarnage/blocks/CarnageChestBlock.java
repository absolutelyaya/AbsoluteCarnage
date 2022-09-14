package yaya.absolutecarnage.blocks;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public class CarnageChestBlock extends ChestBlock
{
	final protected boolean waterloggable;
	final protected boolean singleOnly;
	final protected Class<? extends ChestBlockEntity> entityClass;
	
	public CarnageChestBlock(Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier,
							 Class<? extends ChestBlockEntity> entityClass, boolean waterloggable, boolean singleOnly)
	{
		super(settings, supplier);
		this.entityClass = entityClass;
		this.waterloggable = waterloggable;
		this.singleOnly = singleOnly;
	}
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		try
		{
			return entityClass.getConstructor(BlockPos.class, BlockState.class).newInstance(pos, state);
		}
		catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		if(singleOnly)
		{
			ChestType chestType = ChestType.SINGLE;
			Direction direction = ctx.getPlayerFacing().getOpposite();
			FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
			return this.getDefaultState().with(FACING, direction).with(CHEST_TYPE, chestType)
						   .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER && waterloggable);
		}
		else
			return super.getPlacementState(ctx);
	}
	
	@Override
	public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid)
	{
		return waterloggable;
	}
}
