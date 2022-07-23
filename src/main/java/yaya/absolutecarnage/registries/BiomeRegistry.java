package yaya.absolutecarnage.registries;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.biomes.OverworldBiomes;

public class BiomeRegistry
{
	public static RegistryKey<Biome> INFESTED_CAVERN = RegistryKey.of(Registry.BIOME_KEY, new Identifier(AbsoluteCarnage.MOD_ID, "infested_cavern"));
	
	public static void registerBiomes()
	{
		register(INFESTED_CAVERN, OverworldBiomes.infestedCavern());
	}
	
	private static void register(RegistryKey<Biome> key, Biome biome)
	{
		Registry.register(BuiltinRegistries.BIOME, key, biome);
	}
}
