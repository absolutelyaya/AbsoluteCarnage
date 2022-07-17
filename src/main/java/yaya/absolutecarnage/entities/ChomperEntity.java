package yaya.absolutecarnage.entities;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib3.core.event.ParticleKeyFrameEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.EnumSet;
import java.util.List;

public class ChomperEntity extends HostileEntity implements IAnimatable
{
	private final AnimationFactory factory = new AnimationFactory(this);
	private static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("animation.chomper.idle", true);
	private static final AnimationBuilder BURROW_ANIM = new AnimationBuilder().addAnimation("animation.chomper.burrow", false);
	private static final AnimationBuilder BURROWED_ANIM = new AnimationBuilder().addAnimation("animation.chomper.burrowed", true);
	private static final AnimationBuilder EMERGE_ANIM = new AnimationBuilder().addAnimation("animation.chomper.emerge", false);
	protected static final TrackedData<Byte> ANIMATION = DataTracker.registerData(ChomperEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final byte ANIMATION_IDLE = 0;
	private static final byte ANIMATION_BURROW = 1;
	private static final byte ANIMATION_BURROWED = 2;
	private static final byte ANIMATION_EMERGE = 3;
	
	public ChomperEntity(EntityType<? extends HostileEntity> entityType, World world)
	{
		super(entityType, world);
		this.ignoreCameraFrustum = true;
		this.stepHeight = 1.0F;
	}
	
	@Override
	protected void initGoals()
	{
		goalSelector.add(0, new LookAtEntityGoal(this, LivingEntity.class, 12f));
		goalSelector.add(1, new JumpscareGoal(this));
		//Snap
		//Spit
		targetSelector.add(0, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
		targetSelector.add(1, new ActiveTargetGoal<>(this, AnimalEntity.class, false));
	}
	
	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
	}
	
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event)
	{
		byte anim = dataTracker.get(ANIMATION);
		
		switch (anim)
		{
			case ANIMATION_IDLE -> event.getController().setAnimation(IDLE_ANIM);
			case ANIMATION_BURROW -> event.getController().setAnimation(BURROW_ANIM);
			case ANIMATION_BURROWED -> event.getController().setAnimation(BURROWED_ANIM);
			case ANIMATION_EMERGE -> event.getController().setAnimation(EMERGE_ANIM);
		}
		return PlayState.CONTINUE;
	}
	
	@Override
	public void registerControllers(AnimationData animationData)
	{
		AnimationController<ChomperEntity> controller = new AnimationController<>(this, "controller",
				2, this::predicate);
		controller.registerParticleListener(this::particleListener);
		controller.registerCustomInstructionListener(this::customListener);
		animationData.addAnimationController(controller);
	}
	
