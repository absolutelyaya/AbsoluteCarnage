package yaya.absolutecarnage.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.AnimationState;
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
import java.util.Optional;
import java.util.function.Predicate;

public class SwarmlingWeaverEntity extends AbstractSwarmling implements SwarmEntity, IAnimatable
{
	private final AnimationFactory factory = new AnimationFactory(this);
	private static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("pose", true);
	private static final AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("walk", true);
	private static final AnimationBuilder FLEE_CEILING_ANIM = new AnimationBuilder().addAnimation("flee_ceiling", false);
	private static final AnimationBuilder BALL_ANIM = new AnimationBuilder().addAnimation("ball", true);
	private static final AnimationBuilder WEB_ATTACK_ANIM = new AnimationBuilder().addAnimation("web_attack", false);
	protected static final TrackedData<Byte> ANIMATION = DataTracker.registerData(SwarmlingWeaverEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<BlockPos> ROPE_ATTACHMENT_POS = DataTracker.registerData(SwarmlingWeaverEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
	protected static final TrackedData<BlockPos> ROPE_CLIMB_DEST = DataTracker.registerData(SwarmlingWeaverEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
	protected static final TrackedData<Integer> ANIM_TICKS = DataTracker.registerData(SwarmlingWeaverEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected static final TrackedData<Integer> WEB_COOLDOWN = DataTracker.registerData(SwarmlingWeaverEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected static final TrackedData<Integer> CLIMB_COOLDOWN = DataTracker.registerData(SwarmlingWeaverEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected static final TrackedData<Integer> REINFORCE_COOLDOWN = DataTracker.registerData(SwarmlingWeaverEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final TrackedData<Float> CLIMBING_ROTATION = DataTracker.registerData(SwarmlingWeaverEntity.class, TrackedDataHandlerRegistry.FLOAT);
	public static final TrackedData<Float> SYNCED_YAW = DataTracker.registerData(SwarmlingWeaverEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final byte ANIMATION_IDLE = 0;
	private static final byte ANIMATION_FLEE_CEILING = 1;
	private static final byte ANIMATION_BALL = 2;
	private static final byte ANIMATION_WEB_ATTACK = 3;
	private static final Box BALL_HITBOX = new Box(-0.5, 0.0, -0.5, 0.5, 2.0, 0.5);
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
		goalSelector.add(0, new ClimbToCeilingGoal(this));
		goalSelector.add(1, new ShootWebGoal<>(this, PlayerEntity.class, 6));
		goalSelector.add(2, new WeaverFleeGoal<>(this, PlayerEntity.class, 20f, 1.0f, 1.5f, (a) -> true));
		goalSelector.add(3, new WanderAroundGoal(this, 1.0));
		goalSelector.add(4, new LookAtEntityGoal(this, LivingEntity.class, 5));
		goalSelector.add(5, new LookAroundGoal(this));
		goalSelector.add(6, new ReinforceClusterGoal(this, 10.0));
		goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
		
		//targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
	}
	
	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
		this.dataTracker.startTracking(ROPE_ATTACHMENT_POS, null);
		this.dataTracker.startTracking(ROPE_CLIMB_DEST, null);
		this.dataTracker.startTracking(ANIM_TICKS, 0);
		this.dataTracker.startTracking(WEB_COOLDOWN, 0);
		this.dataTracker.startTracking(CLIMB_COOLDOWN, (30 + random.nextInt(300)) * 20);
		this.dataTracker.startTracking(REINFORCE_COOLDOWN, 0);
		this.dataTracker.startTracking(CLIMBING_ROTATION, 0f);
		this.dataTracker.startTracking(SYNCED_YAW, 0f);
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
			dataTracker.set(ANIM_TICKS, 0);
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
		nbt.putInt("RopeClimbingTicks", dataTracker.get(ANIM_TICKS));
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
			dataTracker.set(ANIM_TICKS, nbt.getInt("RopeClimbingTicks"));
	}
	
	@Override
	protected EntityNavigation createNavigation(World world)
	{
		return new SpiderNavigation(this, world);
	}
	
	///TODO: Falling and impact animations
	///TODO: Spin reinforced webs inbetween sticky nest blocks
	
	public void startClimbingToCeiling(BlockPos pos)
	{
		dataTracker.set(ANIMATION, ANIMATION_FLEE_CEILING);
		dataTracker.set(ROPE_ATTACHMENT_POS, pos);
		dataTracker.set(ROPE_CLIMB_DEST, pos.down(4 + random.nextInt(4)));
		dataTracker.set(WEB_COOLDOWN, 0);
		setNoGravity(true);
		
		//setAiDisabled(true);
	}
	
	public void stopClimbingToCeiling()
	{
		dataTracker.set(ROPE_ATTACHMENT_POS, null);
		dataTracker.set(ANIMATION, ANIMATION_IDLE);
		setNoGravity(false);
		//setAiDisabled(false);
		shouldClimbToCeiling = false;
	}
	
	public void shootWeb(LivingEntity target)
	{
		WebProjectile projectile = WebProjectile.spawn(this, world);
		Vec3d forward = Vec3d.fromPolar(0, headYaw);
		Vec3d right = Vec3d.fromPolar(0, headYaw + 90f);
		Vec3d pos = getPos().subtract(forward.multiply(0.3)).add(right);
		projectile.setPos(pos.x, pos.y + 0.3, pos.z);
		double targetHeight = target.getEyeY() - 2;
		Vec3d vel = new Vec3d(target.getX() - pos.x, targetHeight - projectile.getY(), target.getZ() - pos.z);
		double dist = vel.horizontalLength() * 0.2;
		projectile.setVelocity(vel.x, vel.y + dist, vel.z, 0.75f, 6.0f);
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
				if(dataTracker.get(ANIM_TICKS) >= 27)
					dataTracker.set(ANIMATION, ANIMATION_BALL);
			}
			case ANIMATION_BALL -> controller.setAnimation(BALL_ANIM);
			case ANIMATION_WEB_ATTACK ->
			{
				controller.setAnimation(WEB_ATTACK_ANIM);
				if(controller.getAnimationState() == AnimationState.Stopped)
					dataTracker.set(ANIMATION, ANIMATION_IDLE);
			}
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
		return (getAnimation() == ANIMATION_FLEE_CEILING  && getAnimationTicks() >= 27) || getAnimation() == ANIMATION_BALL;
	}
	
	public boolean hasRopeAttachmentPos()
	{
		return dataTracker.get(ROPE_ATTACHMENT_POS) != null;
	}
	
	//should climb to ceiling after close call encounter with player
	public boolean shouldClimbToCeiling()
	{
		return shouldClimbToCeiling;
	}
	
	public byte getAnimation()
	{
		return dataTracker.get(ANIMATION);
	}
	
	public int getAnimationTicks()
	{
		return dataTracker.get(ANIM_TICKS);
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
			Vec3d dest = new Vec3d(getX(), ropeClimbDestination.getY(), getZ());
			if(!hasNoGravity())
				dataTracker.set(ROPE_CLIMB_DEST, null);
			else if(getPos().squaredDistanceTo(dest) > 0.1f && getAnimationTicks() >= 27)
			{
				Vec3d dir = dest.subtract(getPos()).normalize();
				setPosition(getPos().add(dir.multiply(WALKSPEED / 2)));
			}
			
			if(dataTracker.get(ANIM_TICKS) < 32)
				dataTracker.set(ANIM_TICKS, dataTracker.get(ANIM_TICKS) + 1);
			if(dataTracker.get(CLIMBING_ROTATION) != 0f)
				dataTracker.set(CLIMBING_ROTATION, 0f);
			
			if(getAnimationTicks() == 32 && random.nextInt(32) == 0)
			{
				Vec3d pos = new Vec3d(getX() + random.nextFloat() * 0.5 - 0.25, getY(), getZ() + random.nextFloat() * 0.5 - 0.25);
				world.addParticle(new GoopStringParticleEffect(new Vec3f(0.83f, 0.76f, 0.58f), 0.25f),
						pos.x, pos.y, pos.z, 0f, 0f, 0f);
			}
		}
		
		if(getAnimation() == ANIMATION_WEB_ATTACK)
		{
			navigation.stop();
			setVelocity(0f, getVelocity().y < 0f ? getVelocity().y : 0f, 0f);
			Optional<Float> rot = lookControl.getTargetYaw();
			if(!world.isClient && rot.isPresent())
				dataTracker.set(SYNCED_YAW, rot.get());
			setHeadYaw(dataTracker.get(SYNCED_YAW));
			setBodyYaw(dataTracker.get(SYNCED_YAW));
		}
	}
	
	@Override
	protected void mobTick()
	{
		super.mobTick();
		
		if(isClimbing() && dataTracker.get(CLIMBING_ROTATION) < 1f)
			dataTracker.set(CLIMBING_ROTATION, Math.min(dataTracker.get(CLIMBING_ROTATION) + 0.1f, 1f));
		else if (!isClimbing() && dataTracker.get(CLIMBING_ROTATION) > 0f)
			dataTracker.set(CLIMBING_ROTATION, Math.max(dataTracker.get(CLIMBING_ROTATION) - 0.1f, 0f));
		
		cooldownTick(WEB_COOLDOWN);
		cooldownTick(CLIMB_COOLDOWN);
		cooldownTick(REINFORCE_COOLDOWN);
	}
	
	void cooldownTick(TrackedData<Integer> data)
	{
		if(dataTracker.get(data) > 0)
			dataTracker.set(data, Math.max(dataTracker.get(data) - 1, 0));
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
		if((hasRopeAttachmentPos() || hasNoGravity()) && source != DamageSource.IN_WALL)
		{
			stopClimbingToCeiling();
			return super.damage(source, amount * 1.5f);
		}
		return super.damage(source, amount);
	}
	
	@Override
	public Box getBoundingBox(EntityPose pose)
	{
		if(isRopeClimbing())
			return BALL_HITBOX;
		return super.getBoundingBox(pose);
	}
	
	@Override
	protected Box calculateBoundingBox()
	{
		if(isRopeClimbing())
			return BALL_HITBOX.offset(getPos());
		return super.calculateBoundingBox();
	}
	
	@Override
	public boolean isPushable()
	{
		return !isRopeClimbing();
	}
	
	private static class WeaverFleeGoal<T extends LivingEntity> extends FleeEntityGoal<T>
	{
		public WeaverFleeGoal(PathAwareEntity fleeingEntity, Class<T> classToFleeFrom, float fleeDistance,
							  double fleeSlowSpeed, double fleeFastSpeed, Predicate<LivingEntity> inclusionSelector)
		{
			super(fleeingEntity, classToFleeFrom, fleeDistance, fleeSlowSpeed, fleeFastSpeed, inclusionSelector);
		}
		
		@Override
		public boolean shouldContinue()
		{
			return super.shouldContinue() && mob.getDataTracker().get(ANIMATION) == ANIMATION_IDLE;
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
			return mob.dataTracker.get(ANIMATION) == ANIMATION_WEB_ATTACK && time <= 54 &&
						   target != null && target.isAlive();
		}
		
		@Override
		public boolean canStop()
		{
			return !shouldContinue();
		}
		
		
		@Override
		public void start()
		{
			time = 0;
			mob.setTarget(target);
			mob.shootingWebs = true;
			mob.dataTracker.set(ANIMATION, ANIMATION_WEB_ATTACK);
		}
		
		@Override
		public boolean shouldRunEveryTick()
		{
			return true;
		}
		
		@Override
		public void tick()
		{
			this.mob.getLookControl().lookAt(target);
			
			time++;
			
			if (time == 37 && target != null)
			{
				for (int i = 0; i < mob.world.getDifficulty().getId(); i++)
					mob.shootWeb(target);
				///TODO: replace shooting sound
				mob.playSound(SoundEvents.ENTITY_LLAMA_SPIT, 1.0F,
						0.6F / (mob.getRandom().nextFloat() * 0.4F + 0.8F));
				mob.dataTracker.set(WEB_COOLDOWN, 200); //10 second cooldown
			}
		}
		
		@Override
		public void stop()
		{
			mob.dataTracker.set(ANIMATION, ANIMATION_IDLE);
			target = null;
			mob.shootingWebs = false;
			mob.shouldClimbToCeiling = true;
			//speed up afterwards
			mob.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 100, 1));
		}
	}
	
	private static class ClimbToCeilingGoal extends Goal
	{
		private final SwarmlingWeaverEntity mob;
		
		BlockPos startPos, ceilingPos;
		boolean descending;
		int ticksHanging;
		
		public ClimbToCeilingGoal(SwarmlingWeaverEntity mob)
		{
			this.mob = mob;
			setControls(EnumSet.of(Control.LOOK, Control.MOVE));
		}
		
		@Override
		public boolean canStart()
		{
			if (!(mob.shouldClimbToCeiling() || (mob.random.nextInt(60) == 0 && mob.getDataTracker().get(CLIMB_COOLDOWN) == 0)))
				return false;
			
			for (int i = 0; i < 32; i++)
			{
				BlockPos pos = mob.getBlockPos().up(i);
				if(!mob.world.getBlockState(pos).isAir() && i >=6)
				{
					ceilingPos = pos;
					return true;
				}
			}
			return false;
		}
		
		@Override
		public void start()
		{
			startPos = mob.getBlockPos();
			ticksHanging = 0;
			descending = false;
			mob.startClimbingToCeiling(ceilingPos);
		}
		
		@Override
		public boolean shouldRunEveryTick()
		{
			return true;
		}
		
		@Override
		public void tick()
		{
			ticksHanging++;
			if((!descending && ticksHanging > 300 && ticksHanging % 20 == 0 && mob.random.nextInt(10) == 0) || mob.isInsideWall())
			{
				mob.getDataTracker().set(ROPE_CLIMB_DEST, startPos);
				descending = true;
			}
		}
		
		@Override
		public boolean canStop()
		{
			return descending && mob.squaredDistanceTo(Vec3d.ofCenter(startPos)) < 1f;
		}
		
		@Override
		public boolean shouldContinue()
		{
			return !canStop();
		}
		
		@Override
		public void stop()
		{
			mob.dataTracker.set(CLIMB_COOLDOWN, (120 + mob.random.nextInt(900)) * 20);
			mob.stopClimbingToCeiling();
		}
	}
	
	private static class ReinforceClusterGoal extends Goal
	{
		SwarmlingWeaverEntity mob;
		SwarmClusterEntity target;
		double range;
		TargetPredicate targetPredicate;
		int interactionTime;
		boolean success;
		
		public ReinforceClusterGoal(SwarmlingWeaverEntity mob, double range)
		{
			this.mob = mob;
			this.range = range;
			targetPredicate = TargetPredicate.createNonAttackable().setBaseMaxDistance(range)
									  .setPredicate((entity) -> !entity.hasStatusEffect(StatusEffects.RESISTANCE));
			setControls(EnumSet.of(Control.LOOK, Control.MOVE));
		}
		
		@Override
		public boolean canStart()
		{
			if (mob.random.nextInt(32) > 0 || mob.dataTracker.get(REINFORCE_COOLDOWN) > 0)
				return false;
			target = mob.world.getClosestEntity(this.mob.world.getEntitiesByClass(SwarmClusterEntity.class,
							this.mob.getBoundingBox().expand(range, 3.0, range), (ignored) -> true), targetPredicate,
					this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ());
			return target != null;
		}
		
		@Override
		public void start()
		{
			interactionTime = 0;
			success = false;
		}
		
		@Override
		public void tick()
		{
			mob.getLookControl().lookAt(target, 30f, 30f);
			mob.getNavigation().startMovingTo(target, 1f);
			
			double range = Math.pow(mob.getWidth() * 2, 2);
			double distance = mob.squaredDistanceTo(target);
			int difficulty = mob.world.getDifficulty().getId();
			if(distance < range)
			{
				interactionTime++;
				if(interactionTime > 32 - (difficulty * 6))
					target.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, (30 * 20) * difficulty), mob);
			}
			else if(interactionTime > 0)
				interactionTime = 0;
		}
		
		@Override
		public boolean canStop()
		{
			return !shouldContinue();
		}
		
		@Override
		public boolean shouldContinue()
		{
			return target != null && targetPredicate.test(mob, target);
		}
		
		@Override
		public void stop()
		{
			mob.dataTracker.set(REINFORCE_COOLDOWN, (60 * 20) * (4 - mob.world.getDifficulty().getId()));
		}
	}
}
