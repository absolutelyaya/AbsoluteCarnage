package yaya.absolutecarnage.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import yaya.absolutecarnage.entities.goals.DualWanderingGoal;

public class SwarmlingWarriorEntity extends AbstractSwarmling implements SwarmEntity, IAnimatable
{
	final AnimationFactory factory = new AnimationFactory(this);
	private final static AnimationBuilder IDLEAIR_ANIM = new AnimationBuilder().addAnimation("idle_air", true);
	private final static AnimationBuilder IDLEGROUND_ANIM = new AnimationBuilder().addAnimation("idle_ground", true);
	private final static AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("walk", true);
	private final static AnimationBuilder DROWN_ANIM = new AnimationBuilder().addAnimation("drown", true);
	
	public SwarmlingWarriorEntity(EntityType<? extends HostileEntity> entityType, World world)
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
					   .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D)
					   .add(EntityAttributes.GENERIC_ARMOR, 4.0D)
					   .add(EntityAttributes.GENERIC_FLYING_SPEED, 1.0D)
					   .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2D)
					   .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.5D);
	}
	
	@Override
	protected boolean isDisallowedInPeaceful()
	{
		return true;
	}
	
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event)
	{
		if(((CarnageEntityAccessor)this).isDrowning())
		{
			event.getController().setAnimation(DROWN_ANIM);
			return PlayState.CONTINUE;
		}
		
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
	public void registerControllers(AnimationData animationData)
	{
		AnimationController<SwarmlingWarriorEntity> controller = new AnimationController<>(this, "controller",
				2, this::predicate);
		animationData.addAnimationController(controller);
	}
	
	@Override
	public AnimationFactory getFactory()
	{
		return factory;
	}
}
