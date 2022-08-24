package yaya.absolutecarnage.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;

public class GoopDropParticle extends SpriteBillboardParticle
{
	protected final SpriteProvider spriteProvider;
	protected final Vec3f color;
	
	protected GoopDropParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider, Vec3f color, float scale)
	{
		super(clientWorld, d, e, f);
		setColor(color.getX(), color.getY(), color.getZ());
		this.color = color;
		this.scale = scale;
		this.spriteProvider = spriteProvider;
		sprite = spriteProvider.getSprite(random);
		gravityStrength = 1;
		maxAge = 300;
		setVelocity(random.nextFloat() * 0.5 - 0.25, random.nextFloat() * 0.5, random.nextFloat() * 0.5 - 0.25);
		collidesWithWorld = true;
	}
	
	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if(onGround)
		{
			nextParticle(new BlockPos(new Vec3d(x, y - 0.2, z)));
			markDead();
		}
	}
	
	void nextParticle(BlockPos pos)
	{
		if(world.getBlockState(pos).isSolidBlock(world, pos))
		{
			Vec3d dir = new Vec3d(x, y, z).subtract(new Vec3d(pos.getX(), pos.getY(), pos.getZ())).normalize();
			
			world.addParticle(new GoopParticleEffect(color, scale * 5, dir),
					x, y + Math.max(random.nextFloat() * 0.02f, 0.01f), z, 0, 0, 0);
		}
	}
	
	public static class GoopDropParticleFactory implements ParticleFactory<GoopDropParticleEffect>
	{
		protected final SpriteProvider spriteProvider;
		
		public GoopDropParticleFactory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}
		
		@Nullable
		@Override
		public Particle createParticle(GoopDropParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			return new GoopDropParticle(world, x, y, z, spriteProvider, parameters.getColor(), parameters.getScale());
		}
	}
}
