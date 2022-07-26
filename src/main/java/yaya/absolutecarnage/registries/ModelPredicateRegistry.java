package yaya.absolutecarnage.registries;

import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class ModelPredicateRegistry
{
	public static void registerModels()
	{
		registerHoldUsageItem(ItemRegistry.FLAME_THROWER);
	}
	
	private static void registerHoldUsageItem(Item item)
	{
		FabricModelPredicateProviderRegistry.register(item, new Identifier("use_time"),
				((stack, world, entity, seed) -> {
					if(entity == null)
						return 0f;
					if(entity.getActiveItem() != stack)
						return 0f;
					return 1f - entity.getItemUseTimeLeft() / (float)(stack.getMaxUseTime());
				}));
		
		FabricModelPredicateProviderRegistry.register(item, new Identifier("using"),
				(stack, world, entity, seed) -> (entity != null && entity.isUsingItem() &&
						entity.getActiveItem() == stack ? 1f : 0f));
		
		FabricModelPredicateProviderRegistry.register(item, new Identifier("cooldown"),
				((stack, world, entity, seed) -> {
					if(entity instanceof PlayerEntity player)
						if(player.getItemCooldownManager().isCoolingDown(stack.getItem()))
							return 1f;
					return 0f;
				})); //TODO: get this to actually work
	}
}
