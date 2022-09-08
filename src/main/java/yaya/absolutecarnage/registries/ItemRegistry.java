package yaya.absolutecarnage.registries;

import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.registry.Registry;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.items.*;
import yaya.absolutecarnage.items.trinkets.CarnageTrinket;
import yaya.absolutecarnage.items.trinkets.ShoesTrinketItem;
import yaya.absolutecarnage.items.trinkets.WingTrinketItem;
import yaya.absolutecarnage.utility.TranslationUtil;

public class ItemRegistry
{
	public static final Item JUNGLE_SEEDS = registerItem("jungle_seeds",
			new JungleSeeds(new FabricItemSettings().group(ItemGroupRegistry.CARNAGE).rarity(Rarity.RARE),
					TranslationUtil.getLoreBuilder("jungle_seeds").addLines(2).build()));
	public static final Item FLAME_THROWER = registerItem("flame_thrower",
			new FlameThrower(new FabricItemSettings().group(ItemGroupRegistry.CARNAGE).maxCount(1)));
	
	public static final Item TEST_WINGS = registerItem("test_wings",
			new WingTrinketItem(new FabricItemSettings().group(ItemGroupRegistry.CARNAGE_TRINKETS).maxCount(1).rarity(Rarity.EPIC),
					1f, "test_wings", 10, new Vec2f(0, 2.5f), new Vec2f(13, 7), new Vec2f(26, 35),
					TranslationUtil.getKey("desc", "dev")));
	public static final Item CUTICLE_WINGS = registerItem("cuticle_wings",
			new WingTrinketItem(new FabricItemSettings().group(ItemGroupRegistry.CARNAGE_TRINKETS).maxCount(1), 0.5f,
					"cuticle_wings", 2, new Vec2f(1, 3.5f), new Vec2f(13, 7), new Vec2f(26, 7),
					TranslationUtil.getLoreBuilder("cuticle_wings").addExtra(1).build()));
	public static final Item  SETAE_SHOES = registerItem("setae_boots",
			new ShoesTrinketItem(new FabricItemSettings().group(ItemGroupRegistry.CARNAGE_TRINKETS).maxCount(1),
					new CarnageTrinket.AttributeBuilder().addAttribute(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
							0.2, EntityAttributeModifier.Operation.ADDITION),
					TranslationUtil.getLoreBuilder("setae_boots").addLines(1).addExtra(3).build()));
	
	public static final Item INFESTED_CHEST = registerItem("infested_chest",
			new AnimatedBlockItem(BlockRegistry.INFESTED_CHEST, new FabricItemSettings().group(ItemGroupRegistry.CARNAGE)));
	
	public static final Item CARAPACE = registerItem("carapace", new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)
			.group(ItemGroupRegistry.CARNAGE)));
	public static final Item STICKY_SILK = registerItem("sticky_silk", new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)
			.group(ItemGroupRegistry.CARNAGE)));
	public static final Item CHARRED_REMAINS = registerItem("charred_remains", new CarnageItem(new FabricItemSettings()
			.group(ItemGroupRegistry.CARNAGE).fireproof(), TranslationUtil.getKey("desc", "trash")));
	public static final Item INSECT_EGG = registerItem("insect_egg", new CarnageItem(
			new FabricItemSettings().group(ItemGroupRegistry.CARNAGE), TranslationUtil.getLoreBuilder("insect_egg").addLines(1).build()));
	public static final Item SAND_BAG = registerItem("sand_bag", new SandBagItem(
			new FabricItemSettings().group(ItemGroupRegistry.CARNAGE)));
	
	public static final Item MUREYAKI = registerItem("mureyaki", //mure = swarm, yaki = grilled (群れ焼き). Inspired by taiyaki, those fish waffles.
			new CarnageItem(new FabricItemSettings().food(new FoodComponent.Builder().hunger(6).build())
					.group(ItemGroupRegistry.CARNAGE),
					TranslationUtil.getLoreBuilder("mureyaki").addLines(1).addExtra(1).build()));
	
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
