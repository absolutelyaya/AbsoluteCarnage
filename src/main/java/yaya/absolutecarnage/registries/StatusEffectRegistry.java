package yaya.absolutecarnage.registries;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.effects.AcidStatusEffect;

public class StatusEffectRegistry
{
	public static final StatusEffect ACID = register("acid", new AcidStatusEffect());
	
	public static StatusEffect register(String name, StatusEffect effect)
	{
		Registry.register(Registry.STATUS_EFFECT, new Identifier(AbsoluteCarnage.MOD_ID, name), effect);
		return effect;
	}
	
	public static void registerEffects()
	{
	
	}
}
