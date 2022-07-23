package yaya.absolutecarnage.registries;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import yaya.absolutecarnage.AbsoluteCarnage;

public class BlockRegistry
{
	public static final Block DUNE = register("dune", new FallingBlock(FabricBlockSettings.of(Material.SOIL)
					.sounds(BlockSoundGroup.SAND).strength(0.5f, 0.8f)), ItemGroupRegistry.CARNAGE);
	public static final Block HANGING_WEB = register("hanging_web", new CobwebBlock(FabricBlockSettings.of(Material.COBWEB)
					.sounds(BlockSoundGroup.STONE).strength(0.5f, 0.8f).noCollision().nonOpaque()),
			ItemGroupRegistry.CARNAGE, 100, 100);
	public static final Block HARDENED_SANDSTONE = register("hardened_sandstone", new Block(FabricBlockSettings.of(Material.STONE)
					.sounds(BlockSoundGroup.DRIPSTONE_BLOCK).strength(1.5f, 3).requiresTool()), ItemGroupRegistry.CARNAGE);
	public static final Block SWARM_CLUSTER = register("swarm_cluster_block", new MudBlock(FabricBlockSettings.of(Material.SOIL)
					.sounds(BlockSoundGroup.MUD).strength(1f, 1f).velocityMultiplier(0.8f).luminance(2)),
			ItemGroupRegistry.CARNAGE);
	
	private static Block register(String name, Block block, ItemGroup group, int burn, int spread)
	{
		FlammableBlockRegistry.getDefaultInstance().add(block, burn, spread);
		registerItem(name, block, group);
		return Registry.register(Registry.BLOCK, new Identifier(AbsoluteCarnage.MOD_ID, name), block);
	}
	
	private static Block register(String name, Block block, ItemGroup group)
	{
		registerItem(name, block, group);
		return Registry.register(Registry.BLOCK, new Identifier(AbsoluteCarnage.MOD_ID, name), block);
	}
	
	private static void registerItem(String name, Block block, ItemGroup group)
	{
		Registry.register(Registry.ITEM, new Identifier(AbsoluteCarnage.MOD_ID, name),
				new BlockItem(block, new FabricItemSettings().group(group)));
	}
	
	public static void registerBlocks()
	{
		FlammableBlockRegistry.getDefaultInstance().add(Blocks.COBWEB, 100, 100);
	}
}
