package yaya.absolutecarnage.registries;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import yaya.absolutecarnage.AbsoluteCarnage;

public class BlockRegistry
{
	public static final Block DUNE = register("dune", new FallingBlock(FabricBlockSettings.of(Material.SOIL)
					.sounds(BlockSoundGroup.SAND).strength(0.5f, 0.8f)),
			ItemGroupRegistry.CARNAGE);
	
	private static Block register(String name, Block block, ItemGroup group)
	{
		registerItem(name, block, group);
		return Registry.register(Registry.BLOCK, new Identifier(AbsoluteCarnage.MOD_ID, name), block);
	}
	
	private static Item registerItem(String name, Block block, ItemGroup group)
	{
		return Registry.register(Registry.ITEM, new Identifier(AbsoluteCarnage.MOD_ID, name),
				new BlockItem(block, new FabricItemSettings().group(group)));
	}
	
	public static void registerBlocks()
	{
	
	}
}
