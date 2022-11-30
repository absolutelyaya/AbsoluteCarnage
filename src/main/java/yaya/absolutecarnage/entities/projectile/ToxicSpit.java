package yaya.absolutecarnage.entities.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yaya.absolutecarnage.registries.EntityRegistry;
import yaya.absolutecarnage.registries.StatusEffectRegistry;

public class ToxicSpit extends AbstractStatusEffectProjectile
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
	public void tick()
	{
		super.tick();
		Vec3d pos = getPos();
		world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.SLIME_BALL)),
				pos.x, pos.y, pos.z, 0f, 0f, 0f);
	}
	
	@Override
	protected Item getDefaultItem()
	{
		return Items.SLIME_BALL;
	}
	
	@Override
	StatusEffectInstance getEffect()
	{
		return new StatusEffectInstance(StatusEffectRegistry.ACID, 200, 1);
	}
	
	@Override
	float getDamage()
	{
		return 4f;
	}
}
