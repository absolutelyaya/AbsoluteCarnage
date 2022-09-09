package yaya.absolutecarnage.registries;

import net.minecraft.entity.damage.DamageSource;

public class CarnageDamageSources extends DamageSource
{
	public static final DamageSource ACID = new CarnageDamageSources("acid").setBypassesArmor();
	
	protected CarnageDamageSources(String name)
	{
		super(name);
	}
}
