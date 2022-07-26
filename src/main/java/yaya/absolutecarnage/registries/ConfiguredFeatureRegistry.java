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
import yaya.absolutecarnage.biomes.features.DecalFeatureConfig;
import yaya.absolutecarnage.blocks.DanglingEggBlock;
import yaya.absolutecarnage.blocks.NestBlock;

import java.util.List;

public class ConfiguredFeatureRegistry
{
	public static final RegistryEntry<ConfiguredFeature<VegetationPatchFeatureConfig, ?>> INFESTED_CAVERN_SAND;
	public static final RegistryEntry<ConfiguredFeature<SimpleBlockFeatureConfig, ?>> DEADBUSH;
	public static final RegistryEntry<ConfiguredFeature<BlockColumnFeatureConfig, ?>> WEB_COLUMN;
	public static final RegistryEntry<ConfiguredFeature<BlockColumnFeatureConfig, ?>> HANGING_WEBS;
	public static final RegistryEntry<ConfiguredFeature<BlockColumnFeatureConfig, ?>> DANGLING_EGG;
	public static final RegistryEntry<ConfiguredFeature<DecalFeatureConfig, ?>> WEB_DECAL;
	public static final RegistryEntry<ConfiguredFeature<VegetationPatchFeatureConfig, ?>> SWARM_CLUSTER_PATCH;
	public static final RegistryEntry<ConfiguredFeature<SimpleBlockFeatureConfig, ?>> NEST_HOLES;
	public static final RegistryEntry<ConfiguredFeature<SimpleBlockFeatureConfig, ?>> SKULLS;
	
	private static BlockStateProvider deadbushFeature()
	{
		return new SimpleBlockFeatureConfig(new WeightedBlockStateProvider(DataPool.<BlockState>builder()
								.add(Blocks.DEAD_BUSH.getDefaultState(), 1).build())).toPlace();
	}
	
	private static BlockStateProvider floorWebDecalFeature()
	{
		return new SimpleBlockFeatureConfig(new WeightedBlockStateProvider(DataPool.<BlockState>builder()
				.add(BlockRegistry.FLOOR_WEB_DECAL.getDefaultState(), 2).build())).toPlace();
	}
	
	private static BlockStateProvider wallWebDecalFeature()
	{
		return new SimpleBlockFeatureConfig(new WeightedBlockStateProvider(DataPool.<BlockState>builder()
				.add(BlockRegistry.WALL_WEB_DECAL.getDefaultState(), 2).build())).toPlace();
	}
	
	private static BlockStateProvider ceilingWebFeature(boolean hanging)
	{
		return new SimpleBlockFeatureConfig(new WeightedBlockStateProvider(DataPool.<BlockState>builder()
				.add((hanging ? BlockRegistry.HANGING_WEB : Blocks.COBWEB).getDefaultState(), 1).build())).toPlace();
	}
	private static BlockStateProvider danglingEggFeature(int part)
	{
		return new SimpleBlockFeatureConfig(new WeightedBlockStateProvider(DataPool.<BlockState>builder()
				.add(BlockRegistry.DANGLING_EGG.getDefaultState().with(DanglingEggBlock.PART, part), 1).build())).toPlace();
	}
	
	static
	{
		DEADBUSH = ConfiguredFeatures.register("deadbush", Feature.SIMPLE_BLOCK,
				new SimpleBlockFeatureConfig(deadbushFeature()));
		INFESTED_CAVERN_SAND = ConfiguredFeatures.register("infested_cavern_sand",
				FeatureRegistry.CAVERN_SAND, new VegetationPatchFeatureConfig(BlockTagRegistry.INFESTED_CAVERN_REPLACEABLE,
						BlockStateProvider.of(BlockRegistry.HARDENED_SANDSTONE), PlacedFeatures.createEntry(DEADBUSH),
						VerticalSurfaceType.FLOOR, ConstantIntProvider.create(3), 0.8F, 5,
						0.1F, UniformIntProvider.create(4, 7), 0.7F));
		WEB_COLUMN = ConfiguredFeatures.register("web_column", Feature.BLOCK_COLUMN,
				new BlockColumnFeatureConfig(List.of(BlockColumnFeatureConfig.createLayer(
						new WeightedListIntProvider(DataPool.<IntProvider>builder()
								.add(UniformIntProvider.create(3, 7), 2).add(UniformIntProvider.create(1, 4), 3)
								.add(UniformIntProvider.create(1, 1), 10).build()), ceilingWebFeature(false))), Direction.DOWN,
						BlockPredicate.IS_AIR, true));
		HANGING_WEBS = ConfiguredFeatures.register("hanging_webs", Feature.BLOCK_COLUMN,
				new BlockColumnFeatureConfig(List.of(BlockColumnFeatureConfig.createLayer(
						new WeightedListIntProvider(DataPool.<IntProvider>builder()
								.add(UniformIntProvider.create(1, 1), 1).build()), ceilingWebFeature(true))), Direction.DOWN,
						BlockPredicate.IS_AIR, true));
		DANGLING_EGG = ConfiguredFeatures.register("dangling_egg", Feature.BLOCK_COLUMN,
				new BlockColumnFeatureConfig(List.of(
						BlockColumnFeatureConfig.createLayer(new WeightedListIntProvider(DataPool.<IntProvider>builder()
								.add(UniformIntProvider.create(1, 1), 1).build()), danglingEggFeature(1)),
						BlockColumnFeatureConfig.createLayer(new WeightedListIntProvider(DataPool.<IntProvider>builder()
								.add(UniformIntProvider.create(1, 7), 1).build()), danglingEggFeature(2)),
						BlockColumnFeatureConfig.createLayer(new WeightedListIntProvider(DataPool.<IntProvider>builder()
								.add(UniformIntProvider.create(1, 1), 1).build()), danglingEggFeature(3))
						), Direction.DOWN, BlockPredicate.IS_AIR, true));
		WEB_DECAL = ConfiguredFeatures.register("web_decal", FeatureRegistry.DECAL,
				new DecalFeatureConfig(floorWebDecalFeature(), wallWebDecalFeature()));
		SWARM_CLUSTER_PATCH = ConfiguredFeatures.register("swarm_cluster_patch", Feature.VEGETATION_PATCH,
				new VegetationPatchFeatureConfig(BlockTagRegistry.INFESTED_CAVERN_REPLACEABLE,
						BlockStateProvider.of(BlockRegistry.SWARM_CLUSTER), PlacedFeatures.createEntry(WEB_DECAL),
						VerticalSurfaceType.FLOOR, ConstantIntProvider.create(3), 0.8F, 5,
						0.1F, UniformIntProvider.create(2, 5), 0.7F));
		NEST_HOLES = ConfiguredFeatures.register("nest_holes", Feature.SIMPLE_BLOCK,
				new SimpleBlockFeatureConfig(BlockStateProvider.of(BlockRegistry.NEST_BLOCK.getDefaultState().with(NestBlock.HOLES, true))));
		SKULLS = ConfiguredFeatures.register("skulls", FeatureRegistry.SKULLS,
				new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SKELETON_SKULL.getDefaultState())));
	}
}
