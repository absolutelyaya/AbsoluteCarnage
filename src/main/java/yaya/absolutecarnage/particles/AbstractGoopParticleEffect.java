package yaya.absolutecarnage.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import yaya.absolutecarnage.settings.Settings;
import yaya.yayconfig.settings.SettingsStorage;

import java.util.Locale;

public abstract class AbstractGoopParticleEffect implements ParticleEffect
{
	protected final Vec3f color;
	protected final float scale;
	
	public AbstractGoopParticleEffect(Vec3f color, float scale)
	{
		this.color = color;
		this.scale = MathHelper.clamp(scale, 0.01f, (float)SettingsStorage.getDouble(Settings.GOOP_MAXSIZE.id));
	}
	
	public static Vec3f readColor(StringReader reader) throws CommandSyntaxException
	{
		reader.expect(' ');
		float f = reader.readFloat();
		reader.expect(' ');
		float g = reader.readFloat();
		reader.expect(' ');
		float h = reader.readFloat();
		return new Vec3f(f, g, h);
	}
	
	public static Vec3f readColor(PacketByteBuf buf)
	{
		return new Vec3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
	}
	
	@Override
	public void write(PacketByteBuf buf)
	{
		buf.writeFloat(this.color.getX());
		buf.writeFloat(this.color.getY());
		buf.writeFloat(this.color.getZ());
		buf.writeFloat(this.scale);
	}
	
	public String asString()
	{
		return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f",
				Registry.PARTICLE_TYPE.getId(this.getType()), this.color.getX(), this.color.getY(), this.color.getZ(), this.scale);
	}
	
	public Vec3f getColor() {
		return this.color;
	}
	
	public float getScale() {
		return this.scale;
	}
}
