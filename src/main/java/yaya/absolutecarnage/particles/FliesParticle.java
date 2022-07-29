package yaya.absolutecarnage.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.*;

import java.util.List;
import java.util.Random;

//This class is a modified version of my Snowflake particle in the WeatherEffects mod. That's why some variable names are weird
public class FliesParticle extends SpriteBillboardParticle
{
	private static final double MAX_SQUARED_COLLISION_CHECK_DISTANCE = MathHelper.square(100.0D);
	boolean disappearing;
	float groundTime, maxGroundTime, startScale, meltSpeed, ageOffset;
	Vec2f wind = new Vec2f(0, 0);
	
	protected FliesParticle(ClientWorld world, double x, double y, double z)
	{
		super(world, x, y, z);
		gravityStrength = 0f;
		startScale = 0.05f + random.nextFloat() * 0.1f;
		meltSpeed = 1f;
		ageOffset = random.nextInt() * 9;
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
				markDead();
		}
		else
			setAlpha(alpha);
		wind();
	}
	
	void wind()
	{
		if(age % 20 == 1)
			wind = new Vec2f((random.nextFloat() - 0.5f) * 2f, (random.nextFloat() - 0.5f) * 2f);
		velocityX = MathHelper.lerp(0.02D, velocityX, wind.x * 0.5);
		velocityY = MathHelper.lerp(0.02D, velocityY, Math.sin((age + ageOffset) / 3f) * Math.sin((age + ageOffset) / 20f));
		velocityZ = MathHelper.lerp(0.02D, velocityZ, wind.y * 0.5);
		//TODO: improve movement to look more erratic/insect like
	}
	
	@Override
	public void move(double dx, double dy, double dz)
	{
		double x = dx;
		double y = dy;
		double z = dz;
		if (this.collidesWithWorld && (dx != 0.0D || dy != 0.0D || dz != 0.0D) && dx * dx + dy * dy + dz * dz < MAX_SQUARED_COLLISION_CHECK_DISTANCE)
		{
			Vec3d vec3d = Entity.adjustMovementForCollisions(null, new Vec3d(dx, dy, dz), this.getBoundingBox(), this.world, List.of());
			dx = vec3d.x;
			dy = vec3d.y;
			dz = vec3d.z;
		}
		
		if (dx != 0.0D || dy != 0.0D || dz != 0.0D)
		{
			this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
			this.repositionFromBoundingBox();
		}
		
		if (x != dx)
			this.velocityX = 0.0D;
		if (y != dy)
			this.velocityY = 0.0D;
		if (z != dz)
			this.velocityZ = 0.0D;
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
			fly.setVelocity(velocityX, velocityY, velocityZ);
			return fly;
		}
	}
}
