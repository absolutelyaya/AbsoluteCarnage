package yaya.absolutecarnage.particles;

import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.*;

import java.util.List;

public abstract class SurfaceAlignedParticle extends SpriteBillboardParticle
{
	protected final SpriteProvider spriteProvider;
	protected final Vec3f dir;
	
	protected SurfaceAlignedParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider,
									 Vec3f color, float scale, Vec3d dir)
	{
		super(world, x, y, z);
		this.scale = this.random.nextFloat() * scale * 0.5f + scale * 0.25f;
		this.spriteProvider = spriteProvider;
		sprite = spriteProvider.getSprite(random);
		this.dir = new Vec3f((float)Math.round(dir.x), (float)Math.round(dir.y), (float)Math.round(dir.z));
		gravityStrength = 0;
		angle = random.nextFloat() * 360;
		setColor(color.getX(), color.getY(), color.getZ());
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
		
		List<Vec3f> vec3fs = List.of(
				new Vec3f(-dir.getX(), -dir.getY(), -dir.getZ()),
				new Vec3f(-dir.getX(), (dir.getX() == 0 ? -1 : 1) * dir.getY(), dir.getZ()),
				new Vec3f(dir.getX(), dir.getY(), dir.getZ()),
				new Vec3f(dir.getX(), (dir.getZ() == 0 ? -1 : 1) * dir.getY(), -dir.getZ()));
		
		vec3fs.forEach(i -> i.rotate(Quaternion.fromEulerXyzDegrees(
				new Vec3f(finalDir.getX() * angle, finalDir.getY() * angle, finalDir.getZ() * angle))));
		
		for(int k = 0; k < 4; ++k)
		{
			Vec3f vec3f = vec3fs.get(k);
			vec3f.scale(scale);
			vec3f.add(f, g, h);
		}
		
		int n = this.getBrightness(tickDelta);
		vertexConsumer.vertex(vec3fs.get(0).getX(), vec3fs.get(0).getY(), vec3fs.get(0).getZ()).texture(getMaxU(), getMaxV()).color(this.red, this.green, this.blue, this.alpha).light(n).next();
		vertexConsumer.vertex(vec3fs.get(1).getX(), vec3fs.get(1).getY(), vec3fs.get(1).getZ()).texture(getMaxU(), getMinV()).color(this.red, this.green, this.blue, this.alpha).light(n).next();
		vertexConsumer.vertex(vec3fs.get(2).getX(), vec3fs.get(2).getY(), vec3fs.get(2).getZ()).texture(getMinU(), getMinV()).color(this.red, this.green, this.blue, this.alpha).light(n).next();
		vertexConsumer.vertex(vec3fs.get(3).getX(), vec3fs.get(3).getY(), vec3fs.get(3).getZ()).texture(getMinU(), getMaxV()).color(this.red, this.green, this.blue, this.alpha).light(n).next();
		vertexConsumer.vertex(vec3fs.get(3).getX(), vec3fs.get(3).getY(), vec3fs.get(3).getZ()).texture(getMinU(), getMaxV()).color(this.red, this.green, this.blue, this.alpha).light(n).next();
		vertexConsumer.vertex(vec3fs.get(2).getX(), vec3fs.get(2).getY(), vec3fs.get(2).getZ()).texture(getMinU(), getMinV()).color(this.red, this.green, this.blue, this.alpha).light(n).next();
		vertexConsumer.vertex(vec3fs.get(1).getX(), vec3fs.get(1).getY(), vec3fs.get(1).getZ()).texture(getMaxU(), getMinV()).color(this.red, this.green, this.blue, this.alpha).light(n).next();
		vertexConsumer.vertex(vec3fs.get(0).getX(), vec3fs.get(0).getY(), vec3fs.get(0).getZ()).texture(getMaxU(), getMaxV()).color(this.red, this.green, this.blue, this.alpha).light(n).next();
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if(world.getBlockState(new BlockPos(x - dir.getX(), y - dir.getY(), z - dir.getZ())).isAir())
			markDead();
	}
}
