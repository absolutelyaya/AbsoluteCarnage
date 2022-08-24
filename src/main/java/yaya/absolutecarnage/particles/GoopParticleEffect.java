package yaya.absolutecarnage.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import yaya.absolutecarnage.registries.ParticleRegistry;

public class GoopParticleEffect extends AbstractDustParticleEffect
{
	public static final Codec<GoopParticleEffect> CODEC;
	protected final Vec3d dir;
	
	public GoopParticleEffect(Vec3f color, float scale, Vec3d dir)
	{
		super(color, scale);
		this.dir = dir;
	}
	
	@Override
	public ParticleType<?> getType()
	{
		return ParticleRegistry.GOOP;
	}
	
	public static class Factory implements ParticleEffect.Factory<GoopParticleEffect>
	{
		@Override
		public GoopParticleEffect read(ParticleType type, StringReader reader) throws CommandSyntaxException
		{
			Vec3f vec3f = AbstractDustParticleEffect.readColor(reader);
			reader.expect(' ');
			float f = reader.readFloat();
			reader.expect(' ');
			Vec3f dir = readColor(reader);
			return new GoopParticleEffect(vec3f, f, new Vec3d(dir.getX(), dir.getY(), dir.getZ()));
		}
		
		@Override
		public GoopParticleEffect read(ParticleType type, PacketByteBuf buf)
		{
			return new GoopParticleEffect(readColor(buf), buf.readFloat(),
				new Vec3d(buf.readFloat(), buf.readFloat(), buf.readFloat()));
		}
	}
	
	public Vec3d getDir()
	{
		return dir;
	}
	
	static
	{
		CODEC = RecordCodecBuilder.create(
				(instance) -> instance.group(Vec3f.CODEC.fieldOf("color").forGetter((effect) -> effect.color),
						Codec.FLOAT.fieldOf("scale").forGetter((effect) -> effect.scale),
						Vec3d.CODEC.fieldOf("dir").forGetter((effect) -> effect.dir))
								.apply(instance, GoopParticleEffect::new));
	}
}
