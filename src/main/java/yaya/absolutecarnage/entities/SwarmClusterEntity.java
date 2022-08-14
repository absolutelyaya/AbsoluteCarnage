package yaya.absolutecarnage.entities;

import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.registries.BlockTagRegistry;

public class SwarmClusterEntity extends MobEntity implements IAnimatable
{
	private final AnimationFactory factory = new AnimationFactory(this);
	private final static AnimationBuilder WRIGGLE_ANIM = new AnimationBuilder().addAnimation("animation.swarm_cluster.wriggle", true);
	boolean hatching;
	int swarmlings, hatchTicks;
	
	public SwarmClusterEntity(EntityType<? extends MobEntity> entityType, World world)
	{
		super(entityType, world);
		swarmlings = random.nextInt(3);
	}
	
	public static DefaultAttributeContainer.Builder setAttributes()
	{
		return TameableEntity.createMobAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0D)
				.add(EntityAttributes.GENERIC_ARMOR, 5.0D);
	}
	
	@Override
	protected boolean isDisallowedInPeaceful()
	{
		return true;
	}
	
	@Override
	public boolean damage(DamageSource source, float amount)
	{
		if(source.equals(DamageSource.ON_FIRE))
			amount *= 3f;
		Entity attacker = source.getAttacker();
		if(attacker != null && attacker.isPlayer())
			hatching = true;
		if(source.isFromFalling())
			return super.damage(source, 0);
		return super.damage(source, amount);
	}
	
	void particles(ParticleEffect effect)
	{
		particles(effect, 0f, 1f);
	}
	
	void particles(ParticleEffect effect, float spd, float area)
	{
		Vec3d pos = getPos().add(random.nextDouble() - 0.5 * area, random.nextDouble() * 1.5 * area,
				random.nextDouble() - 0.5 * area);
		Vec3d v = new Vec3d(random.nextDouble() * spd - spd / 2, random.nextDouble() * spd - spd / 2,
				random.nextDouble() * spd - spd / 2);
		world.addParticle(effect, pos.x, pos.y, pos.z, v.x, v.y, v.z);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if(world.getBlockState(getBlockPos().add(0, -1, 0)).isAir())
		{
			if(age > 10)
				hatch();
			else
				setVelocity(0, -10, 0);
		}
		if(hatching && !isOnFire())
		{
			for (int i = 0; i < 4; i++)
				particles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.SOUL_SAND.getDefaultState()));
			hatchTicks++;
		}
		if(isOnFire())
		{
			for (int i = 0; i < 4; i++)
			{
				particles(ParticleTypes.FLAME, 0.05f, 0.75f);
				particles(ParticleTypes.LARGE_SMOKE, 0.02f, 0.5f);
			}
		}
		if(hatchTicks > 120 / Math.max(world.getDifficulty().getId(), 1))
			hatch();
	}
	
	@Override
	protected Identifier getLootTableId()
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "entities/swarm_cluster");
	}
	
	@Override
	public boolean doesRenderOnFire()
	{
		return false;
	}
	
	private void hatch()
	{
		for (int i = 0; i < 32; i++)
			particles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.SOUL_SAND.getDefaultState()));
		//spawn swarmling spawn
		remove(RemovalReason.KILLED);
	}
	
	@Override
	public void takeKnockback(double strength, double x, double z) { }
	
	@Override
	public void pushAwayFrom(Entity entity) { }
	
	@Override
	public boolean isPushable()
	{
		return false;
	}
	
	@Override
	public void onDeath(DamageSource damageSource)
	{
		if(damageSource.isFire())
		{
			setInvisible(true);
		}
		super.onDeath(damageSource);
	}
	
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event)
	{
		event.getController().setAnimation(WRIGGLE_ANIM);
		return PlayState.CONTINUE;
	}
	
	@Override
	public void registerControllers(AnimationData animationData)
	{
		AnimationController<SwarmClusterEntity> controller = new AnimationController<>(this, "controller",
				2, this::predicate);
		animationData.addAnimationController(controller);
	}
	
	@Override
	public AnimationFactory getFactory()
	{
		return factory;
	}
	
	@SuppressWarnings("unused")
	public static boolean canSpawn(EntityType<? extends LivingEntity> type, ServerWorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
		return world.getBlockState(pos.down()).isIn(BlockTagRegistry.SWARMLING_SPAWNABLE) && !world.isSkyVisible(pos) && pos.getY() < 64;
	}
}
