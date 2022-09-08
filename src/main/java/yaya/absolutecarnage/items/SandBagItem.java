package yaya.absolutecarnage.items;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.MixinEnvironment;
import yaya.absolutecarnage.blocks.QuicksandBlock;
import yaya.absolutecarnage.registries.BlockRegistry;
import yaya.absolutecarnage.registries.ItemRegistry;

public class SandBagItem extends CarnageBlockItem
{
	public SandBagItem(Settings settings, String... lore)
	{
		super(BlockRegistry.QUICKSAND, settings, lore);
	}
	
	@Nullable
	@Override
	protected BlockState getPlacementState(ItemPlacementContext context)
	{
		World world = context.getWorld();
		BlockState state = world.getBlockState(context.getBlockPos());
		if(state.isOf(BlockRegistry.QUICKSAND) && context.getSide().equals(Direction.UP) && state.get(QuicksandBlock.INDENT) > 0)
			return state.with(QuicksandBlock.INDENT, state.get(QuicksandBlock.INDENT) - 1);
		return getBlock().getDefaultState();
	}
	
	@Override
	public String getTranslationKey()
	{
		return getOrCreateTranslationKey();
	}
}
