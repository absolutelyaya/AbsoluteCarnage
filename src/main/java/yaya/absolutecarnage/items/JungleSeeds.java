package yaya.absolutecarnage.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import yaya.absolutecarnage.entities.ChompyEntity;
import yaya.absolutecarnage.registries.EntityRegistry;

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
		BlockPos pos = context.getBlockPos();
		World world = context.getWorld();
		BlockState block = world.getBlockState(pos);
		System.out.println("1");
		
		return ActionResult.PASS;
	}
}
