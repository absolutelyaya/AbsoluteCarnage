package yaya.absolutecarnage.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
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

public class SwarmlingEntity extends AbstractSwarmling implements SwarmEntity, IAnimatable
{
	final AnimationFactory factory = new AnimationFactory(this);
	
	public SwarmlingEntity(EntityType<? extends HostileEntity> entityType, World world)
	{
		super(entityType, world);
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
					   .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5D)
					   .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.5D);
	}
	
	@Override
	protected boolean isDisallowedInPeaceful()
	{
		return true;
	}
	
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event)
	{
		event.getController().setAnimation(new AnimationBuilder().addAnimation("pose"));
		return PlayState.CONTINUE;
	}
	
	@Override
	public void registerControllers(AnimationData animationData)
	{
		AnimationController<SwarmlingEntity> controller = new AnimationController<>(this, "controller",
				2, this::predicate);
		animationData.addAnimationController(controller);
	}
	
	@Override
	public AnimationFactory getFactory()
	{
		return factory;
	}
}
