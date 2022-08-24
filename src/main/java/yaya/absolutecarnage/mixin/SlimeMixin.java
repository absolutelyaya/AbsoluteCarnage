package yaya.absolutecarnage.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yaya.absolutecarnage.particles.GoopDropParticleEffect;

@Mixin(SlimeEntity.class)
public abstract class SlimeMixin extends MobEntity
{
	@Shadow public abstract int getSize();
	
	protected SlimeMixin(EntityType<? extends MobEntity> entityType, World world)
	{
		super(entityType, world);
	}
	
	@Override
	public void onDeath(DamageSource damageSource)
	{
		super.onDeath(damageSource);
		splatter(null);
	}
	
	@Override
	protected void onKilledBy(@Nullable LivingEntity adversary)
	{
		super.onKilledBy(adversary);
		splatter(adversary);
	}
	
	protected void splatter(LivingEntity attacker)
	{
		Vec3d pos = getPos();
		Vec3d dir = Vec3d.ZERO;
		if(attacker != null)
			dir = attacker.getPos().subtract(pos).normalize().add(0, random.nextFloat() * 0.25, 0);
		
		for(int i = 0; i < random.nextInt(6) + 3 * (getSize() + 1); i++)
			world.addParticle(new GoopDropParticleEffect(getGoopColor(), 0.5f * getSize() * 0.5f),
					pos.getX(), pos.getY() + 1, pos.getZ(), dir.x, dir.y, dir.z);
		world.addParticle(new GoopDropParticleEffect(getGoopColor(), 1f * getSize() * 0.5f),
				pos.getX(), pos.getY(), pos.getZ(), 0, -1, 0);
	}
	
	@Inject(method = "tick", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/mob/SlimeEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V", shift = At.Shift.AFTER))
	public void onTick(CallbackInfo ci)
	{
		Vec3d pos = getPos();
		world.addParticle(new GoopDropParticleEffect(getGoopColor(), 0.75f * getSize() * 0.5f),
				pos.getX(), pos.getY() + 1, pos.getZ(), 0, -2, 0);
	}
	
	protected Vec3f getGoopColor()
	{
		return new Vec3f(0.01f, 0.75f, 0.4f);
	}
}
