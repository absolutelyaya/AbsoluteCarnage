package yaya.absolutecarnage.registries;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import yaya.absolutecarnage.AbsoluteCarnage;

public class ParticleRegistry
{
	public static final DefaultParticleType FLIES = register("flies", FabricParticleTypes.simple());
	
	static DefaultParticleType register(String name, DefaultParticleType type)
	{
		return Registry.register(Registry.PARTICLE_TYPE, new Identifier(AbsoluteCarnage.MOD_ID, name), type);
	}
	
	public static void registerParticles()
	{
	
	}
}
