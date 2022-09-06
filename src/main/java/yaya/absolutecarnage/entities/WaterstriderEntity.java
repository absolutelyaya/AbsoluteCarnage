package yaya.absolutecarnage.entities;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
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

import java.util.EnumSet;

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
	private LivingEntity mate;
	
	//TODO: Add Strider Eggs and Egg-laying behavior
	//TODO: Devour drowning Swarmling behavior
	
	public WaterstriderEntity(EntityType<? extends AnimalEntity> entityType, World world)
	{
		super(entityType, world);
	}
	
	@Override
	protected void initGoals()
	{
		goalSelector.add(0, new LookAtEntityGoal(this, PlayerEntity.class, 4f));
		
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
	
	public static boolean canSpawn(EntityType<WaterstriderEntity> waterstriderEntityEntityType, ServerWorldAccess serverWorldAccess, SpawnReason spawnReason, BlockPos blockPos, Random random)
	{
		return true;
	}
	
	@Override
	public void tickMovement()
	{
		super.tickMovement();
		LivingEntity target = getTarget();
		if(mate != null)
			target = mate;
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
				if(target == null)
				{
					double dir = random.nextInt(360);
					setRotation((float)dir - 90, 0);
					dir = Math.toRadians(dir);
					thrustVel = new Vec2f((float)Math.cos(dir), (float)Math.sin(dir)).multiply(0.05f);
				}
				else
				{
					Vec3d dir = target.getPos().subtract(pos).multiply(1f, 0f, 1f).normalize().multiply(0.05f);
					thrustVel = new Vec2f((float)dir.x, (float)dir.z);
					System.out.println(thrustVel.x + " | " + thrustVel.y);
				}
			}
		}
		thrustVel = thrustVel.multiply(isBaby() ? 0.5f : 0.75f);
	}
	
	@Override
	protected void mobTick()
	{
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
			((WaterstriderEntity)animal).mate = null;
		}
		
		@Override
		public boolean canStart()
		{
			boolean b = super.canStart();
			if(b)
				((WaterstriderEntity)animal).mate = mate;
			return b;
		}
	}
}
