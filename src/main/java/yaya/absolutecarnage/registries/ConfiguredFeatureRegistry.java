package yaya.absolutecarnage.registries;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.intprovider.WeightedListIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

import java.util.List;

public class ConfiguredFeatureRegistry
{
	public static final RegistryEntry<ConfiguredFeature<VegetationPatchFeatureConfig, ?>> INFESTED_CAVERN_SAND;
	public static final RegistryEntry<ConfiguredFeature<SimpleBlockFeatureConfig, ?>> DEADBUSH;
	public static final RegistryEntry<ConfiguredFeature<SimpleBlockFeatureConfig, ?>> WEB;
	public static final RegistryEntry<ConfiguredFeature<BlockColumnFeatureConfig, ?>> CEILING_WEBS;
	
	private static BlockStateProvider createDeadbushFeature()
	{
		return new SimpleBlockFeatureConfig(new WeightedBlockStateProvider(DataPool.<BlockState>builder()
								.add(Blocks.DEAD_BUSH.getDefaultState(), 1).build())).toPlace();
	}
	
	private static BlockStateProvider createWebFeature()
	{
		return new SimpleBlockFeatureConfig(new WeightedBlockStateProvider(DataPool.<BlockState>builder()
				.add(Blocks.COBWEB.getDefaultState(), 1).build())).toPlace();
	}
	
	static
	{
		DEADBUSH = net.minecraft.world.gen.feature.ConfiguredFeatures.register("deadbush", Feature.SIMPLE_BLOCK,
				new SimpleBlockFeatureConfig(createDeadbushFeature()));
		INFESTED_CAVERN_SAND = net.minecraft.world.gen.feature.ConfiguredFeatures.register("infested_cavern_sand",
				FeatureRegistry.CAVERN_SAND, new VegetationPatchFeatureConfig(BlockTagRegistry.SANDSTONE,
						BlockStateProvider.of(Blocks.DRIPSTONE_BLOCK), PlacedFeatures.createEntry(DEADBUSH),
						VerticalSurfaceType.FLOOR, ConstantIntProvider.create(3), 0.8F, 5,
						0.1F, UniformIntProvider.create(4, 7), 0.7F));
		WEB = net.minecraft.world.gen.feature.ConfiguredFeatures.register("web", Feature.SIMPLE_BLOCK,
				new SimpleBlockFeatureConfig(createWebFeature()));
		CEILING_WEBS = ConfiguredFeatures.register("ceiling_webs", Feature.BLOCK_COLUMN,
				new BlockColumnFeatureConfig(List.of(BlockColumnFeatureConfig.createLayer(
						new WeightedListIntProvider(DataPool.<IntProvider>builder()
								.add(UniformIntProvider.create(3, 7), 2).add(UniformIntProvider.create(1, 4), 3)
								.add(UniformIntProvider.create(1, 1), 10).build()), createWebFeature())), Direction.DOWN,
						BlockPredicate.IS_AIR, true));
	}
}
