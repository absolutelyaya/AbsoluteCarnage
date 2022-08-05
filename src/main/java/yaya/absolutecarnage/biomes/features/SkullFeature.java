package yaya.absolutecarnage.biomes.features;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SkullFeature extends Feature<SimpleBlockFeatureConfig>
{
	public SkullFeature(Codec<SimpleBlockFeatureConfig> codec)
	{
		super(codec);
	}
	
	@Override
	public boolean generate(FeatureContext context)
	{
		SimpleBlockFeatureConfig config = (SimpleBlockFeatureConfig) context.getConfig();
		StructureWorldAccess world = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		BlockState block = config.toPlace().getBlockState(context.getRandom(), blockPos);
		if (!block.canPlaceAt(world, blockPos))
			return false;
		world.setBlockState(blockPos, block.with(SkullBlock.ROTATION, context.getRandom().nextInt(15)), 2);
		return true;
	}
}
