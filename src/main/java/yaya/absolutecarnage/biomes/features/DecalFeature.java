package yaya.absolutecarnage.biomes.features;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class DecalFeature extends Feature<DecalFeatureConfig>
{
	public DecalFeature(Codec<DecalFeatureConfig> codec)
	{
		super(codec);
	}
	
	@Override
	public boolean generate(FeatureContext context)
	{
		DecalFeatureConfig config = (DecalFeatureConfig) context.getConfig();
		StructureWorldAccess world = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		BlockState floor = config.floor().getBlockState(context.getRandom(), blockPos);
		BlockState wall = config.wall().getBlockState(context.getRandom(), blockPos);
		if (!floor.canPlaceAt(world, blockPos))
			return false;
		for (Direction dir : Direction.Type.HORIZONTAL)
		{
			BlockPos pos = blockPos.offset(dir);
			if(world.getBlockState(pos).isSideSolidFullSquare(world, pos, dir.getOpposite()) && context.getRandom().nextInt(2) == 0)
			{
				world.setBlockState(blockPos, wall.with(DeadCoralWallFanBlock.FACING, dir.getOpposite()), 2);
				return true;
			}
		}
		world.setBlockState(blockPos, floor, 2);
		return true;
	}
}
