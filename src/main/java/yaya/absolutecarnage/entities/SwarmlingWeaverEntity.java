package yaya.absolutecarnage.entities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import yaya.absolutecarnage.entities.projectile.WebProjectile;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Predicate;

public class SwarmlingWeaverEntity extends AbstractSwarmling implements SwarmEntity, IAnimatable
{
	private final AnimationFactory factory = new AnimationFactory(this);
	private static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("pose", true);
	private static final AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("walk", true);
	private static final AnimationBuilder FLEE_CEILING_ANIM = new AnimationBuilder().addAnimation("flee_ceiling", false);
	private static final AnimationBuilder BALL_ANIM = new AnimationBuilder().addAnimation("ball", true);
	protected static final TrackedData<Byte> ANIMATION = DataTracker.registerData(SwarmlingWeaverEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Optional<BlockPos>> ROPE_ATTACHMENT_POS = DataTracker.registerData(SwarmlingWeaverEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS);
	protected static final TrackedData<Integer> ROPE_CLIMB_TICKS = DataTracker.registerData(SwarmlingWeaverEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final byte ANIMATION_IDLE = 0;
	private static final byte ANIMATION_FLEE_CEILING = 1;
	private static final byte ANIMATION_BALL = 2;
	private static final EntityDimensions BALL_DIMENSIONS = new EntityDimensions(1, 2, true);
	
	final float walkspeed = 0.3f;
	
	public float alpha = 1f;
	
	BlockPos cachedRopeAttachment = null, ropeClimbDestination = null;
	boolean shootingWebs;
	
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
		goalSelector.add(0, new WeaverFleeGoal<>(this, PlayerEntity.class, 30f, 1.0f, 1.5f, (a) -> true));
		goalSelector.add(1, new WanderAroundGoal(this, 1.0));
		goalSelector.add(2, new LookAtEntityGoal(this, LivingEntity.class, 5));
		goalSelector.add(3, new LookAroundGoal(this));
		goalSelector.add(4, new WanderAroundFarGoal(this, 1.0));
		
		//targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
	}
	
	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
		this.dataTracker.startTracking(ROPE_ATTACHMENT_POS, Optional.empty());
		this.dataTracker.startTracking(ROPE_CLIMB_TICKS, 0);
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
	protected EntityNavigation createNavigation(World world)
	{
		return new SpiderNavigation(this, world);
	}
	
	///TODO: Shoot webs at players
	///TODO: Falling and impact animations
	///TODO: Spin reinforced webs inbetween sticky nest blocks
	
	public void startClimbingToCeiling(BlockPos pos)
	{
		getDataTracker().set(ANIMATION, ANIMATION_FLEE_CEILING);
		getDataTracker().set(ROPE_ATTACHMENT_POS, Optional.of(pos));
		setNoGravity(true);
		ropeClimbDestination = pos.down(4 + random.nextInt(4));
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
		projectile.setVelocity(e, f + h, g, 1.6f, 6.0f);
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
				controller.setAnimation(event.isMoving() ? WALK_ANIM : IDLE_ANIM);
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
		return dataTracker.get(ROPE_ATTACHMENT_POS).isPresent();
	}
	
	public Optional<BlockPos> getRopeAttachmentPos()
	{
		if(!hasRopeAttachmentPos())
			return Optional.empty();
		else if(world.isClient)
		{
			if(cachedRopeAttachment == null && dataTracker.get(ROPE_ATTACHMENT_POS).isPresent())
				cachedRopeAttachment = dataTracker.get(ROPE_ATTACHMENT_POS).get();
			return Optional.of(cachedRopeAttachment);
		}
		else
			return Optional.empty();
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
							getY() + getHeight() + random.nextDouble() * 0.1, getZ() + random.nextDouble() - 0.5 * getWidth(),
							random.nextDouble() - 0.5, 0.5, random.nextDouble() - 0.5);
				}
			}
		}
		
		if(ropeClimbDestination != null)
		{
			if(!hasNoGravity())
				ropeClimbDestination = null;
			else if(getPos().squaredDistanceTo(Vec3d.ofCenter(ropeClimbDestination)) > 0.1f && dataTracker.get(ROPE_CLIMB_TICKS) >= 27)
			{
				Vec3d dir = Vec3d.ofCenter(ropeClimbDestination).subtract(getPos()).normalize();
				setPosition(getPos().add(dir.multiply(walkspeed / 2)));
			}
			
			if(dataTracker.get(ROPE_CLIMB_TICKS) < 32)
				dataTracker.set(ROPE_CLIMB_TICKS, dataTracker.get(ROPE_CLIMB_TICKS) + 1);
		}
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
			dataTracker.set(ROPE_ATTACHMENT_POS, Optional.empty());
			dataTracker.set(ANIMATION, ANIMATION_IDLE);
			dataTracker.set(ROPE_CLIMB_TICKS, 0);
			setNoGravity(false);
			setAiDisabled(false);
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
	
	public static DefaultAttributeContainer.Builder setAttributes()
	{
		return HostileEntity.createMobAttributes()
					   .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0D)
					   .add(EntityAttributes.GENERIC_ARMOR, 2.0D)
					   .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
					   .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.5D);
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
			if(mob.getRandom().nextInt(2) == 0)
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
	
	private static class ShootWebGoal extends Goal
	{
		private final SwarmlingWeaverEntity mob;
		private LivingEntity target;
		int time;
		
		public ShootWebGoal(SwarmlingWeaverEntity mob)
		{
			this.mob = mob;
			this.setControls(EnumSet.of(Control.LOOK));
		}
		
		@Override
		public boolean canStart()
		{
			target = mob.getTarget();
			return target != null && mob.distanceTo(target) < 10f && mob.distanceTo(target) > 5f && mob.canSee(target) && mob.random.nextInt(16) == 0;
		}
		
		@Override
		public boolean shouldContinue()
		{
			return time > 0 && target != null && mob.distanceTo(target) < 10f && mob.distanceTo(target) > 5f && target.isAlive() && mob.canSee(target);
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
			
			switch(time)
			{
				case 7 -> {
					for (int i = 0; i < 3; i++)
						mob.shootWeb(target);
					mob.playSound(SoundEvents.ENTITY_LLAMA_SPIT, 1.0F, 0.6F / (mob.getRandom().nextFloat() * 0.4F + 0.8F));
				}
			}
		}
		
		@Override
		public void stop()
		{
			target = null;
			mob.shootingWebs = false;
		}
	}
}
