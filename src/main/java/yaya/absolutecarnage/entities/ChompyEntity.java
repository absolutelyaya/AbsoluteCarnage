package yaya.absolutecarnage.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import yaya.absolutecarnage.AbsoluteCarnage;

import java.util.EnumSet;
import java.util.function.Predicate;

public class ChompyEntity extends TameableEntity implements IAnimatable
{
	private final AnimationFactory factory = new AnimationFactory(this);
	public final Predicate<LivingEntity> LIFE_FOOD = this::isLifeFood;
	protected static final TrackedData<Byte> ANIMATION = DataTracker.registerData(ChompyEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected final byte ANIMATION_BITE = 1;
	private static final AnimationBuilder IDLE_ANIM  = new AnimationBuilder().addAnimation("animation.chompy.idle", true);
	private static final AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("animation.chompy.walk", true);
	private static final AnimationBuilder BITE_ANIM = new AnimationBuilder().addAnimation("animation.chompy.bite", false);
	
	public ChompyEntity(EntityType<? extends TameableEntity> entityType, World world)
	{
		super(entityType, world);
		this.ignoreCameraFrustum = true;
		this.setTamed(false);
		this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, -1.0F);
		this.setPathfindingPenalty(PathNodeType.DANGER_POWDER_SNOW, -1.0F);
	}
	
