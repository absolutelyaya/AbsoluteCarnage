package yaya.absolutecarnage.biomes.features;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.VegetationPatchFeature;
import net.minecraft.world.gen.feature.VegetationPatchFeatureConfig;
import yaya.absolutecarnage.registries.BlockRegistry;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

public class SandBoxFeature extends VegetationPatchFeature
{
	public SandBoxFeature(Codec<VegetationPatchFeatureConfig> codec)
	{
		super(codec);
	}
	
	protected Set<BlockPos> placeGroundAndGetPositions(StructureWorldAccess world, VegetationPatchFeatureConfig config, Random random, BlockPos pos, Predicate<BlockState> replaceable, int radiusX, int radiusZ) {
		Set<BlockPos> set = super.placeGroundAndGetPositions(world, config, random, pos, replaceable, radiusX, radiusZ);
		HashSet<BlockPos> set2 = new HashSet<>();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Iterator<BlockPos> BlockPositions = set.iterator();
		
		BlockPos blockPos;
		while(BlockPositions.hasNext()) {
			blockPos = BlockPositions.next();
			if (!isSolidBlockAroundPos(world, blockPos, mutable)) {
				set2.add(blockPos);
			}
		}
		
		BlockPositions = set2.iterator();
		
		while(BlockPositions.hasNext()) {
			blockPos = BlockPositions.next();
			world.setBlockState(blockPos, BlockRegistry.DUNE.getDefaultState(), 2);
		}
		
		return set2;
	}
	
	private static boolean isSolidBlockAroundPos(StructureWorldAccess world, BlockPos pos, BlockPos.Mutable mutablePos)
	{
		return isSolidBlockSide(world, pos, mutablePos, Direction.NORTH) ||
					   isSolidBlockSide(world, pos, mutablePos, Direction.EAST) ||
					   isSolidBlockSide(world, pos, mutablePos, Direction.SOUTH) ||
					   isSolidBlockSide(world, pos, mutablePos, Direction.WEST) ||
					   isSolidBlockSide(world, pos, mutablePos, Direction.DOWN);
	}
	
	private static boolean isSolidBlockSide(StructureWorldAccess world, BlockPos pos, BlockPos.Mutable mutablePos, Direction direction)
	{
		mutablePos.set(pos, direction);
		return !world.getBlockState(mutablePos).isSideSolidFullSquare(world, mutablePos, direction.getOpposite());
	}
}
