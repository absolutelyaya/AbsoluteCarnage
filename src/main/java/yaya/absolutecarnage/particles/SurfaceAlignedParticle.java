package yaya.absolutecarnage.particles;

import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class SurfaceAlignedParticle extends SpriteBillboardParticle
{
	private final List<Vec3f> verts = new ArrayList<>();
	private final List<Vec2f> uvs = new ArrayList<>();
	private final List<Float> maxDeform = new ArrayList<>();
	protected final SpriteProvider spriteProvider;
	protected final Vec3f dir;
	
	protected float deformation;
	float targetSize;
	
	protected SurfaceAlignedParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider,
									 Vec3f color, float scale, Vec3d dir)
	{
		super(world, x, y, z);
		this.targetSize = scale;
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
		
		this.dir = new Vec3f((float)Math.round(dir.x), (float)Math.round(dir.y), (float)Math.round(dir.z));
		
		if(dead)
		{
			this.scale = 0;
			return;
		}
		
		Vec3f modDir = new Vec3f(dir.getX() == 0 ? 1 : 0, dir.getY() == 0 ? 1 : 0, dir.getZ() == 0 ? 1 : 0);
		float s = Math.max(targetSize, 1);
		for(int vy = 0; vy <= s; vy++)
			for (int vx = 0; vx <= s; vx++)
			{
				Vec3f vert;
				if(dir.y != 0)
					vert = new Vec3f(modDir.getX() * vx / s, modDir.getY(), modDir.getZ() * vy / s);
				else
					vert = new Vec3f(modDir.getX() * vx / s, modDir.getY() * vy / s, modDir.getZ() * vx / s);
				verts.add(vert);
				uvs.add(new Vec2f(MathHelper.lerp(vx / s, getMinU(), getMaxU()), MathHelper.lerp(vy / s, getMinV(), getMaxV())));
				if(dir.y == 0)
					maxDeform.add(random.nextFloat());
				else
					maxDeform.add(random.nextBoolean() ? random.nextFloat() : 0);
			}
	}
	
	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta)
	{
		if(verts.size() == 0)
			return;
		
		Vec3d camPos = camera.getPos();
		float f = (float)(MathHelper.lerp(tickDelta, this.prevPosX, this.x) - camPos.getX());
		float g = (float)(MathHelper.lerp(tickDelta, this.prevPosY, this.y) - camPos.getY());
		float h = (float)(MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - camPos.getZ());
		
		Vec3f dir = this.dir;
		
		List<Vec3f> verts = new ArrayList<>();
		Vec3f modDir = new Vec3f(dir.getX() == 0 ? 1 : 0, dir.getY() == 0 ? 1 : 0, dir.getZ() == 0 ? 1 : 0);
		this.verts.forEach(i ->
		{
			Vec3f v = i.copy();
			v.subtract(new Vec3f((float)(modDir.getX() * 0.5), (float)(modDir.getY() * 0.5), (float)(modDir.getZ() * 0.5)));
			verts.add(v);
		});
		
		AtomicInteger vert = new AtomicInteger();
		verts.forEach(i ->
		{
			//random rotation
			i.rotate(Quaternion.fromEulerXyzDegrees(new Vec3f(dir.getX() * angle, dir.getY() * angle, dir.getZ() * angle)));
			//deformation
			if(!(this.dir.getY() > 0))
				i.subtract(new Vec3f(0, deformation * maxDeform.get(vert.get()), 0));
			vert.getAndIncrement();
		});
		
		for (Vec3f vec3f : verts)
		{
			vec3f.scale(scale);
			vec3f.add(f, g, h);
		}
		
		int n = this.getBrightness(tickDelta);
		float ts = Math.max(targetSize, 1);
		for (int y = 1, vi = 0; y < (int)ts + 1; y++, vi++)
		{
			for (int x = 1; x < (int)ts + 1; x++, vi++)
			{
				//top
				vertexConsumer.vertex(verts.get(vi).getX(), verts.get(vi).getY(), verts.get(vi).getZ()).texture(uvs.get(vi).x, uvs.get(vi).y).color(this.red, this.green, this.blue, this.alpha).light(n).next();
				vertexConsumer.vertex(verts.get((int)(vi + ts + 1)).getX(), verts.get((int)(vi + ts + 1)).getY(), verts.get((int)(vi + ts + 1)).getZ()).texture(uvs.get((int)(vi + ts + 1)).x, uvs.get((int)(vi + ts + 1)).y).color(this.red, this.green, this.blue, this.alpha).light(n).next();
				vertexConsumer.vertex(verts.get((int)(vi + ts + 2)).getX(), verts.get((int)(vi + ts + 2)).getY(), verts.get((int)(vi + ts + 2)).getZ()).texture(uvs.get((int)(vi + ts + 2)).x, uvs.get((int)(vi + ts + 2)).y).color(this.red, this.green, this.blue, this.alpha).light(n).next();
				vertexConsumer.vertex(verts.get(vi + 1).getX(), verts.get(vi + 1).getY(), verts.get(vi + 1).getZ()).texture(uvs.get(vi + 1).x, uvs.get(vi + 1).y).color(this.red, this.green, this.blue, this.alpha).light(n).next();
				//bottom
				vertexConsumer.vertex(verts.get(vi + 1).getX(), verts.get(vi + 1).getY(), verts.get(vi + 1).getZ()).texture(uvs.get(vi + 1).x, uvs.get(vi + 1).y).color(this.red, this.green, this.blue, this.alpha).light(n).next();
				vertexConsumer.vertex(verts.get((int)(vi + ts + 2)).getX(), verts.get((int)(vi + ts + 2)).getY(), verts.get((int)(vi + ts + 2)).getZ()).texture(uvs.get((int)(vi + ts + 2)).x, uvs.get((int)(vi + ts + 2)).y).color(this.red, this.green, this.blue, this.alpha).light(n).next();
				vertexConsumer.vertex(verts.get((int)(vi + ts + 1)).getX(), verts.get((int)(vi + ts + 1)).getY(), verts.get((int)(vi + ts + 1)).getZ()).texture(uvs.get((int)(vi + ts + 1)).x, uvs.get((int)(vi + ts + 1)).y).color(this.red, this.green, this.blue, this.alpha).light(n).next();
				vertexConsumer.vertex(verts.get(vi).getX(), verts.get(vi).getY(), verts.get(vi).getZ()).texture(uvs.get(vi).x, uvs.get(vi).y).color(this.red, this.green, this.blue, this.alpha).light(n).next();
			}
		}
		
		//TODO: wrap to block edges
		//TODO: figure out why some negative X & Z (bottom) wall goops render weirdly
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
