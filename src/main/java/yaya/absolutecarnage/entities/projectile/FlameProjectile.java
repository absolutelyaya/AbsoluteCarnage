package yaya.absolutecarnage.entities.projectile;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import yaya.absolutecarnage.registries.EntityRegistry;

import java.util.List;

public class FlameProjectile extends ThrownItemEntity
{
	public FlameProjectile(EntityType<? extends ThrownItemEntity> entityType, World world)
	{
		super(entityType, world);
	}
	
	protected FlameProjectile(LivingEntity owner, World world)
	{
		super(EntityRegistry.FLAME_PROJECTILE, owner, world);
	}
	
	public static FlameProjectile spawn(LivingEntity owner, World world)
	{
		return new FlameProjectile(owner, world);
	}
	
	@Override
	protected Item getDefaultItem()
	{
		return Items.FIRE_CHARGE;
	}
	
	@Override
	public boolean hasNoGravity()
	{
		return true;
	}
	
	@Override
	protected void onEntityHit(EntityHitResult entityHitResult)
	{
	
	}
	
	void damageEntity(Entity target)
	{
		if(!isOwner(target))
		{
			target.damage(DamageSource.mobProjectile(this, (LivingEntity)getOwner()), 4f);
			if(target instanceof LivingEntity entity)
				entity.setFireTicks(200);
		}
	}
	
	@Override
	protected void onBlockHit(BlockHitResult blockHitResult)
	{
		super.onBlockHit(blockHitResult);
		Vec3i side = blockHitResult.getSide().getVector();
		Vec3d v = getVelocity();
		if(side.getX() != 0)
			v = v.multiply(0, 1, 1);
		else if(side.getY() != 0)
			v = v.multiply(1, 0, 1);
		else if(side.getZ() != 0)
			v = v.multiply(1, 1, 0);
		setVelocity(v);
	}
	
	@Override
	public boolean updateMovementInFluid(TagKey<Fluid> tag, double speed)
	{
		boolean b = super.updateMovementInFluid(tag, speed);
		if(b && tag.equals(FluidTags.WATER))
		{
			world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, getX(), getY(), getZ(), 0, 0.1, 0);
			kill();
		}
		return b;
	}
	
	double randomParticleOffset(double offset)
	{
		return random.nextDouble() * offset - offset / 2;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		setBoundingBox(getBoundingBox().expand(age / 30f));
		Box box = getBoundingBox();
		Vec3d pos = getPos().add(randomParticleOffset(box.getXLength()), randomParticleOffset(box.getYLength()),
				randomParticleOffset(box.getZLength()));
		Vec3d v = getVelocity().multiply(0.1).add(random.nextDouble() * 0.05 - 0.025, random.nextDouble() * 0.05 - 0.025,
				random.nextDouble() * 0.05 - 0.025);
		world.addParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, v.x, v.y, v.z);
		List<LivingEntity> collide = this.world.getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox());
		for(LivingEntity e : collide)
			damageEntity(e);
		
		setVelocity(getVelocity().multiply(0.9f));
		if(getVelocity().length() < 0.05f)
			kill();
		
		if(!world.isClient && world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK) &&
				   world.getBlockState(getBlockPos()).isAir() && random.nextInt(5) == 0)
			world.setBlockState(getBlockPos(), Block.postProcessState(Blocks.FIRE.getDefaultState(), world, getBlockPos()));
	}
	
	@Override
	public EntityDimensions getDimensions(EntityPose pose)
	{
		return super.getDimensions(pose);
	}
	
	@Override
	public boolean collides()
	{
		return super.collides();
	}
}
