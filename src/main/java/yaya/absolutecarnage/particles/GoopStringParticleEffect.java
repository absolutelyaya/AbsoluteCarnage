package yaya.absolutecarnage.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3f;
import yaya.absolutecarnage.registries.ParticleRegistry;

public class GoopStringParticleEffect extends AbstractDustParticleEffect
{
	public GoopStringParticleEffect(Vec3f color, float scale)
	{
		super(color, scale);
	}
	
	@Override
	public ParticleType<?> getType()
	{
		return ParticleRegistry.GOOP_STRING;
	}
	
	public static class Factory implements ParticleEffect.Factory<GoopStringParticleEffect>
	{
		@Override
		public GoopStringParticleEffect read(ParticleType type, StringReader reader) throws CommandSyntaxException
		{
			Vec3f vec3f = AbstractDustParticleEffect.readColor(reader);
			reader.expect(' ');
			float f = reader.readFloat();
			return new GoopStringParticleEffect(vec3f, f);
		}
		
		@Override
		public GoopStringParticleEffect read(ParticleType type, PacketByteBuf buf)
		{
			return new GoopStringParticleEffect(readColor(buf), buf.readFloat());
		}
	}
}
