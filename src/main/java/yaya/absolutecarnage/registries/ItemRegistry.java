package yaya.absolutecarnage.registries;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.items.JungleSeeds;

public class ItemRegistry
{
	public static final Item JUNGLE_SEEDS = registerItem("jungle_seeds",
			new JungleSeeds(new FabricItemSettings().group(ItemGroupRegistry.CARNAGE).rarity(Rarity.RARE)));
	
	public static Item registerItem(String name, Item item)
	{
		return Registry.register(Registry.ITEM, new Identifier(AbsoluteCarnage.MOD_ID, name), item);
	}
	
	public static void registerItems()
	{
	
	}
}
