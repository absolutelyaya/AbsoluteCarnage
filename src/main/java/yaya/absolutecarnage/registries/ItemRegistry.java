package yaya.absolutecarnage.registries;

import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Vec2f;
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
			new WingTrinketItem(new FabricItemSettings().group(ItemGroupRegistry.CARNAGE_TRINKETS).maxCount(1), 0.5f,
					"cuticle_wings", 2, new Vec2f(1, 3.5f), new Vec2f(13, 7), new Vec2f(26, 7)));
	
	public static final Item INFESTED_CHEST = registerItem("infested_chest",
			new AnimatedBlockItem(BlockRegistry.INFESTED_CHEST, new FabricItemSettings().group(ItemGroupRegistry.CARNAGE)));
	
	public static Item registerItem(String name, Item item)
	{
		if(item instanceof TrinketRenderer)
			TrinketRendererRegistry.registerRenderer(item, (TrinketRenderer)item);
		return Registry.register(Registry.ITEM, new Identifier(AbsoluteCarnage.MOD_ID, name), item);
	}
	
	public static void registerItems()
	{
	
	}
}
