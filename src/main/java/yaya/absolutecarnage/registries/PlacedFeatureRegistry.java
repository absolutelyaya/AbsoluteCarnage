package yaya.absolutecarnage.registries;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.*;
import yaya.absolutecarnage.AbsoluteCarnage;

import java.util.List;

public class PlacedFeatureRegistry
{
	public static final RegistryEntry<PlacedFeature> INFESTED_CAVERN_SAND;
	public static final RegistryEntry<PlacedFeature> WEB_COLUMN;
	public static final RegistryEntry<PlacedFeature> HANGING_WEBS;
	public static final RegistryEntry<PlacedFeature> SWARM_CLUSTER_PATCH;
	public static final RegistryEntry<PlacedFeature> WEB_DECAL;
	public static final RegistryEntry<PlacedFeature> NEST_HOLES;
	
	public static RegistryEntry<PlacedFeature> register(String id, RegistryEntry<? extends ConfiguredFeature<?, ?>> registryEntry, PlacementModifier... modifiers) {
		return BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, new Identifier(AbsoluteCarnage.MOD_ID, id), new PlacedFeature(RegistryEntry.upcast(registryEntry), List.of(modifiers)));
	}
	
	public static void registerPlacedFeatures()
	{
	
	}
	
	static
	{
		INFESTED_CAVERN_SAND = register("infested_cavern_sand", ConfiguredFeatureRegistry.INFESTED_CAVERN_SAND,
				CountPlacementModifier.of(62), SquarePlacementModifier.of(),
				HeightRangePlacementModifier.uniform(YOffset.fixed(8), YOffset.fixed(256)),
				EnvironmentScanPlacementModifier.of(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.IS_AIR, 12),
				RandomOffsetPlacementModifier.vertically(ConstantIntProvider.create(1)), BiomePlacementModifier.of());
		WEB_COLUMN = register("web_column", ConfiguredFeatureRegistry.WEB_COLUMN,
				CountPlacementModifier.of(256), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_120_RANGE,
				EnvironmentScanPlacementModifier.of(Direction.UP, BlockPredicate.matchingBlockTag(BlockTagRegistry.INFESTED_CAVERN_REPLACEABLE), BlockPredicate.IS_AIR, 24),
				RandomOffsetPlacementModifier.vertically(ConstantIntProvider.create(-1)), BiomePlacementModifier.of());
		HANGING_WEBS = register("hanging_webs", ConfiguredFeatureRegistry.HANGING_WEBS,
				CountPlacementModifier.of(256), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_120_RANGE,
				EnvironmentScanPlacementModifier.of(Direction.UP, BlockPredicate.matchingBlockTag(BlockTagRegistry.INFESTED_CAVERN_REPLACEABLE), BlockPredicate.IS_AIR, 24),
				RandomOffsetPlacementModifier.vertically(ConstantIntProvider.create(-1)), BiomePlacementModifier.of());
		SWARM_CLUSTER_PATCH = register("swarm_cluster_patch", ConfiguredFeatureRegistry.SWARM_CLUSTER_PATCH,
				CountPlacementModifier.of(3), SquarePlacementModifier.of(),
				HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(8)),
				EnvironmentScanPlacementModifier.of(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.IS_AIR, 12),
				RandomOffsetPlacementModifier.vertically(ConstantIntProvider.create(1)), BiomePlacementModifier.of());
		WEB_DECAL = register("web_decal", ConfiguredFeatureRegistry.WEB_DECAL,
				CountPlacementModifier.of(256), CountPlacementModifier.of(5), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_120_RANGE,
				EnvironmentScanPlacementModifier.of(Direction.DOWN, BlockPredicate.matchingBlockTag(BlockTagRegistry.INFESTED_CAVERN_REPLACEABLE), BlockPredicate.IS_AIR, 12),
				RandomOffsetPlacementModifier.vertically(ConstantIntProvider.create(1)), BiomePlacementModifier.of());
		NEST_HOLES = register("nest_holes", ConfiguredFeatureRegistry.NEST_HOLES,
				CountPlacementModifier.of(256), CountPlacementModifier.of(3), SquarePlacementModifier.of(),
				HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(8)),
				BlockFilterPlacementModifier.of(BlockPredicate.matchingBlocks(BlockRegistry.NEST_BLOCK)),
				BiomePlacementModifier.of());
	}
}
