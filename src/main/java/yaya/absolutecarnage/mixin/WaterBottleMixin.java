package yaya.absolutecarnage.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.PotionItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yaya.absolutecarnage.blocks.QuicksandBlock;
import yaya.absolutecarnage.registries.BlockRegistry;
import yaya.absolutecarnage.registries.BlockTagRegistry;

@Mixin(PotionItem.class)
public class WaterBottleMixin
{
	@Inject(method = "useOnBlock", at = @At("TAIL"))
	public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir)
	{
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		PlayerEntity player = context.getPlayer();
		ItemStack stack = context.getStack();
		BlockState state = world.getBlockState(pos);
		if (context.getSide() != Direction.DOWN && state.isIn(BlockTagRegistry.QUICKSAND_CONVERTABLE) && PotionUtil.getPotion(stack) == Potions.WATER)
		{
			world.playSound(player, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.PLAYERS, 1.0F, 1.0F);
			world.setBlockState(pos, BlockRegistry.QUICKSAND.getDefaultState().with(QuicksandBlock.INDENT, world.random.nextInt(2) + 1));
			if(player != null)
				player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
			for(int i = 0; i < 5; ++i) {
				world.addParticle(ParticleTypes.SPLASH,
						pos.getX() + world.random.nextDouble(), pos.getY() + 1, pos.getZ() + world.random.nextDouble(),
						0.0, 0.5, 0.0);
			}
		}
	}
}
