package yaya.absolutecarnage.biomes.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;

public record RockFeatureConfig(BlockState state, IntProvider bigChance, IntProvider size) implements FeatureConfig
{
	public static final Codec<RockFeatureConfig> CODEC = RecordCodecBuilder.create(
			(instance) -> instance.group(BlockState.CODEC.fieldOf("state").forGetter(config -> config.state),
							IntProvider.VALUE_CODEC.fieldOf("bigChance").forGetter((config -> config.bigChance)),
							IntProvider.VALUE_CODEC.fieldOf("size").forGetter((config -> config.size)))
					.apply(instance, RockFeatureConfig::new));
	
	@Override
	public BlockState state()
	{
		return state;
	}
	
	@Override
	public IntProvider bigChance()
	{
		return bigChance;
	}
	
	@Override
	public IntProvider size()
	{
		return size;
	}
}
