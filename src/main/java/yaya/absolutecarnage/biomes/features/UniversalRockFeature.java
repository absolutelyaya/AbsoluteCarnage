package yaya.absolutecarnage.biomes.features;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

//why the fuck is the surface block type hard coded in Forest Rock.
public class UniversalRockFeature extends Feature<RockFeatureConfig>
{
	public UniversalRockFeature(Codec<RockFeatureConfig> configCodec)
	{
		super(configCodec);
	}
	
	@Override
	public boolean generate(FeatureContext context)
	{
		BlockPos pos = context.getOrigin();
		StructureWorldAccess world = context.getWorld();
		Random random = context.getRandom();
		RockFeatureConfig config = (RockFeatureConfig)context.getConfig();
		
		int size = config.size().get(random);
		if(random.nextInt(config.bigChance().get(random)) == 0)
			size *= 1.5f + random.nextFloat();
		
		while(world.getBlockState(pos).isOf(Blocks.WATER))
			pos = pos.down();
		//blockPos.up();
		
		BlockPos offset = new BlockPos(0, 0, 0);
		for(int i = 0; i < size; ++i)
		{
			int x = random.nextInt(2);
			int y = random.nextInt(2);
			int z = random.nextInt(2);
			float dist = (float)(x + y + z) * (1f / size) + 0.5F;
			
			for (BlockPos pos2 : BlockPos.iterate(pos.add(-x, -y, -z), pos.add(x, y, z)))
			{
				pos2 = pos2.add(offset);
				//if (blockPos2.getSquaredDistance(pos) <= (double)(dist * dist))
				if(world.getBlockState(pos2.down()).isSolidBlock(world, pos2.down()) &&
						   pos2.getSquaredDistance(pos) < size * size * 0.5)
				{
					world.setBlockState(pos2, config.state(), 4);
					world.setBlockState(pos2.add(1, 0, 0), config.state(), 4);
					world.setBlockState(pos2.add(-1, 0, 0), config.state(), 4);
					world.setBlockState(pos2.add(0, 0, 1), config.state(), 4);
					world.setBlockState(pos2.add(0, 0, -1), config.state(), 4);
					world.setBlockState(pos2.add(0, 1, 0), config.state(), 4);
					world.setBlockState(pos2.add(0, -1, 0), config.state(), 4);
				}
			}
			
			offset = offset.add(-1 + random.nextInt(2), -2 + random.nextInt(3), -1 + random.nextInt(2));
		}
		return true;
	}
}
