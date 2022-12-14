package yaya.absolutecarnage.registries;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.*;
import yaya.absolutecarnage.biomes.features.*;

public class FeatureRegistry
{
	public static final Feature<VegetationPatchFeatureConfig> CAVERN_SAND;
	public static final Feature<DecalFeatureConfig> DECAL;
	public static final Feature<SimpleBlockFeatureConfig> SKULLS;
	public static final Feature<SwarmHillFeatureConfig> SWARM_HILL;
	public static final Feature<RockFeatureConfig> UNIVERSAL_ROCK;
	
	private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
		return Registry.register(Registry.FEATURE, name, feature);
	}
	
	static
	{
		CAVERN_SAND = register("cavern_sand", new SandBoxFeature(VegetationPatchFeatureConfig.CODEC));
		DECAL = register("decal", new DecalFeature(DecalFeatureConfig.CODEC));
		SKULLS = register("skulls", new SkullFeature(SimpleBlockFeatureConfig.CODEC));
		SWARM_HILL = register("swarm_hill", new SwarmHillFeature(SwarmHillFeatureConfig.CODEC));
		UNIVERSAL_ROCK = register("universal_rock", new UniversalRockFeature(RockFeatureConfig.CODEC));
	}
}
