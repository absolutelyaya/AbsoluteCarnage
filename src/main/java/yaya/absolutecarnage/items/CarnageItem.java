package yaya.absolutecarnage.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CarnageItem extends Item
{
	protected final List<String> lore;
	
	public CarnageItem(Settings settings, String... lore)
	{
		super(settings);
		this.lore = Arrays.stream(lore).toList();
	}
	
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		lore.forEach(line ->
		{
			if(!line.startsWith("e#"))
				tooltip.add(Text.translatable(line));
			else if(context.isAdvanced())
				tooltip.add(Text.translatable(line.replace("e#", "")));
		});
	}
}
