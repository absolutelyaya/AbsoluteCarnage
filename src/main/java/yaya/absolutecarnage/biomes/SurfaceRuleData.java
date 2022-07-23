package yaya.absolutecarnage.biomes;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import yaya.absolutecarnage.registries.BiomeRegistry;

public class SurfaceRuleData
{
	private static final MaterialRules.MaterialRule SAND = makeStateRule(Blocks.SAND);
	private static final MaterialRules.MaterialRule PACKED_MUD = makeStateRule(Blocks.PACKED_MUD);
	private static final MaterialRules.MaterialRule SANDSTONE = makeStateRule(Blocks.SANDSTONE);
	private static final MaterialRules.MaterialRule DEEPSLATE = makeStateRule(Blocks.RED_SANDSTONE);
	private static final MaterialRules.MaterialRule BEDROCK = makeStateRule(Blocks.BEDROCK);
	
	private static MaterialRules.MaterialCondition surfaceNoiseThreshold(double min) {
		return MaterialRules.noiseThreshold(NoiseParametersKeys.SURFACE, min / 8.25D, 1.7976931348623157E308D);
	}
	
	public static MaterialRules.MaterialRule makeRules()
	{
		MaterialRules.MaterialCondition noise = MaterialRules.noiseThreshold(NoiseParametersKeys.SURFACE, -0.1818D, 0.1818D);
		MaterialRules.MaterialRule sandySurface = MaterialRules.sequence(MaterialRules.condition(MaterialRules.STONE_DEPTH_CEILING, SANDSTONE), SAND);
		return MaterialRules.sequence(
				MaterialRules.condition(MaterialRules.verticalGradient("bedrock_floor", YOffset.getBottom(), YOffset.aboveBottom(5)), BEDROCK),
				MaterialRules.condition(MaterialRules.verticalGradient("deepslate", YOffset.fixed(0), YOffset.fixed(8)), DEEPSLATE),
				MaterialRules.condition(MaterialRules.biome(BiomeRegistry.INFESTED_CAVERN),
						MaterialRules.condition(surfaceNoiseThreshold(1.0D), MaterialRules.condition(noise, SANDSTONE))),
				MaterialRules.condition(MaterialRules.biome(BiomeRegistry.INFESTED_CAVERN),
						MaterialRules.condition(surfaceNoiseThreshold(1.0D), PACKED_MUD)),
				
				sandySurface
		);
	}
	
	private static MaterialRules.MaterialRule makeStateRule(Block block)
	{
		return MaterialRules.block(block.getDefaultState());
	}
}
