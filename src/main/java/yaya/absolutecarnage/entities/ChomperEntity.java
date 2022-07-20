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
import net.minecraft.sound.SoundEvents;
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
import yaya.absolutecarnage.client.entities.projectile.ToxicSpit;

import java.util.EnumSet;
import java.util.List;

public class ChomperEntity extends HostileEntity implements IAnimatable
{
	private final AnimationFactory factory = new AnimationFactory(this);
	private static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("animation.chomper.idle", true);
	private static final AnimationBuilder BURROW_ANIM = new AnimationBuilder().addAnimation("animation.chomper.burrow", false);
	private static final AnimationBuilder BURROWED_ANIM = new AnimationBuilder().addAnimation("animation.chomper.burrowed", true);
	private static final AnimationBuilder EMERGE_ANIM = new AnimationBuilder().addAnimation("animation.chomper.emerge", false);
	private static final AnimationBuilder SPIT_ANIM = new AnimationBuilder().addAnimation("animation.chomper.spit", false);
	private static final AnimationBuilder BITE_ANIM = new AnimationBuilder().addAnimation("animation.chomper.bite", false);
	protected static final TrackedData<Byte> ANIMATION = DataTracker.registerData(ChomperEntity.class, TrackedDataHandlerRegistry.BYTE);
	public static final TrackedData<Boolean> RARE = DataTracker.registerData(ChomperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final byte ANIMATION_IDLE = 0;
	private static final byte ANIMATION_BURROW = 1;
	private static final byte ANIMATION_BURROWED = 2;
	private static final byte ANIMATION_EMERGE = 3;
	private static final byte ANIMATION_SPIT = 4;
	private static final byte ANIMATION_BITE = 5;
	
	public ChomperEntity(EntityType<? extends HostileEntity> entityType, World world)
	{
		super(entityType, world);
		this.ignoreCameraFrustum = true;
		this.stepHeight = 1.0F;
	}
	
	@Override
	protected void initGoals()
	{
		goalSelector.add(1, new SpitGoal(this));
		goalSelector.add(2, new BiteGoal(this));
		goalSelector.add(3, new JumpscareGoal(this));
		goalSelector.add(5, new LookAtEntityGoal(this, HostileEntity.class, 30f));
		goalSelector.add(6, new LookAroundGoal(this));
		targetSelector.add(0, new RevengeGoal(this, ChomperEntity.class));
		targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
		targetSelector.add(2, new ActiveTargetGoal<>(this, AnimalEntity.class, true));
	}
	
	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
		this.dataTracker.startTracking(RARE, random.nextInt(2) == 0);
	}
	
