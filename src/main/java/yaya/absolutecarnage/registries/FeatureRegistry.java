package yaya.absolutecarnage.registries;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.VegetationPatchFeatureConfig;
import yaya.absolutecarnage.biomes.features.DecalFeatureConfig;
import yaya.absolutecarnage.biomes.features.SandBoxFeature;
import yaya.absolutecarnage.biomes.features.DecalFeature;

public class FeatureRegistry<FC extends FeatureConfig>
{
	public static final Feature<VegetationPatchFeatureConfig> CAVERN_SAND;
	public static final Feature<DecalFeatureConfig> DECAL;
	
	private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
		return Registry.register(Registry.FEATURE, name, feature);
	}
	
	static
	{
		CAVERN_SAND = register("cavern_sand", new SandBoxFeature(VegetationPatchFeatureConfig.CODEC));
		DECAL = register("decal", new DecalFeature(DecalFeatureConfig.CODEC));
	}
}
