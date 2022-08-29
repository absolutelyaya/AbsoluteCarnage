package yaya.absolutecarnage.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class GoopDropParticle extends SpriteBillboardParticle
{
	protected final SpriteProvider spriteProvider;
	protected final Vec3f color;
	float rotSpeed;
	
	protected GoopDropParticle(ClientWorld clientWorld, Vec3d pos, Vec3d vel, SpriteProvider spriteProvider, Vec3f color, float scale)
	{
		super(clientWorld, pos.x, pos.y, pos.z);
		setColor(color.getX(), color.getY(), color.getZ());
		this.color = color;
		this.scale = scale;
		this.spriteProvider = spriteProvider;
		sprite = spriteProvider.getSprite(random);
		gravityStrength = 1 + scale / 2;
		maxAge = 300;
		setVelocity(random.nextFloat() * 0.5 - 0.25, random.nextFloat() * 0.5, random.nextFloat() * 0.5 - 0.25);
		collidesWithWorld = true;
		
		rotSpeed = (random.nextFloat() - 0.5f) * 0.25f;
		
		if(vel.distanceTo(Vec3d.ZERO) > 0)
			setVelocity(vel.x, vel.y, vel.z);
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
		prevAngle = angle;
		angle += rotSpeed;
	}
	
	void nextParticle(BlockPos pos, Vec3d dir)
	{
		if(!world.getBlockState(pos).isAir())
		{
			//Vec3d dir = new Vec3d(x, y, z).subtract(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
			
			dir = dir.normalize();
			
			Vec3d offset = new Vec3d(1, 1, 1).multiply(Math.max(random.nextFloat() * 0.02f, 0.01f));
			offset = offset.add(dir.x < 0 ? 0 : 1, dir.y < 0 ? 0 : 1, dir.z < 0 ? 0 : 1);
			
			if(dir.y == 1)
			{
				world.addParticle(new GoopParticleEffect(color, scale * 2.5f, dir),
						x + dir.x * offset.x, pos.getY() + dir.y * offset.y, z + dir.z * offset.z,
						0, 0, 0);
			}
			else
				world.addParticle(new GoopParticleEffect(color, scale * 2.5f, dir),
						pos.getX() + dir.x * offset.x, pos.getY() + dir.y * offset.y, pos.getZ() + dir.z * offset.z,
						0, 0, 0);
		}
	}
	
	@Override
	public void move(double dx, double dy, double dz)
	{
		if (this.collidesWithWorld && (dx != 0.0 || dy != 0.0 || dz != 0.0) && dx * dx + dy * dy + dz * dz < MathHelper.square(100.0))
		{
			Iterator<VoxelShape> it = world.getBlockCollisions(null, getBoundingBox().stretch(new Vec3d(dx, dy, dz))).iterator();
			if(it.hasNext())
			{
				VoxelShape shape = it.next();
				Vec3d point = shape.getBoundingBox().getCenter();
				Vec3d vec3d = Entity.adjustMovementForCollisions(null, new Vec3d(dx, dy, dz), this.getBoundingBox(), this.world, List.of());
				Vec3d diff = vec3d.subtract(new Vec3d(dx, dy, dz));
				
				nextParticle(new BlockPos(point), diff);
				markDead();
			}
		}
		super.move(dx, dy, dz);
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
			return new GoopDropParticle(world, new Vec3d(x, y, z), new Vec3d(velocityX, velocityY, velocityZ),
					spriteProvider, parameters.getColor(), parameters.getScale());
		}
	}
}
