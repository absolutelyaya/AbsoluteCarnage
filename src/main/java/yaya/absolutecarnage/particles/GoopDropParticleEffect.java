package yaya.absolutecarnage.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3f;
import yaya.absolutecarnage.registries.ParticleRegistry;

public class GoopDropParticleEffect extends AbstractGoopParticleEffect
{
	public GoopDropParticleEffect(Vec3f color, float scale)
	{
		super(color, scale);
	}
	
	@Override
	public ParticleType<?> getType()
	{
		return ParticleRegistry.GOOP_DROP;
	}
	
	public static class Factory implements ParticleEffect.Factory<GoopDropParticleEffect>
	{
		@Override
		public GoopDropParticleEffect read(ParticleType type, StringReader reader) throws CommandSyntaxException
		{
			Vec3f vec3f = AbstractGoopParticleEffect.readVec3(reader);
			reader.expect(' ');
			float f = reader.readFloat();
			return new GoopDropParticleEffect(vec3f, f);
		}
		
		@Override
		public GoopDropParticleEffect read(ParticleType type, PacketByteBuf buf)
		{
			return new GoopDropParticleEffect(readVec3(buf), buf.readFloat());
		}
	}
}
