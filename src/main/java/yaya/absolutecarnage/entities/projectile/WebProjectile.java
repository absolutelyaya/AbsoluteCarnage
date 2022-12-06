package yaya.absolutecarnage.entities.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import yaya.absolutecarnage.registries.EntityRegistry;
import yaya.absolutecarnage.registries.StatusEffectRegistry;

public class WebProjectile extends AbstractStatusEffectProjectile
{
	public WebProjectile(EntityType<? extends ThrownItemEntity> entityType, World world)
	{
		super(entityType, world);
	}
	
	protected WebProjectile(LivingEntity owner, World world)
	{
		super(EntityRegistry.WEB_PROJECTILE, owner, world);
	}
	
	public static WebProjectile spawn(LivingEntity owner, World world)
	{
		return new WebProjectile(owner, world);
	}
	
	@Override
	protected Item getDefaultItem()
	{
		return Items.COBWEB;
	}
	
	@Override
	StatusEffectInstance getEffect()
	{
		return new StatusEffectInstance(StatusEffectRegistry.WEBBED, 600, 0, true, false, true);
	}
	
	@Override
	float getDamage()
	{
		return 0.25f;
	}
}
