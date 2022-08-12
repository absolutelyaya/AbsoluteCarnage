package yaya.absolutecarnage.items;

import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

import java.util.List;

public class JungleSeeds extends CarnageItem
{
	public JungleSeeds(Settings settings, String... lore)
	{
		super(settings, lore);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		//TODO: plant and grow Chomper bevahior
		return ActionResult.PASS;
	}
}
