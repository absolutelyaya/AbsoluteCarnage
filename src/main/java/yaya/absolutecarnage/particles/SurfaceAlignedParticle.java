package yaya.absolutecarnage.particles;

import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class SurfaceAlignedParticle extends SpriteBillboardParticle
{
	protected final SpriteProvider spriteProvider;
	protected final Vec3f dir;
	
	protected float deformation;
	float[] maxDeform;
	
	protected SurfaceAlignedParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider,
									 Vec3f color, float scale, Vec3d dir)
	{
		super(world, x, y, z);
		this.scale = this.random.nextFloat() * scale * 0.5f + scale * 0.25f;
		this.spriteProvider = spriteProvider;
		sprite = spriteProvider.getSprite(random);
		gravityStrength = 0;
		angle = random.nextFloat() * 360;
		setColor(color.getX(), color.getY(), color.getZ());
		
		boolean b = dir.x != 0;
		if(dir.y != 0)
		{
			if(b)
				markDead();
			b = true;
		}
		if(dir.z != 0 && b)
			markDead();
		
		if(dead)
			this.scale = 0;
		
		this.dir = new Vec3f((float)Math.round(dir.x), (float)Math.round(dir.y), (float)Math.round(dir.z));
		
		if(dir.y == 0)
			maxDeform = new float[] {
					random.nextFloat(),
					random.nextFloat(),
					random.nextFloat(),
					random.nextFloat()
			};
		else
			maxDeform = new float[] {
					random.nextBoolean() ? random.nextFloat() : 0,
					random.nextBoolean() ? random.nextFloat() : 0,
					random.nextBoolean() ? random.nextFloat() : 0,
					random.nextBoolean() ? random.nextFloat() : 0
			};
	}
	
	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta)
	{
		Vec3d camPos = camera.getPos();
		float f = (float)(MathHelper.lerp(tickDelta, this.prevPosX, this.x) - camPos.getX());
		float g = (float)(MathHelper.lerp(tickDelta, this.prevPosY, this.y) - camPos.getY());
		float h = (float)(MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - camPos.getZ());
		
		Vec3f dir = this.dir;
		Vec3f finalDir = dir;
		dir = new Vec3f(dir.getX() == 0 ? 1 : 0, dir.getY() == 0 ? 1 : 0, dir.getZ() == 0 ? 1 : 0);
		
		List<Vec3f> verts = List.of(
				new Vec3f(-dir.getX(), -dir.getY(), -dir.getZ()),
				new Vec3f(-dir.getX(), (dir.getX() == 0 ? -1 : 1) * dir.getY(), dir.getZ()),
				new Vec3f(dir.getX(), dir.getY(), dir.getZ()),
				new Vec3f(dir.getX(), (dir.getZ() == 0 ? -1 : 1) * dir.getY(), -dir.getZ()));
		
		AtomicInteger vert = new AtomicInteger();
		verts.forEach(i ->
		{
			//random rotation
			i.rotate(Quaternion.fromEulerXyzDegrees(new Vec3f(finalDir.getX() * angle, finalDir.getY() * angle, finalDir.getZ() * angle)));
			//deformation
			if(!(this.dir.getY() > 0))
			{
				i.subtract(new Vec3f(0, deformation * maxDeform[vert.get()], 0));
			}
			vert.getAndIncrement();
		});
		
		for(int k = 0; k < 4; ++k)
		{
			Vec3f vec3f = verts.get(k);
			vec3f.scale(scale);
			vec3f.add(f, g, h);
		}
		
		int n = this.getBrightness(tickDelta);
		vertexConsumer.vertex(verts.get(0).getX(), verts.get(0).getY(), verts.get(0).getZ()).texture(getMaxU(), getMaxV()).color(this.red, this.green, this.blue, this.alpha).light(n).next();
		vertexConsumer.vertex(verts.get(1).getX(), verts.get(1).getY(), verts.get(1).getZ()).texture(getMaxU(), getMinV()).color(this.red, this.green, this.blue, this.alpha).light(n).next();
		vertexConsumer.vertex(verts.get(2).getX(), verts.get(2).getY(), verts.get(2).getZ()).texture(getMinU(), getMinV()).color(this.red, this.green, this.blue, this.alpha).light(n).next();
		vertexConsumer.vertex(verts.get(3).getX(), verts.get(3).getY(), verts.get(3).getZ()).texture(getMinU(), getMaxV()).color(this.red, this.green, this.blue, this.alpha).light(n).next();
		vertexConsumer.vertex(verts.get(3).getX(), verts.get(3).getY(), verts.get(3).getZ()).texture(getMinU(), getMaxV()).color(this.red, this.green, this.blue, this.alpha).light(n).next();
		vertexConsumer.vertex(verts.get(2).getX(), verts.get(2).getY(), verts.get(2).getZ()).texture(getMinU(), getMinV()).color(this.red, this.green, this.blue, this.alpha).light(n).next();
		vertexConsumer.vertex(verts.get(1).getX(), verts.get(1).getY(), verts.get(1).getZ()).texture(getMaxU(), getMinV()).color(this.red, this.green, this.blue, this.alpha).light(n).next();
		vertexConsumer.vertex(verts.get(0).getX(), verts.get(0).getY(), verts.get(0).getZ()).texture(getMaxU(), getMaxV()).color(this.red, this.green, this.blue, this.alpha).light(n).next();
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if(world.getBlockState(new BlockPos(x - dir.getX(), y - dir.getY(), z - dir.getZ())).isAir())
			markDead();
		deformation = (float)age / maxAge;
	}
}
