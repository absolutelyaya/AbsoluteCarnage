package yaya.absolutecarnage.biomes;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import yaya.absolutecarnage.registries.BiomeRegistry;
import yaya.absolutecarnage.registries.BlockRegistry;

public class SurfaceRuleData
{
	private static final MaterialRules.MaterialRule PACKED_MUD = makeStateRule(Blocks.PACKED_MUD);
	private static final MaterialRules.MaterialRule SANDSTONE = makeStateRule(Blocks.SANDSTONE);
	private static final MaterialRules.MaterialRule SAND = makeStateRule(Blocks.SAND);
	private static final MaterialRules.MaterialRule NEST = makeStateRule(BlockRegistry.NEST_BLOCK);
	private static final MaterialRules.MaterialRule BEDROCK = makeStateRule(Blocks.BEDROCK);
	
	private static final RegistryKey<Biome> CRAWLING_SANDS = BiomeRegistry.CRAWLING_SANDS;
	
	public static MaterialRules.MaterialRule makeRules()
	{
		MaterialRules.MaterialCondition noise = MaterialRules.noiseThreshold(NoiseParametersKeys.SURFACE, -0.1818D, 0.1818D);
		return MaterialRules.sequence(
				MaterialRules.condition(MaterialRules.verticalGradient("bedrock_floor", YOffset.getBottom(), YOffset.aboveBottom(5)), BEDROCK),
				MaterialRules.condition(MaterialRules.verticalGradient("deepslate", YOffset.fixed(0), YOffset.fixed(8)), NEST),
				MaterialRules.sequence(MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR_WITH_SURFACE_DEPTH,
						MaterialRules.sequence(MaterialRules.condition(MaterialRules.STONE_DEPTH_CEILING, SANDSTONE)))),
				MaterialRules.condition(MaterialRules.biome(CRAWLING_SANDS), MaterialRules.condition(MaterialRules.surface(),
						MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR_WITH_SURFACE_DEPTH, SAND))),
				MaterialRules.condition(MaterialRules.biome(CRAWLING_SANDS), MaterialRules.condition(noise, SANDSTONE)),
				MaterialRules.condition(MaterialRules.biome(CRAWLING_SANDS), PACKED_MUD)
		);
	}
	
	private static MaterialRules.MaterialRule makeStateRule(Block block)
	{
		return MaterialRules.block(block.getDefaultState());
	}
}
