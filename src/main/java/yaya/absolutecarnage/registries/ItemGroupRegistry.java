package yaya.absolutecarnage.registries;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import yaya.absolutecarnage.AbsoluteCarnage;

public class ItemGroupRegistry
{
	public static final ItemGroup CARNAGE = FabricItemGroupBuilder.build(
			new Identifier(AbsoluteCarnage.MOD_ID, "carnage_main"), () -> new ItemStack(ItemRegistry.JUNGLE_SEEDS));
	public static final ItemGroup CARNAGE_TRINKETS = FabricItemGroupBuilder.build(
			new Identifier(AbsoluteCarnage.MOD_ID, "carnage_trinkets"), () -> new ItemStack(Items.FEATHER));
}
