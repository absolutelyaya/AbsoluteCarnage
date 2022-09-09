package yaya.absolutecarnage.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.sound.SoundEvents;
import yaya.absolutecarnage.registries.CarnageDamageSources;

public class AcidStatusEffect extends StatusEffect
{
	public AcidStatusEffect()
	{
		super(StatusEffectCategory.HARMFUL, 0xb6d53c);
	}
	
	//TODO: replace acid effect icon. Currently it's using the future "slimed" effect icon.
	
	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier)
	{
		int i = 25 >> amplifier;
		if (i > 0)
			return duration % i == 0;
		else
			return true;
	}
	
	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier)
	{
		entity.damage(CarnageDamageSources.ACID, 1 << amplifier);
		if(shouldMakeNoise(amplifier))
			entity.playSound(SoundEvents.ENTITY_GENERIC_BURN, 1f, 1.25f);
	}
	
	boolean shouldMakeNoise(int amplifier)
	{
		return 25 >> amplifier > 1;
	}
}
