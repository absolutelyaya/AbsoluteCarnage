package yaya.absolutecarnage.registries;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.items.AnimatedBlockItem;
import yaya.absolutecarnage.items.FlameThrower;
import yaya.absolutecarnage.items.JungleSeeds;
import yaya.absolutecarnage.items.trinkets.WingTrinketItem;

public class ItemRegistry
{
	public static final Item JUNGLE_SEEDS = registerItem("jungle_seeds",
			new JungleSeeds(new FabricItemSettings().group(ItemGroupRegistry.CARNAGE).rarity(Rarity.RARE)));
	public static final Item FLAME_THROWER = registerItem("flame_thrower",
			new FlameThrower(new FabricItemSettings().group(ItemGroupRegistry.CARNAGE).maxCount(1)));
	
	public static final Item CUTICLE_WINGS = registerItem("cuticle_wings",
			new WingTrinketItem(new FabricItemSettings().group(ItemGroupRegistry.CARNAGE_TRINKETS).maxCount(1), 0.25f));
	
	public static final Item INFESTED_CHEST = registerItem("infested_chest",
			new AnimatedBlockItem(BlockRegistry.INFESTED_CHEST, new FabricItemSettings().group(ItemGroupRegistry.CARNAGE)));
	
	public static Item registerItem(String name, Item item)
	{
		return Registry.register(Registry.ITEM, new Identifier(AbsoluteCarnage.MOD_ID, name), item);
	}
	
	public static void registerItems()
	{
	
	}
}
