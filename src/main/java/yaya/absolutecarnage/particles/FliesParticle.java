package yaya.absolutecarnage.particles;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.*;

import java.util.Random;

//This class is a modified version of my Snowflake particle in the WeatherEffects mod. That's why some variable names are weird
public class FliesParticle extends SpriteBillboardParticle
{
	boolean disappearing;
	float groundTime, maxGroundTime, startScale, meltSpeed;
	Vec2f wind = new Vec2f(0, 0);
	
	protected FliesParticle(ClientWorld world, double x, double y, double z)
	{
		super(world, x, y, z);
		gravityStrength = 0f;
		startScale = 0.05f + random.nextFloat() * 0.1f;
		meltSpeed = 1f;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		alpha = Math.min(((float)this.age) / (float)this.maxAge * 20, 1f);
		scale = startScale * alpha;
		BlockPos pos = new BlockPos(new Vec3d(x, y, z));
		FluidState state = world.getFluidState(pos);
		if(age > maxAge - 20)
			disappearing = true;
		if (disappearing || state.isIn(FluidTags.WATER) || state.isIn(FluidTags.LAVA))
		{
			if(groundTime == 0)
			{
				if(state.isIn(FluidTags.LAVA))
				{
					world.addParticle(ParticleTypes.SMOKE, x, pos.getY() + 0.9, z, 0, 0, 0);
					markDead();
				}
			}
			setAlpha(alpha - groundTime / maxGroundTime);
			scale = startScale * Math.max(alpha - groundTime / maxGroundTime, 0);
			if ((groundTime += meltSpeed) > maxGroundTime)
			{
				markDead();
			}
		}
		else
			setAlpha(alpha);
		velocityY = Math.sin(age / 3f) * Math.sin(age / 20f) * 0.1f;
		wind();
	}
	
	void wind()
	{
		if(age % 20 == 1)
		{
			wind = new Vec2f((random.nextFloat() - 0.5f) * 2f, (random.nextFloat() - 0.5f) * 2f);
		}
		velocityX = MathHelper.lerp(0.02D, velocityX, wind.x * 0.5);
		velocityZ = MathHelper.lerp(0.02D, velocityZ, wind.y * 0.5);
	}
	
	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	public void setMaxGroundTime(int maxGroundTime)
	{
		this.maxGroundTime = maxGroundTime;
	}
	
	public record FliesParticleFactory(SpriteProvider spriteProvider) implements ParticleFactory<DefaultParticleType>
	{
		@Override
		public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			Random r = new Random();
			FliesParticle fly = new FliesParticle(world, x, y, z);
			fly.setSprite(spriteProvider);
			fly.setMaxGroundTime(10 + r.nextInt(15));
			fly.setMaxAge(200);
			fly.setAlpha(0f);
			return fly;
		}
	}
}
