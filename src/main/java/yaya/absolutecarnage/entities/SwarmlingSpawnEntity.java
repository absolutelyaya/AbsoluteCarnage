package yaya.absolutecarnage.entities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
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
	
	public SwarmlingSpawnEntity(EntityType<? extends HostileEntity> entityType, World world)
	{
		super(entityType, world);
		this.moveControl = new FlightMoveControl(this, 20, true);
		this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
		this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
		this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 16.0F);
		this.setPathfindingPenalty(PathNodeType.DANGER_CACTUS, -1.0F);
	}
	
	@Override
	protected void initGoals()
	{
		goalSelector.add(0, new AttackGoal(this));
		goalSelector.add(1, new LookAtEntityGoal(this, LivingEntity.class, 5));
		goalSelector.add(2, new LookAroundGoal(this));
		goalSelector.add(3, new FlyGoal(this, 1.0));
		
		targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
	}
	
	public static DefaultAttributeContainer.Builder setAttributes()
	{
		return TameableEntity.createMobAttributes()
					   .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
					   .add(EntityAttributes.GENERIC_ARMOR, 4.0D)
					   .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.25D)
					   .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0D)
					   .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D);
	}
	
	protected EntityNavigation createNavigation(World world) {
		BirdNavigation nav = new BirdNavigation(this, world);
		nav.setCanPathThroughDoors(false);
		nav.setCanSwim(false);
		nav.setCanEnterOpenDoors(true);
		return nav;
	}
	
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event)
	{
		if(isOnGround())
			event.getController().setAnimation(IDLEGROUND_ANIM);
		else
			event.getController().setAnimation(IDLEAIR_ANIM);
		return PlayState.CONTINUE;
	}
	
	@Override
	protected boolean isDisallowedInPeaceful()
	{
		return true;
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
}
