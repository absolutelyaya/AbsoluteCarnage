package yaya.absolutecarnage.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yaya.absolutecarnage.entities.ChompyEntity;
import yaya.absolutecarnage.registries.EntityRegistry;
import yaya.absolutecarnage.registries.ItemRegistry;

@Mixin(FlowerPotBlock.class)
public class FlowerPotMixin
{
	@Inject(at = @At("RETURN"), method = "onUse")
	private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir)
	{
		ItemStack itemStack = player.getStackInHand(hand);
		if(itemStack.isOf(ItemRegistry.JUNGLE_SEEDS))
		{
			if(world.breakBlock(pos, false, player))
			{
				ChompyEntity entity = new ChompyEntity(EntityRegistry.CHOMPY, world);
				entity.setPos(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
				world.spawnEntity(entity);
				if(!player.getAbilities().creativeMode)
					itemStack.decrement(1);
			}
		}
	}
	//TODO: probably remove this mixin.
}
