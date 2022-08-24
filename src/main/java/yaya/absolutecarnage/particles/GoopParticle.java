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
	protected GoopParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider, Vec3f color, float scale, Vec3d dir)
	{
		super(world, x, y, z, spriteProvider, color, scale, dir);
		maxAge = 300;
	}
	
	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
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
