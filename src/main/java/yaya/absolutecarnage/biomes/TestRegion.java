package yaya.absolutecarnage.biomes;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import terrablender.api.ParameterUtils;
import terrablender.api.ParameterUtils.*;
import terrablender.api.Region;
import terrablender.api.RegionType;
import yaya.absolutecarnage.AbsoluteCarnage;

import java.util.List;
import java.util.function.Consumer;

public class TestRegion extends Region
{
	public TestRegion(Identifier name, int weight)
	{
		super(name, RegionType.OVERWORLD, weight);
	}
	
	@Override
	public void addBiomes(Registry<Biome> registry, Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> mapper)
	{
		this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
			builder.replaceBiome(BiomeKeys.DESERT, RegistryKey.of(Registry.BIOME_KEY, new Identifier(AbsoluteCarnage.MOD_ID, "crawling_sands")));
		});
	}
}
