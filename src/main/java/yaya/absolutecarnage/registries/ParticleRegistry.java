package yaya.absolutecarnage.registries;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.particles.GoopDropParticleEffect;
import yaya.absolutecarnage.particles.GoopParticle;
import yaya.absolutecarnage.particles.GoopParticleEffect;
import yaya.absolutecarnage.particles.GoopStringParticleEffect;

public class ParticleRegistry
{
	public static final DefaultParticleType FLIES = register("flies", FabricParticleTypes.simple());
	public static final ParticleType<GoopDropParticleEffect> GOOP_DROP =
			Registry.register(Registry.PARTICLE_TYPE, new Identifier(AbsoluteCarnage.MOD_ID, "goop_drop"),
					FabricParticleTypes.complex(new GoopDropParticleEffect.Factory()));
	public static final ParticleType<GoopParticleEffect> GOOP =
			Registry.register(Registry.PARTICLE_TYPE, new Identifier(AbsoluteCarnage.MOD_ID, "goop"),
					FabricParticleTypes.complex(new GoopParticleEffect.Factory()));
	public static final ParticleType<GoopStringParticleEffect> GOOP_STRING =
			Registry.register(Registry.PARTICLE_TYPE, new Identifier(AbsoluteCarnage.MOD_ID, "goop_string"),
					FabricParticleTypes.complex(new GoopStringParticleEffect.Factory()));
	
	@SuppressWarnings("SameParameterValue")
	static DefaultParticleType register(String name, DefaultParticleType type)
	{
		return Registry.register(Registry.PARTICLE_TYPE, new Identifier(AbsoluteCarnage.MOD_ID, name), type);
	}
	
	public static void registerParticles()
	{
	
	}
}
