package yaya.absolutecarnage.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class QuicksandBlock extends Block
{
	public static final IntProperty INDENT = IntProperty.of("indent", 0, 6);
	
	private final Random random = Random.create();
	private final Map<Entity, Integer> victimUnmovingTime = new HashMap<>();
	
	public QuicksandBlock(Settings settings)
	{
		super(settings);
		setDefaultState(getStateManager().getDefaultState().with(INDENT, 1));
	}
	
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return getShape(state.get(INDENT));
	}
	
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		VoxelShape shape = VoxelShapes.empty();
		if (context instanceof EntityShapeContext entityContext)
		{
			Entity entity = entityContext.getEntity();
			if (entity != null)
			{
				if (entity.fallDistance > 2.5F)
					shape = getShape(state.get(INDENT) + 2);
				else if (entity instanceof FallingBlockEntity ||
								 canStepUp(getShape(state.get(INDENT)), pos, entityContext) && !context.isDescending())
					shape = getShape(state.get(INDENT));
				else
					shape = getColl(Math.max(entity.getY() - pos.getY(), 0), state);
				
				if(victimUnmovingTime.containsKey(entity))
				{
					//accelerate to full sinking speed over 5 seconds of not moving
					shape = shape.offset(0.0, -MathHelper.clampedLerp(0.0, 0.1, (victimUnmovingTime.get(entity) - 20) / 200.0), 0.0);
					if(entity instanceof PlayerEntity player)
						player.sendMessage(Text.of("unmoving time " + victimUnmovingTime.get(entity)), true);
				}
			}
		}
		return shape;
	}
	
	boolean canStepUp(VoxelShape shape, BlockPos pos, EntityShapeContext context)
	{
		Entity entity = context.getEntity();
		if(entity == null)
			return false;
		//TODO: fix stepup from inside other quicksand blocks \/
		if(entity.world.getBlockState(entity.getBlockPos()).isOf(this))
			return entity.getY() > pos.getY() + shape.getMax(Direction.Axis.Y);
		double delta = Math.abs(entity.getPos().y - pos.getY() - (shape.getMax(Direction.Axis.Y)));
		return delta < entity.stepHeight && !shape.getBoundingBox().offset(pos).intersects(entity.getBoundingBox());
	}
	
	public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos)
	{
		return getShape(state.get(INDENT));
	}
	
	public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return VoxelShapes.empty();
	}
	
	public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.empty();
	}
	
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
	{
		if(world.getBlockState(pos.down()).isOf(this))
			world.setBlockState(pos.down(), state.with(INDENT, 0));
		super.onPlaced(world, pos, state, placer, itemStack);
	}
	
	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		int indent = 0;
		World world = ctx.getWorld();
		BlockPos pos = ctx.getBlockPos();
		if(world.isAir(pos.up()))
			indent = random.nextInt(4);
		return stateManager.getDefaultState().with(INDENT, indent);
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
	{
		if(direction.equals(Direction.DOWN) && neighborState.isOf(this))
			world.setBlockState(neighborPos, state.with(INDENT, 0), 1);
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}
	
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(INDENT);
	}
	
	VoxelShape getShape(int indent)
	{
		return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0 - Math.max((Math.min(indent, 6)) * 2, 1), 16.0);
	}
	
	VoxelShape getColl(double y, BlockState state)
	{
		return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0,
				Math.min(y * 16.0, 16.0 - (Math.min(state.get(INDENT), 6)) * 2), 16.0);
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
		if(getShape(state.get(INDENT)).getBoundingBox().offset(pos).intersects(entity.getBoundingBox()))
			entity.slowMovement(state, new Vec3d(0.9, entity.getVelocity().y > 0f ? 0.75 : 0.5, 0.9));
		else
			entity.slowMovement(state, new Vec3d(0.95, 1.0, 0.95));
		boolean moved = !entity.getPos().equals(new Vec3d(entity.lastRenderX, entity.lastRenderY, entity.lastRenderZ));
		if (world.isClient && random.nextInt(2) == 0 && moved && world.isAir(pos.up()))
		{
			world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
					(random.nextFloat() - 0.5f) * 0.5f + entity.getX(),
					pos.getY() + getShape(state.get(INDENT)).getMax(Direction.Axis.Y) + 0.1,
					(random.nextFloat() - 0.5f) * 0.5f + entity.getZ(),
					(random.nextFloat() - 0.5f) * 0.1f, 0.05f, (random.nextFloat() - 0.5f) * 0.1f);
		}
		
		if(entity.getX() == entity.lastRenderX && entity.getZ() == entity.lastRenderZ)
			victimUnmovingTime.put(entity, victimUnmovingTime.getOrDefault(entity, 0) + 1);
		else
			victimUnmovingTime.put(entity, 0);
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		super.randomTick(state, world, pos, random);
		if(victimUnmovingTime.keySet().size() == 0)
			return;
		List<Entity> removal = new ArrayList<>();
		for (Entity e : victimUnmovingTime.keySet())
		{
			if(!VoxelShapes.fullCube().getBoundingBox().offset(pos).intersects(e.getBoundingBox()))
				removal.add(e);
		}
		removal.forEach(victimUnmovingTime::remove);
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
