package yaya.absolutecarnage.biomes.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record DecalFeatureConfig(BlockStateProvider floor, BlockStateProvider wall) implements FeatureConfig
{
	public static final Codec<DecalFeatureConfig> CODEC = RecordCodecBuilder.create((instance) ->
					instance.group(BlockStateProvider.TYPE_CODEC.fieldOf("floor").forGetter(config -> config.floor),
					BlockStateProvider.TYPE_CODEC.fieldOf("wall").forGetter((config -> config.wall)))
					.apply(instance, DecalFeatureConfig::new));
	
	public DecalFeatureConfig(BlockStateProvider floor, BlockStateProvider wall)
	{
		this.floor = floor;
		this.wall = wall;
	}
	
	public BlockStateProvider floor()
	{
		return this.floor;
	}
	
	public BlockStateProvider wall()
	{
		return wall;
	}
}

