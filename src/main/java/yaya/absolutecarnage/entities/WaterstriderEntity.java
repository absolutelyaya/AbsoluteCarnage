package yaya.absolutecarnage.entities;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
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
	
	public WaterstriderEntity(EntityType<? extends AnimalEntity> entityType, World world)
	{
		super(entityType, world);
	}
	
	@Override
	protected void initGoals()
	{
		goalSelector.add(0, new LookAtEntityGoal(this, PlayerEntity.class, 4f));
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
		return null;
	}
	
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event)
	{
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
		Vec3d pos = getPos();
		if(isSubmergedInWater())
			setPos(pos.x, pos.y + 0.55, pos.z);
		else if(isTouchingWater())
		{
			setVelocity(getVelocity().multiply(1, 0, 1));
			setPos(pos.x, Math.round(pos.y) - 1.0 / 16.0, pos.z);
		}
		
		if(thrustTimer <= 0)
			thrustTimer = 60 + random.nextInt(120);
		
		if(thrustTimer == 34)
			setActiveAnimation(ANIMATION_THRUST);
		
		if(dataTracker.get(ANIMATION) == ANIMATION_THRUST)
		{
			if(animTime >= 34)
				setActiveAnimation(ANIMATION_IDLE);
			
			if(animTime == 20)
			{
				double dir = random.nextInt(360);
				setRotation((float)dir - 90, 0);
				
				dir = Math.toRadians(dir);
				thrustVel = new Vec2f((float)Math.cos(dir), (float)Math.sin(dir)).multiply(0.05f);
			}
		}
		
		thrustVel = thrustVel.multiply(0.75f);
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
	}
}
