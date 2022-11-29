package yaya.absolutecarnage.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class WebbedStatusEffect extends StatusEffect
{
	public WebbedStatusEffect()
	{
		super(StatusEffectCategory.HARMFUL, 0x00ffffff);
	}
	
	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier)
	{
		return true;
	}
	
	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier)
	{
		if(entity instanceof PlayerEntity player)
			player.sendMessage(Text.translatable("msg.absolute_carnage.webbed"), true);
	}
	
	///TODO: add freeing methods (moving and attacking) that speed up removal of this effect
	///TODO: add a way to not change FOV due to this effect
	///TODO: disable running
}
