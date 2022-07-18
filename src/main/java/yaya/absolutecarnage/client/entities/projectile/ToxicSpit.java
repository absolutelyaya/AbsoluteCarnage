package yaya.absolutecarnage.client.entities.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yaya.absolutecarnage.registries.EntityRegistry;

public class ToxicSpit extends ThrownItemEntity
{
	public ToxicSpit(EntityType<? extends ThrownItemEntity> entityType, World world)
	{
		super(entityType, world);
	}
	
	protected ToxicSpit(LivingEntity owner, World world)
	{
		super(EntityRegistry.TOXIC_SPIT, owner, world);
	}
	
	public static ToxicSpit spawn(LivingEntity owner, World world)
	{
		return new ToxicSpit(owner, world);
	}
	
	@Override
	protected Item getDefaultItem()
	{
		return Items.SLIME_BALL;
	}
	
	@Override
	protected void onEntityHit(EntityHitResult entityHitResult)
	{
		Entity target = entityHitResult.getEntity();
		target.damage(DamageSource.mobProjectile(this, (LivingEntity)getOwner()), 4f);
		if(target instanceof LivingEntity entity)
		{
			entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 1));
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
	
	@Override
	public void tick()
	{
		super.tick();
		Vec3d pos = getPos();
		world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.SLIME_BALL)),
				pos.x, pos.y, pos.z, 0f, 0f, 0f);
	}
}
