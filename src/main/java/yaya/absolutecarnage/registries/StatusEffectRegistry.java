package yaya.absolutecarnage.registries;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.effects.AcidStatusEffect;
import yaya.absolutecarnage.effects.WebbedStatusEffect;

public class StatusEffectRegistry
{
	public static final StatusEffect ACID = register("acid", new AcidStatusEffect());
	public static final StatusEffect WEBBED = register("webbed", new WebbedStatusEffect()
		.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635",
				-1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
	
	public static StatusEffect register(String name, StatusEffect effect)
	{
		Registry.register(Registry.STATUS_EFFECT, new Identifier(AbsoluteCarnage.MOD_ID, name), effect);
		return effect;
	}
	
	public static void registerEffects()
	{
	
	}
}
