package yaya.absolutecarnage.registries;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.*;

public class PlacedFeatureRegistry
{
	public static final RegistryEntry<PlacedFeature> INFESTED_CAVERN_SAND;
	public static final RegistryEntry<PlacedFeature> CEILING_WEBS;
	
	static
	{
		INFESTED_CAVERN_SAND = PlacedFeatures.register("infested_cavern_sand", ConfiguredFeatureRegistry.INFESTED_CAVERN_SAND,
				CountPlacementModifier.of(62), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_120_RANGE,
				EnvironmentScanPlacementModifier.of(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.IS_AIR, 12),
				RandomOffsetPlacementModifier.vertically(ConstantIntProvider.create(1)), BiomePlacementModifier.of());
		CEILING_WEBS = PlacedFeatures.register("ceiling_webs", ConfiguredFeatureRegistry.CEILING_WEBS,
				CountPlacementModifier.of(256), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_120_RANGE,
				EnvironmentScanPlacementModifier.of(Direction.UP, BlockPredicate.matchingBlockTag(BlockTagRegistry.SANDSTONE), BlockPredicate.IS_AIR, 24),
				RandomOffsetPlacementModifier.vertically(ConstantIntProvider.create(-1)), BiomePlacementModifier.of());
	}
}
