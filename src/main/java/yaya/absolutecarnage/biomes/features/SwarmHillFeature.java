package yaya.absolutecarnage.biomes.features;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.HashMap;
import java.util.Map;

public class SwarmHillFeature extends Feature<SwarmHillFeatureConfig>
{
	public SwarmHillFeature(Codec<SwarmHillFeatureConfig> configCodec)
	{
		super(configCodec);
	}
	
	@Override
	public boolean generate(FeatureContext context)
	{
		Random random = context.getRandom();
		BlockPos origin = context.getOrigin()/*.add(8, 0, 8)*/;
		SwarmHillFeatureConfig config = (SwarmHillFeatureConfig)context.getConfig();
		int radius = config.radius().get(random);
		int maxHeight = config.height().getMax();
		int minHeight = config.height().getMin();
		int floorRadius = config.floorRadius().get(random);
		StructureWorldAccess world = context.getWorld();
		BlockState wall = config.wall();
		BlockState floor = config.floor();
		int bottom = world.getBottomY() + 20;
		Map<BlockPos, Byte> blocks = new HashMap<>();
		
		for (int degrees = 0; degrees < 360; degrees += 45)
		{
			double rads = Math.toRadians(degrees + random.nextInt(30) - 15);
			double xDir = Math.sin(rads);
			double zDir = Math.cos(rads);
			
			BlockPos pos = origin.add(new BlockPos(xDir * (random.nextInt(radius) + floorRadius), 0, zDir * (random.nextInt(radius) + floorRadius)));
			int patchRadius = random.nextInt(floorRadius - floorRadius / 2) + floorRadius / 2;
			for(int x = -patchRadius; x < patchRadius; x++)
			{
				for(int y = -patchRadius; y < patchRadius; y++)
				{
					BlockPos pos2 = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos.add(x, 0, y)).down();
					if(random.nextDouble() > pos2.getSquaredDistance(pos) / (patchRadius * patchRadius))
					{
						if(!world.getBlockState(pos2.down()).isSolidBlock(world, pos2.down()))
							world.setBlockState(pos2.down(), wall, 2);
						world.setBlockState(pos2, floor, 2);
					}
				}
			}
		}
		
		for (int degrees = 0; degrees < 360; degrees += 9)
		{
			double rads = Math.toRadians(degrees);
			double x = Math.sin(rads);
			double z = Math.cos(rads);
			
			for (int dist = 0; dist < radius + 1; dist++)
			{
				BlockPos pos = new BlockPos(Math.round(origin.getX() + x * dist), origin.getY() - 1, Math.round(origin.getZ() + z * dist));
				for (int i = bottom; i < origin.getY() + 10; i++)
				{
					BlockPos pos2 = new BlockPos(pos.getX(), i, pos.getZ());
					if(!blocks.containsKey(pos2) && !world.getBlockState(pos2).isAir())
					{
						world.setBlockState(pos2, Blocks.AIR.getDefaultState(), 2);
						blocks.put(pos2, (byte)0);
					}
				}
			}
			
			for (int dist = 0; dist < 2; dist++)
			{
				BlockPos pos = new BlockPos(Math.round(origin.getX() + x * (radius - 1 + dist)), origin.getY() - 1,
						Math.round(origin.getZ() + z * (radius - 1 + dist)));
				if(!world.getBlockState(pos).isOf(wall.getBlock()))
				{
					int min = dist == 0 ? minHeight : 0;
					int goalHeight = (random.nextInt(maxHeight - min) + min) / (dist + 1);
					for (int i = bottom; i < origin.getY() + maxHeight; i++)
					{
						BlockPos pos2 = new BlockPos(pos.getX(), i, pos.getZ());
						if(i > origin.getY() + goalHeight)
							blocks.put(pos2, (byte)2);
						else
						{
							if((blocks.getOrDefault(pos2, (byte)2) == 0 && world.getBlockState(pos2).isAir()) || i >= origin.getY() - 10)
							{
								double noise = 1 - (i - bottom) / (double)(origin.getY() + maxHeight - bottom);
								if((i < origin.getY() && dist == 0 && random.nextDouble() > noise) ||
										   (dist == 1 && random.nextDouble() > noise - 0.5) || i >= origin.getY())
								{
									world.setBlockState(pos2, wall, 2);
									blocks.put(pos2, (byte)1);
								}
							}
						}
					}
				}
			}
			
			for (int i = bottom; i < origin.getY() + 10; i++)
			{
				int counter = 0;
				for (int dist = 0; dist < radius + 1; dist++)
				{
					if(random.nextInt(11 + dist) > 10)
						counter--;
					BlockPos pos = new BlockPos(Math.round(origin.getX() + x * dist), i, Math.round(origin.getZ() + z * dist));
					if(random.nextDouble() > 0.7)
					{
						if(blocks.getOrDefault(pos, (byte)2) == 0)
							world.setBlockState(pos.add(0, 0, 0), Blocks.COARSE_DIRT.getDefaultState(), 2);
						blocks.put(pos, (byte)1);
					}
					else if(blocks.getOrDefault(pos, (byte)2) == 0)
						world.setBlockState(pos, Blocks.PACKED_MUD.getDefaultState(), 2);
					if(random.nextDouble() > 0.95 - dist * 0.25 && counter <= 0 && dist < radius - 2)
					{
						for (int offset = 0; offset < random.nextInt(5) + 3; offset++)
							world.setBlockState(pos.add(0, offset, 0), Blocks.AIR.getDefaultState(), 2);
						counter = random.nextInt(Math.max(64 - dist * 5, 10));
					}
				}
			}
		}
		world.setBlockState(origin.down(5), Blocks.COARSE_DIRT.getDefaultState(), 2);
		return true;
	}
}
