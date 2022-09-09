package yaya.absolutecarnage.entities;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import yaya.absolutecarnage.registries.EntityRegistry;
import yaya.absolutecarnage.registries.ItemRegistry;
import yaya.absolutecarnage.registries.StatusEffectRegistry;

import java.util.EnumSet;
import java.util.List;

public class WaterstriderEntity extends AnimalEntity implements IAnimatable
{
	final AnimationFactory factory = new AnimationFactory(this);
	private final static AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("pose", true);
	private final static AnimationBuilder THRUST_ANIM = new AnimationBuilder().addAnimation("thrust", false);
	protected static final TrackedData<Byte> ANIMATION = DataTracker.registerData(WaterstriderEntity.class, TrackedDataHandlerRegistry.BYTE);
	private final byte ANIMATION_IDLE = 0;
	private final byte ANIMATION_THRUST = 1;
	
	private Vec2f thrustVel = Vec2f.ZERO;
	private int thrustTimer, animTime;
	private float targetSpeed = 1f, speed = 1f;
	private LivingEntity target, grabbed;
	
	//TODO: Add Strider Eggs and Egg-laying behavior
	
	public WaterstriderEntity(EntityType<? extends AnimalEntity> entityType, World world)
	{
		super(entityType, world);
	}
	
	@Override
	protected void initGoals()
	{
		goalSelector.add(0, new DevourDrowningGoal(this));
		goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 4f));
		
		targetSelector.add(1, new MateGoal(this, 1f));
	}
	
	@Override
	public int getMaxLookYawChange()
	{
		return 180;
	}
	
	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
	}
	
	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity)
	{
		return EntityRegistry.WATERSTRIDER.create(world);
	}
	
	@Override
	public boolean isBreedingItem(ItemStack stack)
	{
		return stack.isOf(ItemRegistry.INSECT_EGG);
	}
	
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event)
	{
		event.getController().animationSpeed = speed;
		byte anim = dataTracker.get(ANIMATION);
		
		switch(anim)
		{
			case ANIMATION_IDLE -> event.getController().setAnimation(IDLE_ANIM);
			case ANIMATION_THRUST -> event.getController().setAnimation(THRUST_ANIM);
		}
		return PlayState.CONTINUE;
	}
	
	@Override
	public void registerControllers(AnimationData animationData)
	{
		AnimationController<WaterstriderEntity> controller = new AnimationController<>(this, "controller",
				2, this::predicate);
		animationData.addAnimationController(controller);
	}
	
	@Override
	public AnimationFactory getFactory()
	{
		return factory;
	}
	
	@Override
	public EntityGroup getGroup()
	{
		return EntityGroup.ARTHROPOD;
	}
	
	public static DefaultAttributeContainer.Builder setAttributes()
	{
		return HostileEntity.createMobAttributes()
					   .add(EntityAttributes.GENERIC_MAX_HEALTH, 16.0D)
					   .add(EntityAttributes.GENERIC_ARMOR, 4.0D)
					   .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D);
	}
	
	public static boolean canSpawn(EntityType<WaterstriderEntity> waterstriderEntityEntityType,
								   ServerWorldAccess serverWorldAccess, SpawnReason spawnReason, BlockPos blockPos, Random random)
	{
		return true;
	}
	
	@Override
	public void tickMovement()
	{
		super.tickMovement();
		LivingEntity target = getTarget();
		if(this.target != null)
			target = this.target;
		Vec3d pos = getPos();
		if(isSubmergedInWater())
			setPos(pos.x, pos.y + 0.55, pos.z);
		else if(isTouchingWater())
		{
			setVelocity(getVelocity().multiply(1, 0, 1));
			setPos(pos.x, Math.round(pos.y) - 1.0 / 16.0, pos.z);
		}
		
		targetSpeed = getTarget() != null ? 2.0f : 1.0f;
		
		if(targetSpeed != speed)
			thrustTimer = 0;
		
		if(thrustTimer <= 0)
			thrustTimer = (int)((60 + random.nextInt(120)) / speed);
		if(thrustTimer == (int)(34 / speed))
			setActiveAnimation(ANIMATION_THRUST);
		if(dataTracker.get(ANIMATION) == ANIMATION_THRUST)
		{
			if(animTime >= (int)(34 / speed))
				setActiveAnimation(ANIMATION_IDLE);
			if(animTime == (int)(20 / speed))
			{
				if(target != null && !target.isAlive())
					target = null;
				if(target == null)
				{
					double dir = random.nextInt(360);
					setRotation((float)dir - 90, 0);
					thrustVel = dirToVec(dir).multiply(0.05f);
				}
				else
				{
					Vec3d dir = target.getPos().subtract(pos).multiply(1f, 0f, 1f).normalize().multiply(0.05f);
					thrustVel = new Vec2f((float)dir.x, (float)dir.z);
				}
			}
		}
		thrustVel = thrustVel.multiply(isBaby() ? 0.5f : 0.75f);
		
		if(grabbed != null)
		{
			if(!grabbed.isAlive())
			{
				//devouring victim results in breeding readiness as if a food item was given.
				//this... sounds like it could lead to chaos lol
				setLoveTicks(600);
				grabbed = null;
			}
			else
			{
				Vec3d grabPos = new Vec3d(pos.x, pos.y, pos.z);
				Vec2f forward = dirToVec(headYaw + 90).multiply(1.75f);
				grabPos = grabPos.add(forward.x, 0, forward.y);
				grabbed.setPos(grabPos.x, grabPos.y, grabPos.z);
				grabbed.addStatusEffect(new StatusEffectInstance(StatusEffectRegistry.ACID, 120, 2));
			}
		}
	}
	
	void grabEntity(LivingEntity victim)
	{
		grabbed = victim;
		grabbed.damage(DamageSource.mob(this), 6f);
	}
	
	Vec2f dirToVec(double dir)
	{
		dir = Math.toRadians(dir);
		return new Vec2f((float)Math.cos(dir), (float)Math.sin(dir));
	}
	
	@Override
	protected void mobTick()
	{
		if(grabbed == null)
			thrustTimer--;
		animTime++;
		super.mobTick();
	}
	
	@Override
	public Vec3d getVelocity()
	{
		return super.getVelocity().add(thrustVel.x, 0, thrustVel.y);
	}
	
	void setActiveAnimation(byte animation)
	{
		dataTracker.set(ANIMATION, animation);
		animTime = 0;
		speed = targetSpeed;
	}
	
	@Override
	public int getMaxLookPitchChange()
	{
		return 0;
	}
	
	static class MateGoal extends AnimalMateGoal
	{
		public MateGoal(AnimalEntity animal, double chance)
		{
			super(animal, chance);
			this.setControls(EnumSet.of(Control.LOOK));
		}
		
		@Override
		public void stop()
		{
			super.stop();
			((WaterstriderEntity)animal).target = null;
		}
		
		@Override
		public boolean canStart()
		{
			boolean b = super.canStart();
			if(b)
				((WaterstriderEntity)animal).target = mate;
			return b;
		}
	}
	
	static class DevourDrowningGoal extends Goal
	{
		private static final TargetPredicate VALID_PREY = TargetPredicate.createNonAttackable().setBaseMaxDistance(8.0).ignoreVisibility();
		final WaterstriderEntity mob;
		
		AbstractSwarmling target;
		
		public DevourDrowningGoal(WaterstriderEntity mob)
		{
			this.mob = mob;
			this.setControls(EnumSet.of(Control.LOOK));
		}
		
		@Override
		public boolean canStart()
		{
			target = findVictim();
			return target != null && mob.grabbed == null && !mob.isBaby();
		}
		
		@Override
		public void tick()
		{
			mob.getLookControl().lookAt(target, 10.0F, (float)mob.getMaxLookPitchChange());
			mob.target = target;
			if(mob.squaredDistanceTo(target) < 9f)
				mob.grabEntity(target);
		}
		
		@Override
		public boolean shouldContinue()
		{
			return target != null && target.isAlive() && mob.grabbed == null;
		}
		
		@Override
		public void stop()
		{
			target = null;
		}
		
		@Nullable
		private AbstractSwarmling findVictim()
		{
			List<AbstractSwarmling> targets = mob.world.getTargets(AbstractSwarmling.class, VALID_PREY, mob, mob.getBoundingBox().expand(8.0));
			double nearestDistance = Double.MAX_VALUE;
			AbstractSwarmling selectedTarget = null;
			
			for (AbstractSwarmling target : targets)
			{
				if (((CarnageEntityAccessor)target).isDrowning() && mob.squaredDistanceTo(target) < nearestDistance)
				{
					selectedTarget = target;
					nearestDistance = mob.squaredDistanceTo(target);
				}
			}
			return selectedTarget;
		}
	}
}
