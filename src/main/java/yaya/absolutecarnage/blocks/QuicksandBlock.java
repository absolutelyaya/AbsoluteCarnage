package yaya.absolutecarnage.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
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
import yaya.absolutecarnage.registries.ItemRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Accidentally made Quicksand the most complicated block in the game (to my knowledge). Whoops!
@SuppressWarnings("deprecation")
public class QuicksandBlock extends Block
{
	public static final IntProperty INDENT = IntProperty.of("indent", 0, 6);
	public static final BooleanProperty SUPPORTED = BooleanProperty.of("supported");
	
	private final Random random = Random.create();
	private final Map<Entity, Integer> victimContactTime = new HashMap<>();
	
	public QuicksandBlock(Settings settings)
	{
		super(settings);
		setDefaultState(getStateManager().getDefaultState().with(SUPPORTED, true).with(INDENT, 6));
	}
	
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(INDENT).add(SUPPORTED);
	}
	
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return getOutline(state.get(INDENT));
	}
	
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		if(!state.get(SUPPORTED))
			return VoxelShapes.empty();
		
		VoxelShape shape = VoxelShapes.empty();
		if (context instanceof EntityShapeContext entityContext)
		{
			Entity entity = entityContext.getEntity();
			if (entity != null)
			{
				if (entity.fallDistance > 2F)
				{
					shape = getShape(state.get(INDENT) + 3);
					victimContactTime.put(entity, 600);
				}
				else if (entity instanceof FallingBlockEntity ||
								 canStepUp(getShape(state.get(INDENT)), pos, entityContext) && !context.isDescending()
										 && victimContactTime.getOrDefault(entity, 0) < 60)
					shape = getShape(state.get(INDENT));
				else
					shape = getColl(Math.max(entity.getY() - pos.getY(), 0), state);
				
				if(victimContactTime.containsKey(entity))
				{
					//accelerate to full sinking speed over certain (unstable) period of not moving
					shape = shape.offset(0.0, -MathHelper.clampedLerp(0.0, 0.01, (victimContactTime.get(entity) - 60) / 400.0), 0.0);
				}
			}
		}
		return shape;
	}
	
	boolean canStepUp(VoxelShape shape, BlockPos pos, EntityShapeContext context)
	{
		Entity entity = context.getEntity();
		if(entity == null)
			return true;
		if(entity.world.getBlockState(entity.getBlockPos()).isOf(this))
			return isOnTop(entity);
		double delta = Math.abs(entity.getPos().y - pos.getY() - (shape.getMax(Direction.Axis.Y)));
		return delta < entity.stepHeight && !shape.getBoundingBox().offset(pos).intersects(entity.getBoundingBox());
	}
	
	boolean isOnTop(Entity entity)
	{
		BlockState state = entity.getBlockStateAtPos();
		if(!state.isOf(this))
			return true;
		return entity.getY() >= entity.getBlockPos().getY() + getShape(state.get(INDENT)).getMax(Direction.Axis.Y);
	}
	
	public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos)
	{
		return getShape(state.get(INDENT));
	}
	
	public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return VoxelShapes.empty();
	}
	
	public boolean hasSidedTransparency(BlockState state) {
		return true;
	}
	
	public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.empty();
	}
	
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
	{
		if(world.getBlockState(pos.down()).isOf(this) && placer instanceof PlayerEntity && ((PlayerEntity)placer).getAbilities().creativeMode)
			world.setBlockState(pos.down(), state.with(INDENT, 0));
		world.setBlockState(pos, updateSupported(state, world, pos));
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
		return updateSupported(stateManager.getDefaultState().with(INDENT, indent), world, pos);
	}
	
	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context)
	{
		return state.isOf(this) && state.get(INDENT) != 0 && context.getSide().equals(Direction.UP)
					   && context.getStack().isOf(ItemRegistry.SAND_BAG);
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
	{
		if(direction.equals(Direction.DOWN) && neighborState.isOf(this))
			world.setBlockState(neighborPos, state.with(INDENT, 0), 1);
		state = updateSupported(state, world, pos);
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}
	
	private BlockState updateSupported(BlockState state, WorldAccess world, BlockPos pos)
	{
		boolean b = true;
		for (int i = 0; i < 5; i++)
		{
			BlockState s = world.getBlockState(pos.down(i));
			if(s.isSolidBlock(world, pos.down(i)) && !s.isOf(this))
				break;
			if(s.isAir())
				b = false;
		}
		return state.with(SUPPORTED, b);
	}
	
	public VoxelShape getShape(int indent)
	{
		return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0 - Math.max((Math.min(indent, 6)) * 2, 1), 16.0);
	}
	
	public VoxelShape getOutline(int indent)
	{
		return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0 - (Math.min(indent, 6) * 2), 16.0);
	}
	
	VoxelShape getColl(double y, BlockState state)
	{
		if(y <= 0)
			return VoxelShapes.empty();
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
				dropStacks(state, world, pos);
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
		entity.handleFallDamage(fallDistance, 0.5F, DamageSource.FALL);
	}
	
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		if(!isOnTop(entity))
			entity.slowMovement(state, new Vec3d(0.3, entity.getVelocity().y > 0f ? 1 : 0.25, 0.3));
		boolean moved = !entity.getPos().equals(new Vec3d(entity.lastRenderX, entity.lastRenderY, entity.lastRenderZ));
		if (world.isClient && random.nextInt(2) == 0 && moved && world.isAir(pos.up()) && !isOnTop(entity))
		{
			world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
					(random.nextFloat() - 0.5f) * 0.5f + entity.getX(),
					pos.getY() + getShape(state.get(INDENT)).getMax(Direction.Axis.Y) + 0.1,
					(random.nextFloat() - 0.5f) * 0.5f + entity.getZ(),
					(random.nextFloat() - 0.5f) * 0.1f, 0.05f, (random.nextFloat() - 0.5f) * 0.1f);
		}
		
		if(entity.getX() == entity.lastRenderX && entity.getY() <= entity.lastRenderY && entity.getZ() == entity.lastRenderZ)
			victimContactTime.put(entity, victimContactTime.getOrDefault(entity, 0) + 1);
		else
			victimContactTime.put(entity, 0);
	}
	
	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
		return false;
	}
	
	@Override
	public int getOpacity(BlockState state, BlockView world, BlockPos pos)
	{
		return state.get(INDENT) == 0 ? world.getMaxLightLevel() : 0;
	}
	
	@Override
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos)
	{
		return state.get(INDENT) == 0 ? 0.2f : 1.0f;
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		if(victimContactTime.keySet().size() == 0)
			return;
		List<Entity> removal = new ArrayList<>();
		for (Entity e : victimContactTime.keySet())
		{
			if(!VoxelShapes.fullCube().getBoundingBox().offset(pos).intersects(e.getBoundingBox()))
				removal.add(e);
		}
		removal.forEach(victimContactTime::remove);
	}
	
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
	{
		world.createAndScheduleBlockTick(pos, this, 20);
	}
	
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
	{
		return new ItemStack(ItemRegistry.SAND_BAG);
	}
}
