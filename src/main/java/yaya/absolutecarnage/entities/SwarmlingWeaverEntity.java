package yaya.absolutecarnage.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import yaya.absolutecarnage.entities.projectile.WebProjectile;
import yaya.absolutecarnage.particles.GoopStringParticleEffect;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class SwarmlingWeaverEntity extends AbstractSwarmling implements SwarmEntity, IAnimatable
{
	private final AnimationFactory factory = new AnimationFactory(this);
	private static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("pose", true);
	private static final AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("walk", true);
	private static final AnimationBuilder FLEE_CEILING_ANIM = new AnimationBuilder().addAnimation("flee_ceiling", false);
	private static final AnimationBuilder BALL_ANIM = new AnimationBuilder().addAnimation("ball", true);
	protected static final TrackedData<Byte> ANIMATION = DataTracker.registerData(SwarmlingWeaverEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<BlockPos> ROPE_ATTACHMENT_POS = DataTracker.registerData(SwarmlingWeaverEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
	protected static final TrackedData<BlockPos> ROPE_CLIMB_DEST = DataTracker.registerData(SwarmlingWeaverEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
	protected static final TrackedData<Integer> ROPE_CLIMB_TICKS = DataTracker.registerData(SwarmlingWeaverEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected static final TrackedData<Integer> WEB_COOLDOWN = DataTracker.registerData(SwarmlingWeaverEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final TrackedData<Float> CLIMBING_ROTATION = DataTracker.registerData(SwarmlingWeaverEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final byte ANIMATION_IDLE = 0;
	private static final byte ANIMATION_FLEE_CEILING = 1;
	private static final byte ANIMATION_BALL = 2;
	private static final EntityDimensions BALL_DIMENSIONS = new EntityDimensions(1, 2, true);
	private static final float WALKSPEED = 0.3f;
	
	public float alpha = 1f, lastClimbingRot;
	
	BlockPos cachedRopeAttachment = null;
	boolean shootingWebs, shouldClimbToCeiling;
	
	public SwarmlingWeaverEntity(EntityType<? extends HostileEntity> entityType, World world)
	{
		super(entityType, world);
		this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
		this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
		this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 16.0F);
		this.setPathfindingPenalty(PathNodeType.DANGER_CACTUS, -1.0F);
	}
	
	@Override
	protected void initGoals()
	{
		goalSelector.add(0, new ShootWebGoal<>(this, PlayerEntity.class, 6));
		goalSelector.add(1, new WeaverFleeGoal<>(this, PlayerEntity.class, 20f, 1.0f, 1.5f, (a) -> true));
		goalSelector.add(2, new WanderAroundGoal(this, 1.0));
		goalSelector.add(3, new LookAtEntityGoal(this, LivingEntity.class, 5));
		goalSelector.add(4, new LookAroundGoal(this));
		goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
		
		//targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
	}
	
	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
		this.dataTracker.startTracking(ROPE_ATTACHMENT_POS, null);
		this.dataTracker.startTracking(ROPE_CLIMB_DEST, null);
		this.dataTracker.startTracking(ROPE_CLIMB_TICKS, 0);
		this.dataTracker.startTracking(WEB_COOLDOWN, 0);
		this.dataTracker.startTracking(CLIMBING_ROTATION, 0f);
	}
	
	public static DefaultAttributeContainer.Builder setAttributes()
	{
		return HostileEntity.createMobAttributes()
					   .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0D)
					   .add(EntityAttributes.GENERIC_ARMOR, 2.0D)
					   .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, WALKSPEED)
					   .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.5D);
	}
	
	@Override
	public void onTrackedDataSet(TrackedData<?> data)
	{
		super.onTrackedDataSet(data);
		if(data.equals(ROPE_ATTACHMENT_POS))
			cachedRopeAttachment = null;
		if(data.equals(ANIMATION))
			dataTracker.set(ROPE_CLIMB_TICKS, 0);
	}
	
	@Override
	public void writeCustomDataToNbt(NbtCompound nbt)
	{
		super.writeCustomDataToNbt(nbt);
		if(hasRopeAttachmentPos())
		{
			BlockPos pos = getRopeAttachmentPos();
			nbt.putIntArray("RopeAttachmentPos", List.of(pos.getX(), pos.getY(), pos.getZ()));
		}
		else
			nbt.putIntArray("RopeAttachmentPos", List.of());
		nbt.putInt("RopeClimbingTicks", dataTracker.get(ROPE_CLIMB_TICKS));
	}
	
	@Override
	public void readCustomDataFromNbt(NbtCompound nbt)
	{
		super.readCustomDataFromNbt(nbt);
		if(nbt.contains("RopeAttachmentPos"))
		{
			int[] pos = nbt.getIntArray("RopeAttachmentPos");
			if(pos.length == 3)
				dataTracker.set(ROPE_ATTACHMENT_POS, new BlockPos(pos[0], pos[1], pos[2]));
		}
		if(nbt.contains("RopeClimbingTicks"))
			dataTracker.set(ROPE_CLIMB_TICKS, nbt.getInt("RopeClimbingTicks"));
	}
	
	@Override
	protected EntityNavigation createNavigation(World world)
	{
		return new SpiderNavigation(this, world);
	}
	
	///TODO: Falling and impact animations
	///TODO: Spin reinforced webs inbetween sticky nest blocks
	///TODO: randomly go up to/come down from ceiling behavior
	///TODO: interact with clusters behavior
	
	public void startClimbingToCeiling(BlockPos pos)
	{
		dataTracker.set(ANIMATION, ANIMATION_FLEE_CEILING);
		dataTracker.set(ROPE_ATTACHMENT_POS, pos);
		dataTracker.set(ROPE_CLIMB_DEST, pos.down(4 + random.nextInt(4)));
		dataTracker.set(WEB_COOLDOWN, 0);
		setNoGravity(true);
		
		setAiDisabled(true);
	}
	
	public void shootWeb(LivingEntity target)
	{
		WebProjectile projectile = WebProjectile.spawn(this, world);
		double d = target.getEyeY() - 2;
		double e = target.getX() - this.getX();
		double f = d - projectile.getY();
		double g = target.getZ() - this.getZ();
		double h = Math.sqrt(e * e + g * g) * 0.2;
		projectile.setVelocity(e, f + h, g, 0.75f, 6.0f);
		this.world.spawnEntity(projectile);
	}
	
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event)
	{
		byte anim = dataTracker.get(ANIMATION);
		AnimationController<?> controller = event.getController();
		
		controller.setAnimationSpeed(1f);
		switch (anim)
		{
			case ANIMATION_IDLE ->
			{
				controller.setAnimationSpeed(getVelocity().horizontalLengthSquared() > 0.03 ? 2f : 1f);
				controller.setAnimation(event.isMoving() || dataTracker.get(CLIMBING_ROTATION) > 0f ? WALK_ANIM : IDLE_ANIM);
			}
			case ANIMATION_FLEE_CEILING ->
			{
				controller.setAnimation(FLEE_CEILING_ANIM);
				if(dataTracker.get(ROPE_CLIMB_TICKS) >= 27)
					dataTracker.set(ANIMATION, ANIMATION_BALL);
			}
			case ANIMATION_BALL -> controller.setAnimation(BALL_ANIM);
		}
		return PlayState.CONTINUE;
	}
	
	@Override
	public void registerControllers(AnimationData animationData)
	{
		AnimationController<SwarmlingWeaverEntity> controller = new AnimationController<>(this, "controller",
				2, this::predicate);
		animationData.addAnimationController(controller);
	}
	
	@Override
	public AnimationFactory getFactory()
	{
		return factory;
	}
	
	@Override
	public boolean isClimbing()
	{
		return horizontalCollision;
	}
	
	public boolean isRopeClimbing()
	{
		return dataTracker.get(ROPE_CLIMB_TICKS) >= 27;
	}
	
	public boolean hasRopeAttachmentPos()
	{
		return dataTracker.get(ROPE_ATTACHMENT_POS) != null;
	}
	
	public boolean shouldClimbToCeiling()
	{
		return shouldClimbToCeiling;
	}
	
	public int getRopeClimbingTicks()
	{
		return dataTracker.get(ROPE_CLIMB_TICKS);
	}
	
	public BlockPos getRopeAttachmentPos()
	{
		if(!hasRopeAttachmentPos())
			return null;
		else if(world.isClient)
		{
			if(cachedRopeAttachment == null && dataTracker.get(ROPE_ATTACHMENT_POS) != null)
				cachedRopeAttachment = dataTracker.get(ROPE_ATTACHMENT_POS);
			return cachedRopeAttachment;
		}
		else
			return dataTracker.get(ROPE_ATTACHMENT_POS);
	}
	
	@Override
	public void tickMovement()
	{
		super.tickMovement();
		if (getVelocity().horizontalLengthSquared() > 0.03 && random.nextInt(5) == 0)
		{
			BlockState blockState = world.getBlockState(new BlockPos(getBlockX(), getY() - 0.2, getBlockZ()));
			Vec3d inverseMoveDir = getVelocity().multiply(-1.0, 0.0, -1.0).normalize();
			if (!blockState.isAir())
			{
				for (int i = 0; i < 2 + random.nextInt(4); i++)
				{
					world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState),
							getX() + random.nextDouble() - 0.5 * getWidth(),
							getY() + 0.1, getZ() + random.nextDouble() - 0.5 * getWidth(),
							inverseMoveDir.x * 0.5, 0.5, inverseMoveDir.z * 0.5);
				}
			}
			
			if(random.nextInt(6) == 0)
			{
				for (int i = 0; i < 4 + random.nextInt(2); i++)
				{
					world.addParticle(ParticleTypes.SPLASH,
							getX() + random.nextDouble() - 0.5 * getWidth(),
							getY() + getHeight() + random.nextDouble() * 0.1,
							getZ() + random.nextDouble() - 0.5 * getWidth(),
							random.nextDouble() - 0.5, 0.5, random.nextDouble() - 0.5);
				}
			}
		}
		
		BlockPos ropeClimbDestination = dataTracker.get(ROPE_CLIMB_DEST);
		if(ropeClimbDestination != null)
		{
			if(!hasNoGravity())
				dataTracker.set(ROPE_CLIMB_DEST, null);
			else if(getPos().squaredDistanceTo(Vec3d.ofCenter(ropeClimbDestination)) > 0.1f && getRopeClimbingTicks() >= 27)
			{
				Vec3d dir = Vec3d.ofCenter(ropeClimbDestination).subtract(getPos()).normalize();
				setPosition(getPos().add(dir.multiply(WALKSPEED / 2)));
			}
			
			if(dataTracker.get(ROPE_CLIMB_TICKS) < 32)
				dataTracker.set(ROPE_CLIMB_TICKS, dataTracker.get(ROPE_CLIMB_TICKS) + 1);
			if(dataTracker.get(CLIMBING_ROTATION) != 0f)
				dataTracker.set(CLIMBING_ROTATION, 0f);
			
			if(getRopeClimbingTicks() == 32 && random.nextInt(32) == 0)
			{
				Vec3d pos = new Vec3d(getX() + random.nextFloat() * 0.5 - 0.25, getY(), getZ() + random.nextFloat() * 0.5 - 0.25);
				world.addParticle(new GoopStringParticleEffect(new Vec3f(0.83f, 0.76f, 0.58f), 0.25f),
						pos.x, pos.y, pos.z, 0f, 0f, 0f);
			}
		}
	}
	
	@Override
	protected void mobTick() //NOTE this doesn't get executed while NoAI is true.
	{
		super.mobTick();
		
		if(isClimbing() && dataTracker.get(CLIMBING_ROTATION) < 1f)
			dataTracker.set(CLIMBING_ROTATION, Math.min(dataTracker.get(CLIMBING_ROTATION) + 0.1f, 1f));
		else if (!isClimbing() && dataTracker.get(CLIMBING_ROTATION) > 0f)
			dataTracker.set(CLIMBING_ROTATION, Math.max(dataTracker.get(CLIMBING_ROTATION) - 0.1f, 0f));
		
		if(dataTracker.get(WEB_COOLDOWN) > 0)
			dataTracker.set(WEB_COOLDOWN, Math.max(dataTracker.get(WEB_COOLDOWN) - 1, 0));
	}
	
	@Override
	public void slowMovement(BlockState state, Vec3d multiplier)
	{
		if (!state.isOf(Blocks.COBWEB))
			super.slowMovement(state, multiplier);
	}
	
	@Override
	protected double getGravityScale()
	{
		return 1.0;
	}
	
	@Override
	public boolean damage(DamageSource source, float amount)
	{
		if(hasRopeAttachmentPos() || hasNoGravity())
		{
			dataTracker.set(ROPE_ATTACHMENT_POS, null);
			dataTracker.set(ANIMATION, ANIMATION_IDLE);
			dataTracker.set(ROPE_CLIMB_TICKS, 0);
			setNoGravity(false);
			setAiDisabled(false);
			shouldClimbToCeiling = false;
			return super.damage(source, amount * 1.5f);
		}
		return super.damage(source, amount);
	}
	
	@Override
	public EntityDimensions getDimensions(EntityPose pose)
	{
		if(isRopeClimbing())
			return BALL_DIMENSIONS;
		return super.getDimensions(pose);
	}
	
	private static class WeaverFleeGoal<T extends LivingEntity> extends FleeEntityGoal<T>
	{
		public WeaverFleeGoal(PathAwareEntity fleeingEntity, Class<T> classToFleeFrom, float fleeDistance, double fleeSlowSpeed, double fleeFastSpeed, Predicate<LivingEntity> inclusionSelector)
		{
			super(fleeingEntity, classToFleeFrom, fleeDistance, fleeSlowSpeed, fleeFastSpeed, inclusionSelector);
		}
		
		@Override
		public void stop()
		{
			super.stop();
			//should climb to ceiling after close call encounter with player
			if(mob.getRandom().nextInt(2) == 0 && ((SwarmlingWeaverEntity)mob).shouldClimbToCeiling())
			{
				for (int i = 0; i < 32; i++)
				{
					BlockPos pos = mob.getBlockPos().up(i);
					if(!mob.world.getBlockState(pos).isAir())
					{
						if(i < 6)
							return;
						((SwarmlingWeaverEntity)mob).startClimbingToCeiling(pos);
						break;
					}
				}
			}
		}
	}
	
	private static class ShootWebGoal<T extends LivingEntity> extends Goal
	{
		private final SwarmlingWeaverEntity mob;
		private LivingEntity target;
		int time;
		float range;
		Class<T> fleeFromClass;
		TargetPredicate withinRangePredicate;
		
		public ShootWebGoal(SwarmlingWeaverEntity mob, Class<T> fleeFromClass, float range)
		{
			this.mob = mob;
			this.fleeFromClass = fleeFromClass;
			this.range = range;
			setControls(EnumSet.of(Control.LOOK, Control.MOVE));
			withinRangePredicate = TargetPredicate.createAttackable().setBaseMaxDistance(range);
		}
		
		@Override
		public boolean canStart()
		{
			if (mob.dataTracker.get(WEB_COOLDOWN) > 0 || mob.random.nextInt(8) > 0)
				return false; //fail fast! If these conditions aren't met, don't waste time finding a target.
			target = mob.world.getClosestEntity(this.mob.world.getEntitiesByClass(fleeFromClass,
					this.mob.getBoundingBox().expand(range, 3.0, range), (ignored) -> true), withinRangePredicate,
					this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ());
			return target != null && mob.canSee(target);
		}
		
		@Override
		public boolean shouldContinue()
		{
			return time > 0 && target != null && target.isAlive();
		}
		
		@Override
		public boolean canStop()
		{
			return !shouldContinue();
		}
		
		
		@Override
		public void start()
		{
			time = 40;
			mob.shootingWebs = true;
		}
		
		@Override
		public void tick()
		{
			time = Math.max(time - 1, 0);
			if(target == null)
				return;
			this.mob.getLookControl().lookAt(target);
			///TODO: figure out a way to back away from the target. Thing below doesn't work due to body rotation.
			this.mob.getMoveControl().moveTo(mob.getX(), mob.getY(), mob.getZ(), 0f);
			//if(mob.squaredDistanceTo(target) < 3f * 3f)
			//	this.mob.getMoveControl().strafeTo(-0.75f, 0f);
			
			if (time == 7) ///TODO: add animation and match timing
			{
				for (int i = 0; i < 3; i++)
					mob.shootWeb(target);
				///TODO: replace shooting sound
				mob.playSound(SoundEvents.ENTITY_LLAMA_SPIT, 1.0F, 0.6F / (mob.getRandom().nextFloat() * 0.4F + 0.8F));
				mob.dataTracker.set(WEB_COOLDOWN, 200); //10 second cooldown
			}
		}
		
		@Override
		public void stop()
		{
			target = null;
			mob.shootingWebs = false;
			mob.shouldClimbToCeiling = true;
			//speed up afterwards
			mob.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 100, 1));
		}
	}
}
