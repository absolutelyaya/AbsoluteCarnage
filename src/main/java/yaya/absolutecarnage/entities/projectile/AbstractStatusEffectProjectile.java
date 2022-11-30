package yaya.absolutecarnage.entities.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public abstract class AbstractStatusEffectProjectile extends ThrownItemEntity
{
	public AbstractStatusEffectProjectile(EntityType<? extends ThrownItemEntity> entityType, World world)
	{
		super(entityType, world);
	}
	
	public AbstractStatusEffectProjectile(EntityType<? extends ThrownItemEntity> type, LivingEntity owner, World world)
	{
		super(type, owner, world);
	}
	
	@Override
	protected Item getDefaultItem()
	{
		return Items.BARRIER;
	}
	
	abstract StatusEffectInstance getEffect();
	
	abstract float getDamage();
	
	@Override
	protected void onEntityHit(EntityHitResult entityHitResult)
	{
		Entity target = entityHitResult.getEntity();
		target.damage(DamageSource.mobProjectile(this, (LivingEntity)getOwner()), getDamage());
		if(target instanceof LivingEntity entity)
		{
			entity.addStatusEffect(getEffect());
		}
	}
	
	@Override
	protected void onBlockHit(BlockHitResult blockHitResult)
	{
		super.onBlockHit(blockHitResult);
	}
	
	@Override
	protected void onCollision(HitResult hitResult)
	{
		super.onCollision(hitResult);
		if (!this.world.isClient)
		{
			this.world.sendEntityStatus(this, (byte)3);
			this.kill();
		}
	}
}
