package yaya.absolutecarnage.items;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CarnageBlockItem extends BlockItem
{
	protected final List<String> lore;
	
	public CarnageBlockItem(Block block, Settings settings, String... lore)
	{
		super(block, settings);
		this.lore = Arrays.stream(lore).toList();
	}
	
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		lore.forEach(line ->
		{
			if(!line.startsWith("e#"))
				tooltip.add(Text.translatable(line));
		});
		if(lore.stream().anyMatch(line -> line.startsWith("e#")) && context.isAdvanced())
		{
			tooltip.add(Text.empty());
			lore.forEach(line ->
			{
				if(line.startsWith("e#") && context.isAdvanced())
					tooltip.add(Text.translatable(line.replace("e#", "")));
			});
			tooltip.add(Text.empty());
		}
	}
}
