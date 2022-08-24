package yaya.absolutecarnage.entities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.NoPenaltySolidTargeting;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
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
import yaya.absolutecarnage.registries.BlockTagRegistry;

public class SwarmlingSpawnEntity extends HostileEntity implements IAnimatable
{
	final AnimationFactory factory = new AnimationFactory(this);
	private final static AnimationBuilder IDLEAIR_ANIM = new AnimationBuilder().addAnimation("idle_air", true);
	private final static AnimationBuilder IDLEGROUND_ANIM = new AnimationBuilder().addAnimation("idle_ground", true);
	private final static AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("walk", true);
	private boolean groundNavigation, stopAirNavigation;
	
	public SwarmlingSpawnEntity(EntityType<? extends HostileEntity> entityType, World world)
	{
		super(entityType, world);
		switchNavigator(true);
		this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
		this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
		this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 16.0F);
		this.setPathfindingPenalty(PathNodeType.DANGER_CACTUS, -1.0F);
	}
	
	@Override
	protected void initGoals()
	{
		goalSelector.add(0, new AttackGoal(this));
		goalSelector.add(1, new DualWanderingGoal(this, 1.0));
		goalSelector.add(2, new LookAtEntityGoal(this, LivingEntity.class, 5));
		goalSelector.add(3, new LookAroundGoal(this));
		
		targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
	}
	
	public static DefaultAttributeContainer.Builder setAttributes()
	{
		return HostileEntity.createMobAttributes()
					   .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
					   .add(EntityAttributes.GENERIC_ARMOR, 4.0D)
					   .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.75D)
					   .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D)
					   .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0D);
	}
	
	protected EntityNavigation createNavigation(World world) {
		BirdNavigation nav = new BirdNavigation(this, world);
		nav.setCanPathThroughDoors(false);
		nav.setCanSwim(false);
		nav.setCanEnterOpenDoors(true);
		return nav;
	}
	
	public void switchNavigator(boolean onLand)
	{
		if (onLand)
		{
			this.moveControl = new MoveControl(this);
			this.navigation = new MobNavigation(this, world);
		}
		else
		{
			this.moveControl = new FlightMoveControl(this, 20, true);
			this.navigation = new BirdNavigation(this, world);
		}
		groundNavigation = onLand;
	}
	
	public void switchNavigator()
	{
		if(groundNavigation)
			switchNavigator(false);
		else
		{
			this.moveControl = new FlightMoveControl(this, 20, false);
			stopAirNavigation = true;
		}
	}
	
	@Override
	public void tickMovement()
	{
		super.tickMovement();
		Vec3d vec3d = this.getVelocity();
		if (!this.onGround && vec3d.y < 0.0)
			this.setVelocity(vec3d.multiply(1.0, 0.8, 1.0));
	}
	
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event)
	{
		if(isOnGround())
		{
			if(event.isMoving())
				event.getController().setAnimation(WALK_ANIM);
			else
				event.getController().setAnimation(IDLEGROUND_ANIM);
		}
		else
			event.getController().setAnimation(IDLEAIR_ANIM);
		return PlayState.CONTINUE;
	}
	
	@Override
	protected boolean isDisallowedInPeaceful()
	{
		return true;
	}
	
	public boolean isGroundNavigating()
	{
		return groundNavigation;
	}
	
	@Override
	public void registerControllers(AnimationData animationData)
	{
		AnimationController<SwarmlingSpawnEntity> controller = new AnimationController<>(this, "controller",
				2, this::predicate);
		animationData.addAnimationController(controller);
	}
	
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}
	
	protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition)
	{
		setOnGround(onGround);
		if(stopAirNavigation)
		{
			switchNavigator(true);
			stopAirNavigation = false;
		}
	}
	
	@Override
	public AnimationFactory getFactory()
	{
		return factory;
	}
	
	@SuppressWarnings("unused")
	public static boolean canSpawn(EntityType<? extends LivingEntity> type, ServerWorldAccess world, SpawnReason reason, BlockPos pos, Random random)
	{
		return world.getBlockState(pos.down()).isIn(BlockTagRegistry.SWARMLING_SPAWNABLE);
	}
	
	public class DualWanderingGoal extends WanderAroundGoal
	{
		final SwarmlingSpawnEntity mob;
		
		public DualWanderingGoal(SwarmlingSpawnEntity mob, double speed)
		{
			super(mob, speed);
			this.mob = mob;
		}
		
		@Override
		public boolean canStart()
		{
			return super.canStart();
		}
		
		@Nullable
		@Override
		protected Vec3d getWanderTarget()
		{
			if(mob.isGroundNavigating())
				return super.getWanderTarget();
			else
			{
				Vec3d vec3d = this.mob.getRotationVec(0.0F);
				Vec3d vec3d2 = AboveGroundTargeting.find(this.mob, 8, 7, vec3d.x, vec3d.z, 1.57F, 6, 4);
				return vec3d2 != null ? vec3d2 : NoPenaltySolidTargeting.find(this.mob, 8, 4, -2, vec3d.x, vec3d.z, 1.57);
			}
		}
		
		@Override
		public void stop()
		{
			super.stop();
			if(random.nextInt(mob.isGroundNavigating() ? 4 : 8) == 0)
				mob.switchNavigator();
		}
	}
}