	@Override
	public boolean saveNbt(NbtCompound nbt)
	{
		nbt.put("rare_variant", NbtByte.of(dataTracker.get(RARE)));
		return super.saveNbt(nbt);
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		dataTracker.set(RARE, nbt.getBoolean("rare_variant"));
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
			case ANIMATION_SPIT -> event.getController().setAnimation(SPIT_ANIM);
			case ANIMATION_BITE -> event.getController().setAnimation(BITE_ANIM);
		}
		return PlayState.CONTINUE;
	}
	
	public void PlayAnimation(byte anim)
	{
		getDataTracker().set(ANIMATION, anim);
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
	
	public void spit(LivingEntity target)
	{
		ToxicSpit projectile = ToxicSpit.spawn(this, world);
		double d = target.getEyeY() - 2;
		double e = target.getX() - this.getX();
		double f = d - projectile.getY();
		double g = target.getZ() - this.getZ();
		double h = Math.sqrt(e * e + g * g) * 0.20000000298023224D;
		projectile.setVelocity(e, f + h, g, 1.6F, 6.0F);
		this.world.spawnEntity(projectile);
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
		return super.collides() && (anim == ANIMATION_IDLE || anim == ANIMATION_BITE || anim == ANIMATION_SPIT);
	}
	
	@Override
	protected void pushAway(Entity entity)
	{
		byte anim = dataTracker.get(ANIMATION);
		if(!(anim == ANIMATION_BURROWED || anim == ANIMATION_EMERGE))
			super.pushAway(entity);
	}
	
	@Override
	public void takeKnockback(double strength, double x, double z) { }
	
	private static class JumpscareGoal extends Goal
	{
		private final ChomperEntity mob;
		private LivingEntity target;
		private boolean isBurrowed, isEmerging, shouldContinue = true;
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
			return target != null && target.isAlive() && mob.distanceTo(target) > 10f;
		}
		
		@Override
		public boolean shouldContinue()
		{
			return !(isEmerging && animDuration == 0);
		}
		
		@Override
		public boolean canStop()
		{
			return !shouldContinue;
		}
		
		@Override
		public void stop()
		{
			mob.getNavigation().stop();
			animDuration = 0;
			isBurrowed = false;
			isEmerging = false;
			target = null;
			
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
	
	private static class BiteGoal extends Goal
	{
		private final ChomperEntity mob;
		private LivingEntity target;
		int animTime;
		
		public BiteGoal(ChomperEntity mob)
		{
			this.mob = mob;
			this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
		}
		
		@Override
		public boolean canStart()
		{
			target = mob.getTarget();
			return target != null && mob.distanceTo(target) < 5f && mob.canSee(target) && target.isAlive();
		}
		
		@Override
		public boolean shouldContinue()
		{
			return animTime != 0 && mob.distanceTo(target) < 5f;
		}
		
		@Override
		public boolean canStop()
		{
			return !shouldContinue();
		}
		
		@Override
		public void stop()
		{
			target = null;
			mob.getDataTracker().set(ANIMATION, ANIMATION_IDLE);
		}
		
		@Override
		public void tick()
		{
			animTime = Math.max(animTime - 1, 0);
			if(target == null)
				return;
			this.mob.getLookControl().lookAt(target);
			
			switch (animTime)
			{
				case 0 -> {
					mob.PlayAnimation(ANIMATION_BITE);
					animTime = 25;
				}
				case 8 -> mob.tryAttack(target);
				case 1 -> mob.PlayAnimation(ANIMATION_IDLE);
			}
		}
	}
	
	private static class SpitGoal extends Goal
	{
		private final ChomperEntity mob;
		private LivingEntity target;
		int cooldown;
		
		public SpitGoal(ChomperEntity mob)
		{
			this.mob = mob;
			this.setControls(EnumSet.of(Control.LOOK));
		}
		
		@Override
		public boolean canStart()
		{
			target = mob.getTarget();
			return target != null && mob.distanceTo(target) < 10f && mob.distanceTo(target) > 5f && mob.canSee(target);
		}
		
		@Override
		public boolean shouldContinue()
		{
			return target != null && mob.distanceTo(target) < 10f && mob.distanceTo(target) > 5f && target.isAlive() && mob.canSee(target);
		}
		
		@Override
		public boolean canStop()
		{
			return !shouldContinue();
		}
		
		@Override
		public void stop()
		{
			mob.getDataTracker().set(ANIMATION, ANIMATION_IDLE);
			target = null;
		}
		
		@Override
		public void tick()
		{
			cooldown = Math.max(cooldown - 1, 0);
			if(target == null)
				return;
			this.mob.getLookControl().lookAt(target);
			
			switch (cooldown)
			{
				case 0 -> {
					mob.PlayAnimation(ANIMATION_SPIT);
					cooldown = 10;
				}
				case 7 -> {
					for (int i = 0; i < 3; i++)
						mob.spit(target);
					mob.playSound(SoundEvents.ENTITY_LLAMA_SPIT, 1.0F, 0.6F / (mob.getRandom().nextFloat() * 0.4F + 0.8F));
				}
				case 2 -> mob.PlayAnimation(ANIMATION_IDLE);
			}
		}
	}
}