	protected void initGoals()
	{
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new BiteGoal(this));
		this.goalSelector.add(2, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
		this.goalSelector.add(3, new WanderAroundPointOfInterestGoal(this, 0.75f, false));
		this.goalSelector.add(4, new WanderAroundFarGoal(this, 0.75f, 1));
		this.goalSelector.add(5, new LookAroundGoal(this));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
		
		this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
		this.targetSelector.add(2, new AttackWithOwnerGoal(this));
		this.targetSelector.add(3, (new RevengeGoal(this)).setGroupRevenge());
		this.targetSelector.add(4, new UntamedActiveTargetGoal<>(this, AnimalEntity.class, false, LIFE_FOOD));
	}
	
	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		this.dataTracker.startTracking(ANIMATION, (byte)0);
	}
	
	@Override
	protected Identifier getLootTableId()
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "entities/chompy");
	}
	
	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity)
	{
		return null;
	}
	
	public static DefaultAttributeContainer.Builder setAttributes()
	{
		return TameableEntity.createMobAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
				.add(EntityAttributes.GENERIC_ATTACK_SPEED, 3.0f)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f);
	}
	
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event)
	{
		if(dataTracker.get(ANIMATION) == ANIMATION_BITE)
		{
			event.getController().setAnimation(BITE_ANIM);
			if(event.getController().getAnimationState() == AnimationState.Stopped)
				dataTracker.set(ANIMATION, (byte)0);
			else
				return PlayState.CONTINUE;
		}
		if(event.isMoving())
		{
			event.getController().setAnimation(WALK_ANIM);
			return PlayState.CONTINUE;
		}
		
		event.getController().setAnimation(IDLE_ANIM);
		return PlayState.CONTINUE;
	}
	
	@Override
	public void registerControllers(AnimationData animationData)
	{
		AnimationController<ChompyEntity> controller = new AnimationController<>(this, "controller",
				2, this::predicate);
		animationData.addAnimationController(controller);
	}
	
	@Override
	public AnimationFactory getFactory()
	{
		return factory;
	}
	
	@Nullable
	@Override
	protected SoundEvent getAmbientSound()
	{
		return super.getAmbientSound();
	}
	
	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source)
	{
		return super.getHurtSound(source);
	}
	
	@Nullable
	@Override
	protected SoundEvent getDeathSound()
	{
		return super.getDeathSound();
	}
	
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		Item item = itemStack.getItem();
		if (this.world.isClient)
		{
			boolean bl = this.isOwner(player) || this.isTamed() || itemStack.isOf(Items.BEEF) && !this.isTamed();
			return bl ? ActionResult.CONSUME : ActionResult.PASS;
		}
		else
		{
			if (this.isTamed())
			{
				if (this.isBreedingItem(itemStack) && this.getHealth() < this.getMaxHealth())
				{
					if (!player.getAbilities().creativeMode)
					{
						itemStack.decrement(1);
					}
					
					if(item.getFoodComponent()!= null)
						this.heal((float)item.getFoodComponent().getHunger());
					return ActionResult.SUCCESS;
				}
			}
			else if (itemStack.isOf(Items.BEEF))
			{
				if (!player.getAbilities().creativeMode)
				{
					itemStack.decrement(1);
				}
				
				if (this.random.nextInt(3) == 0)
				{
					this.setOwner(player);
					this.navigation.stop();
					this.setTarget(null);
					this.world.sendEntityStatus(this, (byte)7);
				}
				else
				{
					this.world.sendEntityStatus(this, (byte)6);
				}
				
				return ActionResult.SUCCESS;
			}
			
			return super.interactMob(player, hand);
		}
	}
	
	public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
		if (!(target instanceof CreeperEntity) && !(target instanceof GhastEntity)) {
			if (target instanceof ChompyEntity wolfEntity) {
				return !wolfEntity.isTamed() || wolfEntity.getOwner() != owner;
			} else if (target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity)owner).shouldDamagePlayer((PlayerEntity)target)) {
				return false;
			} else if (target instanceof AbstractHorseEntity && ((AbstractHorseEntity)target).isTame()) {
				return false;
			} else {
				return !(target instanceof TameableEntity) || !((TameableEntity)target).isTamed();
			}
		} else {
			return false;
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
	}
	
	public void setAnimation(byte anim)
	{
		dataTracker.set(ANIMATION, anim);
	}
	
	@Override
	public boolean isBreedingItem(ItemStack stack)
	{
		Item item = stack.getItem();
		return item.isFood() && item.getFoodComponent() != null && item.getFoodComponent().isMeat();
	}
	
	boolean isLifeFood(LivingEntity entity)
	{
		EntityType<?> entityType = entity.getType();
		return entityType == EntityType.RABBIT || entityType == EntityType.SHEEP || entityType == EntityType.PIG;
	}
	
	static class BiteGoal extends Goal
	{
		private final ChompyEntity mob;
		private LivingEntity target;
		private int cooldown;
		private int animDuration;
		
		public BiteGoal(ChompyEntity mob)
		{
			this.mob = mob;
			this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
		}
		
		public boolean canStart()
		{
			LivingEntity livingEntity = this.mob.getTarget();
			if (livingEntity == null)
			{
				return false;
			}
			else
			{
				this.target = livingEntity;
				return true;
			}
		}
		
		public boolean shouldContinue()
		{
			if (!this.target.isAlive())
			{
				return false;
			}
			else if (this.mob.squaredDistanceTo(this.target) > 225.0D)
			{
				return false;
			}
			else
			{
				return !this.mob.getNavigation().isIdle() || this.canStart();
			}
		}
		
		public void stop()
		{
			this.target = null;
			this.mob.getNavigation().stop();
		}
		
		@Override
		public void tick()
		{
			this.mob.getLookControl().lookAt(this.target, 30.0F, 30.0F);
			double d = (this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F);
			double e = this.mob.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ());
			double f = 0.8D;
			if (e > d && e < 16.0D)
			{
				f = 0.6D;
			} else if (e < 225.0D)
			{
				f = 1.0D;
			}
			
			this.mob.getNavigation().startMovingTo(this.target, f);
			this.cooldown = Math.max(this.cooldown - 1, 0);
			this.animDuration = Math.max(this.animDuration - 1, 0);
			if (!(e > d))
			{
				if (this.cooldown <= 0)
				{
					this.cooldown = 20;
					mob.setAnimation(mob.ANIMATION_BITE);
					animDuration = 16;
				}
			}
			if(animDuration == 8)
			{
				mob.tryAttack(target);
			}
			if(animDuration == 0)
				mob.setAnimation((byte)0);
		}
	}
}
