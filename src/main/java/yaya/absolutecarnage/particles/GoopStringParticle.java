package yaya.absolutecarnage.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;

public class GoopStringParticle extends SpriteAAParticle
{
	protected GoopStringParticle(ClientWorld world, Vec3d pos, SpriteProvider spriteProvider, Vec3f color, float scale)
	{
		super(world, pos.x, pos.y - 0.25, pos.z, spriteProvider);
		gravityStrength = random.nextFloat() * 0.25f + 0.1f;
		maxAge = random.nextInt(15) + 20;
		setColor(color.getX(), color.getY(), color.getZ());
		this.scale.scale(scale);
		collidesWithWorld = true;
	}
	
	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	@Override
	public void move(double dx, double dy, double dz)
	{
		///TODO: fix jiterring somehow. maybe using last y delta instead would already fix it?
		super.move(dx, dy, dz);
		if (!onGround)
		{
			scale.add(-0.001f, (float)-dy, -0.001f);
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		alpha = MathHelper.lerp((float)age / maxAge, 1.2f, 0f);
	}
	
	public static class GoopStringParticleFactory implements ParticleFactory<GoopStringParticleEffect>
	{
		protected final SpriteProvider spriteProvider;
		
		public GoopStringParticleFactory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}
		
		@Nullable
		@Override
		public Particle createParticle(GoopStringParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			return new GoopStringParticle(world, new Vec3d(x, y, z), spriteProvider, parameters.getColor(), parameters.getScale());
		}
	}
}
