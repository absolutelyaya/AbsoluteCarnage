package yaya.absolutecarnage.particles;

import yaya.yayconfig.settings.SettingsStorage;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.*;
import yaya.absolutecarnage.settings.Settings;

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
		
		this.dir = new Vec3f((float)Math.round(dir.x), (float)Math.round(dir.y), (float)Math.round(dir.z));
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
		
		AtomicInteger atomicInt = new AtomicInteger();
		verts.forEach(i ->
		{
			//random rotation
			i.rotate(Quaternion.fromEulerXyzDegrees(new Vec3f(dir.getX() * angle, dir.getY() * angle, dir.getZ() * angle)));
			//deformation
			if(!(this.dir.getY() > 0))
				i.subtract(new Vec3f(0, deformation * maxDeform.get(atomicInt.get()), 0));
			i.scale(scale);
			i.add(f, g, h);
			atomicInt.getAndIncrement();
		});
		
		int n = this.getBrightness(tickDelta);
		float ts = Math.max(targetSize, 1);
		
		for (int y = 1, vi = 0; y < (int)ts + 1; y++, vi++)
		{
			for (int x = 1; x < (int)ts + 1; x++, vi++)
			{
				Vec3f[] modVerts = new Vec3f[] {verts.get(vi).copy(), verts.get((int)(vi + ts + 1)).copy(),
						verts.get((int)(vi + ts + 2)).copy(), verts.get(vi + 1).copy()};
				
				boolean grounded = true;
				
				if(dir.getY() != 0)
				{
					Vec3f faceCenter = modVerts[0].copy();
					faceCenter.add(modVerts[1]);
					faceCenter.add(modVerts[2]);
					faceCenter.add(modVerts[3]);
					faceCenter.scale(0.25f);
					
					grounded = !world.isAir(new BlockPos(camPos.add(new Vec3d(faceCenter))).down());
					
					
					if(SettingsStorage.getBoolean(Settings.DEBUG_SURFACEALIGNED_PARTICLE.id))
					{
						world.addParticle(ParticleTypes.FLAME,
								camPos.x + faceCenter.getX(), camPos.y + faceCenter.getY() + 0.1, camPos.z + faceCenter.getZ(),
								0, 0.05, 0);
					}
					
					//if(!grounded)
					//{
					//	for (Vec3f mv : modVerts)
					//	{
					//		Vec3f camPosF = new Vec3f(camPos);
					//		mv.add(camPosF);
					//		moveToBlockEdge(mv);
					//		mv.subtract(camPosF);
					//	}
					//}
				}
				
				if(grounded)
				{
					//top
					vertexConsumer.vertex(modVerts[0].getX(), modVerts[0].getY(), modVerts[0].getZ()).texture(uvs.get(vi).x, uvs.get(vi).y).color(this.red, this.green, this.blue, this.alpha).light(n).next();
					vertexConsumer.vertex(modVerts[1].getX(), modVerts[1].getY(), modVerts[1].getZ()).texture(uvs.get((int)(vi + ts + 1)).x, uvs.get((int)(vi + ts + 1)).y).color(this.red, this.green, this.blue, this.alpha).light(n).next();
					vertexConsumer.vertex(modVerts[2].getX(), modVerts[2].getY(), modVerts[2].getZ()).texture(uvs.get((int)(vi + ts + 2)).x, uvs.get((int)(vi + ts + 2)).y).color(this.red, this.green, this.blue, this.alpha).light(n).next();
					vertexConsumer.vertex(modVerts[3].getX(), modVerts[3].getY(), modVerts[3].getZ()).texture(uvs.get(vi + 1).x, uvs.get(vi + 1).y).color(this.red, this.green, this.blue, this.alpha).light(n).next();
					//bottom
					vertexConsumer.vertex(modVerts[3].getX(), modVerts[3].getY(), modVerts[3].getZ()).texture(uvs.get(vi + 1).x, uvs.get(vi + 1).y).color(this.red, this.green, this.blue, this.alpha).light(n).next();
					vertexConsumer.vertex(modVerts[2].getX(), modVerts[2].getY(), modVerts[2].getZ()).texture(uvs.get((int)(vi + ts + 2)).x, uvs.get((int)(vi + ts + 2)).y).color(this.red, this.green, this.blue, this.alpha).light(n).next();
					vertexConsumer.vertex(modVerts[1].getX(), modVerts[1].getY(), modVerts[1].getZ()).texture(uvs.get((int)(vi + ts + 1)).x, uvs.get((int)(vi + ts + 1)).y).color(this.red, this.green, this.blue, this.alpha).light(n).next();
					vertexConsumer.vertex(modVerts[0].getX(), modVerts[0].getY(), modVerts[0].getZ()).texture(uvs.get(vi).x, uvs.get(vi).y).color(this.red, this.green, this.blue, this.alpha).light(n).next();
				}
			}
		}
		
		if(SettingsStorage.getBoolean(Settings.DEBUG_SURFACEALIGNED_PARTICLE.id))
		{
			int x = 0, y = 0;
			for (Vec3f v : verts)
			{
				world.addParticle(new DustParticleEffect(new Vec3f(x / ts, y / ts, 0f), 0.5f),
						camPos.x + v.getX(), camPos.y + v.getY() + 0.1, camPos.z + v.getZ(),
						0, 0.05, 0);
				x++;
				if(x > (int)ts)
				{
					x = 0;
					y++;
				}
			}
			
			world.addParticle(new DustParticleEffect(new Vec3f(1f, 1f, 1f), 1f), this.x, this.y, this.z,
					0, 0.25, 0);
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
	
	private void moveToBlockEdge(Vec3f vert)
	{
		vert.set(Math.round(vert.getX()), vert.getY(), Math.round(vert.getZ()));
	}
}
