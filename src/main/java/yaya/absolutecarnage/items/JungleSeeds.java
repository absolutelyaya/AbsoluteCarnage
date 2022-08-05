package yaya.absolutecarnage.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JungleSeeds extends Item
{
	public JungleSeeds(Settings settings)
	{
		super(settings);
	}
	
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		tooltip.add(Text.translatable("item.absolute_carnage.jungle_seeds.desc1"));
		tooltip.add(Text.translatable("item.absolute_carnage.jungle_seeds.desc2"));
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		//TODO: plant and grow Chomper bevahior
		return ActionResult.PASS;
	}
}
