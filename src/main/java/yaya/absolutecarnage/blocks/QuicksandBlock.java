package yaya.absolutecarnage.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class QuicksandBlock extends Block
{
	public static final IntProperty INDENT = IntProperty.of("indent", 0, 6);
	
	private final Random random = Random.create();
	
	public QuicksandBlock(Settings settings)
	{
		super(settings);
		setDefaultState(getStateManager().getDefaultState().with(INDENT, 1));
	}
	
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return getShape(state.get(INDENT));
	}
	
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}
	
	public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
		return getShape(state.get(INDENT));
	}
	
	public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}
	
	public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.empty();
	}
	
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
	{
		int indent = 0;
		if(world.isAir(pos.up()))
			indent = random.nextInt(4);
		world.setBlockState(pos, state.with(INDENT, indent));
		if(world.getBlockState(pos.down()).isOf(this))
			world.setBlockState(pos.down(), state.with(INDENT, 0));
		super.onPlaced(world, pos, state, placer, itemStack);
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
	{
		if(direction.equals(Direction.DOWN) && neighborState.isOf(this))
		{
			world.setBlockState(neighborPos, state.with(INDENT, 0), 1);
		}
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}
	
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(INDENT);
	}
	
	VoxelShape getShape(int indent)
	{
		return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0 - indent * 2, 16.0);
	}
	
	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack)
	{
		if(player.getAbilities().creativeMode || state.get(INDENT) == 6)
			super.afterBreak(world, player, pos, state, blockEntity, stack);
		else
			indent(world, player, pos, state);
	}
	
	void indent(World world, PlayerEntity player, BlockPos pos, BlockState state)
	{
		if(world.isAir(pos.up()))
		{
			if(state.get(INDENT) == 6)
				world.breakBlock(pos, true);
			else
			{
				world.setBlockState(pos, state.with(INDENT, Math.min(state.get(INDENT) + random.nextInt(2) + 1, 6)));
				spawnBreakParticles(world, player, pos, state);
			}
		}
		else if(world.getBlockState(pos.up()).isOf(this))
		{
			world.setBlockState(pos, state);
			indent(world, player, pos.up(), world.getBlockState(pos.up()));
		}
	}
	
	@Override
	public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance)
	{
		//no fall damage
	}
	
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		entity.slowMovement(state, new Vec3d(0.9, 0.5, 0.9));
		boolean moved = !entity.getPos().equals(new Vec3d(entity.lastRenderX, entity.lastRenderY, entity.lastRenderZ));
		if (world.isClient && random.nextInt(3) == 0 && moved && world.isAir(pos.up()))
		{
			world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
					(random.nextFloat() - 0.5f) * 0.5f + entity.getX(),
					pos.getY() + getShape(state.get(INDENT)).getMax(Direction.Axis.Y) + 0.1,
					(random.nextFloat() - 0.5f) * 0.5f + entity.getZ(),
					(random.nextFloat() - 0.5f) * 0.1f, 0.05f, (random.nextFloat() - 0.5f) * 0.1f);
		}
	}
	
	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
		return false;
	}
	
	@Override
	public int getOpacity(BlockState state, BlockView world, BlockPos pos)
	{
		return 10;
	}
}