	private <ENTITY extends IAnimatable> void particleListener(ParticleKeyFrameEvent<ENTITY> event)
	{
		if(event.effect.equals("burst"))
		{
			Vec3d pos = getPos();
			for (int i = 0; i < 64; i++)
			{
				world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()),
						pos.x - 0.75D + random.nextDouble() * 1.5D, pos.y + random.nextDouble() * 1.5D, pos.z - 0.75f + random.nextDouble() * 1.5D,
						0f, random.nextDouble() * 0.2f, 0f);
			}
		}
		if(event.effect.equals("snap"))
		{
			Vec3d pos = getPos().add(new Vec3d(0, 1, 0));
			for (int i = 0; i < 10; i++)
			{
				world.addParticle(ParticleTypes.CRIT,
						pos.x - 0.5D + random.nextDouble(), pos.y + random.nextDouble() * 1.5D, pos.z - 0.5f + random.nextDouble(),
						0f, random.nextDouble() * 0.1f, 0f);
			}
		}
	}
	
	private <ENTITY extends IAnimatable> void customListener(CustomInstructionKeyframeEvent<ENTITY> event)
	{
	
	}
	
	@Override
	public AnimationFactory getFactory()
	{
		return factory;
	}
	
	public static DefaultAttributeContainer.Builder setAttributes()
	{
		return HostileEntity.createMobAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 60.0D)
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.5f)
				.add(EntityAttributes.GENERIC_ATTACK_SPEED, 3.0f)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5f);
	}
	
	@Override
	public void tickMovement()
	{
		super.tickMovement();
		byte anim = dataTracker.get(ANIMATION);
		if(anim == ANIMATION_BURROW || anim == ANIMATION_BURROWED)
		{
			Vec3d pos = getPos();
			for (int i = 0; i < 5; i++)
			{
				world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()),
						pos.x - 0.5f + random.nextDouble(), pos.y + random.nextDouble() * 0.5f, pos.z - 0.5f + random.nextDouble(),
						0f, 0f, 0f);
			}
		}
	}
	
	public boolean isLookingAround()
	{
		byte anim = dataTracker.get(ANIMATION);
		return anim == ANIMATION_IDLE;
	}
	
	@Override
	public boolean isPushable()
	{
		return false;
	}
	
	@Override
	public boolean collides()
	{
		byte anim = dataTracker.get(ANIMATION);
		return super.collides() && (anim == ANIMATION_IDLE);
	}
	
	@Override
	protected void pushAway(Entity entity)
	{
		byte anim = dataTracker.get(ANIMATION);
		if(!(anim == ANIMATION_BURROWED || anim == ANIMATION_EMERGE))
			super.pushAway(entity);
	}
	
	private static class JumpscareGoal extends Goal
	{
		private final ChomperEntity mob;
		private LivingEntity target;
		private boolean isBurrowed, isEmerging, shouldContinue;
		private int animDuration;
		
		public JumpscareGoal(ChomperEntity mob)
		{
			this.mob = mob;
			this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
		}
		
		@Override
		public boolean canStart()
		{
			target = mob.getTarget();
			return target != null && target.isAlive() && !mob.isInAttackRange(target);
		}
		
		@Override
		public boolean shouldContinue()
		{
			return shouldContinue;
		}
		
		@Override
		public void stop()
		{
			target = null;
			mob.getNavigation().stop();
			animDuration = 0;
			isBurrowed = false;
			isEmerging = false;
			
			mob.getDataTracker().set(ANIMATION, ANIMATION_IDLE);
		}
		
		@Override
		public void tick()
		{
			if(!shouldContinue)
				shouldContinue = true;
			this.animDuration = Math.max(this.animDuration - 1, 0);
			if(!((target != null && target.isAlive()) || animDuration > 0) && !isEmerging)
				isEmerging = true;
			
			if((target != null && mob.distanceTo(target) < 1f) || isEmerging)
			{
				//Emerge
				if(isBurrowed)
				{
					mob.getDataTracker().set(ANIMATION, ANIMATION_EMERGE);
					animDuration = 23;
					isBurrowed = false;
					isEmerging = true;
				}
				if(animDuration == 12)
				{
					List<LivingEntity> list = mob.world.getNonSpectatingEntities(LivingEntity.class,
							mob.getBoundingBox().expand(0.5D, 0.0D, 0.5D));
					for (LivingEntity target : list)
					{
						if(target != mob)
						{
							mob.tryAttack(target);
							target.addVelocity(0D, 0.5D, 0D);
						}
					}
				}
				if(animDuration == 0)
					shouldContinue = false;
			}
			else
			{
				//Burrow and move to target
				if(isBurrowed)
				{
					Path path = mob.getNavigation().findPathTo(target, 0);
					mob.getNavigation().startMovingAlong(path, 0.7f);
					mob.getDataTracker().set(ANIMATION, ANIMATION_BURROWED);
				}
				else if(animDuration == 0)
				{
					mob.getDataTracker().set(ANIMATION, ANIMATION_BURROW);
					animDuration = 25;
				}
				if (animDuration == 1)
					isBurrowed = true;
			}
		}
	}
}
