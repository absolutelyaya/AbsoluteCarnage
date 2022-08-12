package yaya.absolutecarnage.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yaya.absolutecarnage.entities.projectile.FlameProjectile;
import yaya.absolutecarnage.utility.TranslationUtil;

import java.util.function.Predicate;

public class FlameThrower extends RangedWeaponItem
{
	public FlameThrower(Settings settings)
	{
		super(settings);
	}
	
	@Override
	public Predicate<ItemStack> getProjectiles()
	{
		return null;
	}
	
	@Override
	public int getRange()
	{
		return 10;
	}
	
	void fire(LivingEntity user)
	{
		FlameProjectile projectile = FlameProjectile.spawn(user, user.world);
		Vec3d dir = user.getRotationVector();
		projectile.setVelocity(dir.x, dir.y - 0.1, dir.z, 1.6F, 16.0F);
		user.world.spawnEntity(projectile);
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		ItemStack itemStack = user.getStackInHand(hand);
		if (world.isClient) {
			return TypedActionResult.pass(itemStack);
		}
		else
		{
			user.setCurrentHand(hand);
			return TypedActionResult.success(itemStack, false);
		}
	}
	
	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
	{
		if(remainingUseTicks % (remainingUseTicks < getMaxUseTime(stack) / 2 ? 3 : 6) == 0)
		{
			for (int i = 0; i < 5; i++)
				fire(user);
			Vec3d pos = user.getPos();
			world.playSound(pos.x, pos.y, pos.z, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1f,
					0.5f + user.getRandom().nextFloat() * 0.25f, true);
		}
		if(user instanceof PlayerEntity)
		{
			if (remainingUseTicks == 1)
			{
				((PlayerEntity)user).getItemCooldownManager().set(this, 300);
				((PlayerEntity)user).sendMessage(TranslationUtil.getText("msg", "flamethrower.overheat"), true);
			}
		}
	}
	
	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		if(user instanceof PlayerEntity && remainingUseTicks > 0)
			((PlayerEntity)user).getItemCooldownManager().set(this, 10);
		user.clearActiveItem();
	}
	
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}
	
	@Override
	public int getMaxUseTime(ItemStack stack)
	{
		return 150;
	}
}
