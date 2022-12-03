package yaya.absolutecarnage.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
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

public class SwarmlingWeaverEntity extends AbstractSwarmling implements SwarmEntity, IAnimatable
{
	private final AnimationFactory factory = new AnimationFactory(this);
	private static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("pose", true);
	private static final AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("walk", true);
	private static final AnimationBuilder FLEE_CEILING_ANIM = new AnimationBuilder().addAnimation("flee_ceiling", false);
	protected static final TrackedData<Byte> ANIMATION = DataTracker.registerData(SwarmlingWeaverEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final byte ANIMATION_IDLE = 0;
	private static final byte ANIMATION_WALK = 1;
	private static final byte ANIMATION_FLEE_CEILING = 2;
	
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
		goalSelector.add(0, new FleeEntityGoal<>(this, PlayerEntity.class, 30f, 1.0f, 1.5f, (a) -> true));
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
	}
	
	@Override
	protected EntityNavigation createNavigation(World world)
	{
		return new SpiderNavigation(this, world);
	}
	
	///TODO: Climb to Ceiling behavior
	///TODO: Shoot webs at players
	///TODO: Spin reinforced webs inbetween sticky nest blocks
	
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
		
		event.getController().setAnimationSpeed(getVelocity().horizontalLengthSquared() > 0.0025 ? 2f : 1f);
		switch (anim)
		{
			case ANIMATION_IDLE -> event.getController().setAnimation(event.isMoving() ? WALK_ANIM : IDLE_ANIM);
			case ANIMATION_FLEE_CEILING -> event.getController().setAnimation(FLEE_CEILING_ANIM);
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
	
	public static DefaultAttributeContainer.Builder setAttributes()
	{
		return HostileEntity.createMobAttributes()
					   .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0D)
					   .add(EntityAttributes.GENERIC_ARMOR, 2.0D)
					   .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
					   .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.5D);
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
