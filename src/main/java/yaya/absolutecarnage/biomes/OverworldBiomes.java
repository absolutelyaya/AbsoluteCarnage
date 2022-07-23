package yaya.absolutecarnage.biomes;

import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.UndergroundPlacedFeatures;
import yaya.absolutecarnage.registries.EntityRegistry;
import yaya.absolutecarnage.registries.PlacedFeatureRegistry;

import javax.annotation.Nullable;

public class OverworldBiomes
{
	
	protected static int calculateSkyColor(float color)
	{
		float $$1 = color / 3.0F;
		$$1 = MathHelper.clamp($$1, -1.0F, 1.0F);
		return MathHelper.hsvToRgb(0.62222224F - $$1 * 0.05F, 0.5F + $$1 * 0.1F, 1.0F);
	}
	
	private static Biome biome(Biome.Precipitation precipitation, float temperature, float downfall, SpawnSettings.Builder spawnBuilder, GenerationSettings.Builder biomeBuilder, @Nullable MusicSound music)
	{
		return biome(precipitation, temperature, downfall, 4159204, 329011, spawnBuilder, biomeBuilder, music);
	}
	
	private static Biome biome(Biome.Precipitation precipitation, float temperature, float downfall, int waterColor, int waterFogColor, SpawnSettings.Builder spawnBuilder, GenerationSettings.Builder biomeBuilder, @Nullable MusicSound music)
	{
		return (new Biome.Builder()).precipitation(precipitation).temperature(temperature).downfall(downfall).effects((new BiomeEffects.Builder()).waterColor(waterColor).waterFogColor(waterFogColor).fogColor(12638463).skyColor(calculateSkyColor(temperature)).moodSound(BiomeMoodSound.CAVE).music(music).build()).spawnSettings(spawnBuilder.build()).generationSettings(biomeBuilder.build()).build();
	}
	
	public static Biome infestedCavern()
	{
		SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();
		spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityRegistry.SWARM_CLUSTER,
				5, 2, 6));
		
		GenerationSettings.Builder biomeBuilder = new GenerationSettings.Builder();
		//default Desert
		DefaultBiomeFeatures.addDefaultOres(biomeBuilder);
		DefaultBiomeFeatures.addDefaultDisks(biomeBuilder);
		DefaultBiomeFeatures.addDefaultFlowers(biomeBuilder);
		DefaultBiomeFeatures.addDefaultGrass(biomeBuilder);
		DefaultBiomeFeatures.addDesertDeadBushes(biomeBuilder);
		DefaultBiomeFeatures.addDefaultMushrooms(biomeBuilder);
		DefaultBiomeFeatures.addDesertVegetation(biomeBuilder);
		DefaultBiomeFeatures.addDesertFeatures(biomeBuilder);
		//Custom
		biomeBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, PlacedFeatureRegistry.INFESTED_CAVERN_SAND);
		biomeBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, PlacedFeatureRegistry.CEILING_WEBS);
		
		return biome(Biome.Precipitation.NONE, 2f, 0f, 0xc69d6f, 0xbf8752, spawnBuilder, biomeBuilder, null);
	}
}
