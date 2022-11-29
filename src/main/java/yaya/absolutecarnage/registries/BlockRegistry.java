package yaya.absolutecarnage.registries;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.blocks.*;
import yaya.absolutecarnage.entities.blocks.InfestedChestBlockEntity;
import yaya.absolutecarnage.entities.blocks.OrnateSandstoneChestBlockEntity;

public class BlockRegistry
{
	public static final Block DUNE = register("dune", new FallingBlock(FabricBlockSettings.of(Material.SOIL)
					.sounds(BlockSoundGroup.SAND).strength(0.5f, 0.8f)), ItemGroupRegistry.CARNAGE);
	public static final Block HANGING_WEB = register("hanging_web", new HangingCobwebBlock(FabricBlockSettings.of(Material.COBWEB)
					.sounds(BlockSoundGroup.STONE).strength(0.5f, 0.8f).noCollision().nonOpaque()),
			ItemGroupRegistry.CARNAGE, 100, 100);
	public static final Block HARDENED_SANDSTONE = register("hardened_sandstone", new Block(FabricBlockSettings.of(Material.STONE)
					.sounds(BlockSoundGroup.DRIPSTONE_BLOCK).strength(1.5f, 3).requiresTool()), ItemGroupRegistry.CARNAGE);
	public static final Block SWARM_CLUSTER = register("swarm_cluster_block", new MudBlock(FabricBlockSettings.of(Material.SOIL)
					.sounds(BlockSoundGroup.MUD).strength(1f, 1f).velocityMultiplier(0.8f).luminance(2)),
			ItemGroupRegistry.CARNAGE);
	public static final Block NEST_BLOCK = register("nest_block", new NestBlock(FabricBlockSettings.of(Material.STONE)
					.sounds(BlockSoundGroup.DEEPSLATE).strength(3f, 6f).requiresTool()), ItemGroupRegistry.CARNAGE);
	public static final Block FLOOR_WEB_DECAL = register("floor_web_decal", new FloorDecal(FabricBlockSettings.of(Material.REPLACEABLE_PLANT)
					.strength(0.5f, 0f).sounds(BlockSoundGroup.WOOL).noCollision().nonOpaque().jumpVelocityMultiplier(0.8f)
					.drops(new Identifier(AbsoluteCarnage.MOD_ID, "blocks/web_decal"))),
			null, 100, 100);
	public static final Block WALL_WEB_DECAL = register("wall_web_decal", new WallDecal(FabricBlockSettings.copyOf(FLOOR_WEB_DECAL).dropsLike(FLOOR_WEB_DECAL)),
			null, 100, 100);
	public static final Block DANGLING_EGG = register("dangling_egg", new DanglingEggBlock(FabricBlockSettings.of(Material.COBWEB)
					.luminance(DanglingEggBlock.getLuminanceSupplier(10)).nonOpaque().noCollision().sounds(BlockSoundGroup.MUD)),
			null, 100, 100);
	public static final Block QUICKSAND = register("quicksand", new QuicksandBlock(FabricBlockSettings.of(Material.POWDER_SNOW)
					.sounds(BlockSoundGroup.SAND).strength(0.5f, 0.8f).velocityMultiplier(0.4F).noCollision()
					.drops(new Identifier(AbsoluteCarnage.MOD_ID, "blocks/quicksand"))), null);
	
	public static final Block INFESTED_CHEST = register("infested_chest", new CarnageChestBlock(FabricBlockSettings.of(Material.WOOD)
					.nonOpaque(), () -> BlockEntityRegistry.INFESTED_CHEST, InfestedChestBlockEntity.class, false, true), null);
	public static final Block ORNATE_SANDSTONE_CHEST = register("ornate_sandstone_chest", new CarnageChestBlock(FabricBlockSettings.of(Material.STONE)
					.nonOpaque(), () -> BlockEntityRegistry.ORNATE_SANDSTONE_CHEST, OrnateSandstoneChestBlockEntity.class, true, false), null);
	
	@SuppressWarnings("SameParameterValue")
	private static Block register(String name, Block block, ItemGroup group, int burn, int spread)
	{
		FlammableBlockRegistry.getDefaultInstance().add(block, burn, spread);
		if(group != null)
			registerItem(name, block, group);
		return Registry.register(Registry.BLOCK, new Identifier(AbsoluteCarnage.MOD_ID, name), block);
	}
	
	private static Block register(String name, Block block, ItemGroup group)
	{
		if(group != null)
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
