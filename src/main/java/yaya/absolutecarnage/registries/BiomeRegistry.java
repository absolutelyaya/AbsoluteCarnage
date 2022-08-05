package yaya.absolutecarnage.registries;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import yaya.absolutecarnage.AbsoluteCarnage;

public class BiomeRegistry
{
	public static final RegistryKey<Biome> CRAWLING_SANDS = RegistryKey.of(Registry.BIOME_KEY, new Identifier(AbsoluteCarnage.MOD_ID, "crawling_sands"));
}
