package yaya.absolutecarnage.mixin;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.registries.ItemRegistry;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class SlowDownReductionMixin extends LivingEntity implements Nameable, CommandOutput
{
	@Shadow public abstract boolean isPlayer();
	
	@Shadow @Final private PlayerAbilities abilities;
	
	protected SlowDownReductionMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}
	
	@Inject(at = @At("HEAD"), method = "slowMovement", cancellable = true)
	public void slowMovement(BlockState state, Vec3d multiplier, CallbackInfo info)
	{
		if(this.isPlayer() && !this.abilities.flying)
		{
			Optional<TrinketComponent> opt = TrinketsApi.getTrinketComponent(this);
			if(opt.isEmpty())
				return;
			TrinketComponent component = opt.get();
			if(component.isEquipped(ItemRegistry.SETAE_SHOES) && state.isIn(TagKey.of(Registry.BLOCK_KEY,
					new Identifier(AbsoluteCarnage.MOD_ID, "setae_shoes_affected"))))
			{
				multiplier = multiplier.multiply(3);
				movementMultiplier = new Vec3d(Math.min(multiplier.x, 1), 1, Math.min(multiplier.x, 1));
				info.cancel();
			}
		}
	}
	
	@Override
	protected float getJumpVelocityMultiplier()
	{
		float multiplier = super.getJumpVelocityMultiplier();
		
		Optional<TrinketComponent> opt = TrinketsApi.getTrinketComponent(this);
		if(opt.isEmpty())
			return multiplier;
		TrinketComponent component = opt.get();
		BlockState state = this.world.getBlockState(this.getBlockPos());
		if(component.isEquipped(ItemRegistry.SETAE_SHOES) && state.isIn(TagKey.of(Registry.BLOCK_KEY,
				new Identifier(AbsoluteCarnage.MOD_ID, "setae_shoes_affected"))))
		{
			multiplier = multiplier * 2;
			multiplier = Math.min(multiplier, 1);
		}
		
		return multiplier;
	}
}
