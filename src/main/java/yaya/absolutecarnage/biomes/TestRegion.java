package yaya.absolutecarnage.biomes;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import terrablender.api.ParameterUtils.*;
import terrablender.api.Region;
import terrablender.api.RegionType;
import yaya.absolutecarnage.registries.BiomeRegistry;

import java.util.List;
import java.util.function.Consumer;

public class TestRegion extends Region
{
	public TestRegion(Identifier name, RegionType type, int weight)
	{
		super(name, type, weight);
	}
	
	@Override
	public void addBiomes(Registry<Biome> registry, Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> mapper)
	{
		this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
			//builder.replaceBiome(BiomeKeys.DESERT, BiomeRegistry.INFESTED_CAVERN);
			
			List<MultiNoiseUtil.NoiseHypercube> undergroundDesert = new ParameterPointListBuilder()
					.temperature(Temperature.HOT, Temperature.WARM)
					.humidity(Humidity.ARID, Humidity.DRY)
					.continentalness(Continentalness.span(Continentalness.INLAND, Continentalness.FAR_INLAND), Continentalness.span(Continentalness.MID_INLAND, Continentalness.FAR_INLAND))
					.erosion(Erosion.EROSION_6, Erosion.EROSION_5, Erosion.EROSION_4, Erosion.EROSION_3, Erosion.EROSION_2, Erosion.EROSION_1, Erosion.EROSION_0)
					.depth(Depth.FLOOR, Depth.UNDERGROUND, Depth.SURFACE)
					.weirdness(Weirdness.FULL_RANGE)
					.build();
			
			undergroundDesert.forEach(point -> builder.replaceBiome(point, BiomeRegistry.INFESTED_CAVERN));
		});
	}
}
