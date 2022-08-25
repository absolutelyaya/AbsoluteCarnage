package yaya.absolutecarnage.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;

public class GoopParticle extends SurfaceAlignedParticle
{
	private final Vec3f color;
	
	protected GoopParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider, Vec3f color, float scale, Vec3d dir)
	{
		super(world, x, y, z, spriteProvider, color, scale, dir);
		maxAge = 300;
		this.alpha = Math.min(random.nextFloat() + 0.5f, 1);
		this.color = color;
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
		if(dir.getY() < 0 && random.nextInt(60) == 0)
			world.addParticle(new GoopStringParticleEffect(color, 0.25f),
					x + random.nextFloat() * scale - scale / 2f, y, z + random.nextFloat() * scale - scale / 2f,
					0, 0, 0);
	}
	
	public static class GoopParticleFactory implements ParticleFactory<GoopParticleEffect>
	{
		protected final SpriteProvider spriteProvider;
		
		public GoopParticleFactory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}
		
		@Nullable
		@Override
		public Particle createParticle(GoopParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			return new GoopParticle(world, x, y, z, spriteProvider, parameters.getColor(), parameters.getScale(), parameters.getDir());
		}
	}
}
