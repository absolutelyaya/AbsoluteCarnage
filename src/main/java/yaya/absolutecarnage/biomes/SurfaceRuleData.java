package yaya.absolutecarnage.biomes;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.registries.BlockRegistry;

public class SurfaceRuleData
{
	private static final MaterialRules.MaterialRule PACKED_MUD = makeStateRule(Blocks.PACKED_MUD);
	private static final MaterialRules.MaterialRule SANDSTONE = makeStateRule(Blocks.SANDSTONE);
	private static final MaterialRules.MaterialRule NEST = makeStateRule(BlockRegistry.NEST_BLOCK);
	private static final MaterialRules.MaterialRule BEDROCK = makeStateRule(Blocks.BEDROCK);
	
	private static final RegistryKey<Biome> INFESTED_CAVERN =
			RegistryKey.of(Registry.BIOME_KEY, new Identifier(AbsoluteCarnage.MOD_ID, "infested_cavern"));
	
	public static MaterialRules.MaterialRule makeRules()
	{
		MaterialRules.MaterialCondition noise = MaterialRules.noiseThreshold(NoiseParametersKeys.SURFACE, -0.1818D, 0.1818D);
		return MaterialRules.sequence(
				MaterialRules.condition(MaterialRules.verticalGradient("bedrock_floor", YOffset.getBottom(), YOffset.aboveBottom(5)), BEDROCK),
				MaterialRules.condition(MaterialRules.verticalGradient("deepslate", YOffset.fixed(0), YOffset.fixed(8)), NEST),
				MaterialRules.condition(MaterialRules.biome(INFESTED_CAVERN), MaterialRules.condition(noise, SANDSTONE)),
				MaterialRules.condition(MaterialRules.biome(INFESTED_CAVERN), PACKED_MUD)
		);
	}
	
	private static MaterialRules.MaterialRule makeStateRule(Block block)
	{
		return MaterialRules.block(block.getDefaultState());
	}
}
