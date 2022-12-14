package yaya.absolutecarnage.registries;

import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import yaya.absolutecarnage.AbsoluteCarnage;

public class BlockTagRegistry
{
	public static TagKey<Block> INFESTED_CAVERN_REPLACEABLE = TagKey.of(Registry.BLOCK_KEY,
			new Identifier(AbsoluteCarnage.MOD_ID, "infested_cavern_replaceable"));
	public static TagKey<Block> SWARMLING_SPAWNABLE = TagKey.of(Registry.BLOCK_KEY,
			new Identifier(AbsoluteCarnage.MOD_ID, "swarmling_spawnable_on"));
}
