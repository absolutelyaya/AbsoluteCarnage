package yaya.absolutecarnage.biomes.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;

public record SwarmHillFeatureConfig(BlockState floor, BlockState wall, IntProvider radius, IntProvider height,
									 IntProvider floorRadius) implements FeatureConfig
{
	public static final Codec<SwarmHillFeatureConfig> CODEC = RecordCodecBuilder.create(
			(instance) -> instance.group(BlockState.CODEC.fieldOf("floor").forGetter(config -> config.floor),
							BlockState.CODEC.fieldOf("wall").forGetter((config -> config.wall)),
							IntProvider.VALUE_CODEC.fieldOf("radius").forGetter((config -> config.radius)),
							IntProvider.VALUE_CODEC.fieldOf("height").forGetter((config -> config.height)),
							IntProvider.VALUE_CODEC.fieldOf("floorRadius").forGetter((config -> config.height)))
					.apply(instance, SwarmHillFeatureConfig::new));
	
	public BlockState floor()
	{
		return this.floor;
	}
	
	public BlockState wall()
	{
		return wall;
	}
	
	public IntProvider radius()
	{
		return radius;
	}
	
	public IntProvider height()
	{
		return height;
	}
	
	public IntProvider floorRadius()
	{
		return floorRadius;
	}
}
