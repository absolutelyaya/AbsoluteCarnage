package yaya.absolutecarnage.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DecorationItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import yaya.absolutecarnage.entities.CarnagePaintingEntity;
import yaya.absolutecarnage.registries.PaintingRegistry;

import java.util.List;
import java.util.Optional;

public class CarnagePaintingItem extends DecorationItem
{
	public CarnagePaintingItem(EntityType<? extends AbstractDecorationEntity> type, Settings settings)
	{
		super(type, settings);
	}
	
	@Override
	public ItemStack getDefaultStack()
	{
		ItemStack stack = super.getDefaultStack();
		NbtCompound nbt = stack.getOrCreateSubNbt("absolute_carnage");
		nbt.put("variant", NbtString.of(PaintingRegistry.Hyroglyphics.get("monolith").toString()));
		stack.setSubNbt("absolute_carnage", nbt);
		return stack;
	}
	
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		super.appendTooltip(stack, world, tooltip, context);
		NbtCompound nbt = stack.getOrCreateSubNbt("absolute_carnage");
		if(nbt.contains("variant"))
		{
			tooltip.add(Text.empty());
			Identifier id = Identifier.tryParse(nbt.getString("variant"));
			if(id == null)
				return;
			String[] parts = id.getPath().split("/");
			tooltip.add(Text.translatable("item.absolute_carnage.hyroglyphics.desc1", parts[parts.length - 1]));
			RegistryKey<PaintingVariant> registryKey = RegistryKey.of(Registry.PAINTING_VARIANT_KEY, id);
			Optional<RegistryEntry<PaintingVariant>> optionalVariant = Registry.PAINTING_VARIANT.getEntry(registryKey);
			if(optionalVariant.isPresent())
			{
				PaintingVariant variant = optionalVariant.get().value();
				tooltip.add(Text.translatable("item.absolute_carnage.hyroglyphics.desc2.valid", variant.getWidth() / 16, variant.getHeight() / 16));
			}
			else
				tooltip.add(Text.translatable("item.absolute_carnage.hyroglyphics.desc2.invalid"));
		}
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		BlockPos blockPos = context.getBlockPos();
		Direction direction = context.getSide();
		BlockPos blockPos2 = blockPos.offset(direction);
		PlayerEntity playerEntity = context.getPlayer();
		ItemStack itemStack = context.getStack();
		if(playerEntity == null)
			return ActionResult.FAIL;
		NbtCompound nbt = itemStack.getSubNbt("absolute_carnage");
		if(nbt != null)
		{
			if(!nbt.contains("variant"))
			{
				playerEntity.sendMessage(Text.translatable("msg.absolute_carnage.painting.invalid", getName()), true);
				return ActionResult.FAIL;
			}
		}
		else
		{
			playerEntity.sendMessage(Text.translatable("msg.absolute_carnage.painting.nonbt", getName()), true);
			return ActionResult.FAIL;
		}
		Identifier variant = Identifier.tryParse(nbt.getString("variant"));
		
		if (!this.canPlaceOn(playerEntity, direction, itemStack, blockPos2))
		{
			playerEntity.sendMessage(Text.translatable("can't place here"), true);
			return ActionResult.FAIL;
		}
		else
		{
			World world = context.getWorld();
			AbstractDecorationEntity abstractDecorationEntity;
			
			Optional<CarnagePaintingEntity> optional = CarnagePaintingEntity.placePainting(world, blockPos2, direction, variant);
			if (optional.isEmpty())
			{
				playerEntity.sendMessage(Text.translatable("msg.absolute_carnage.painting.nospace", getName()), true);
				return ActionResult.CONSUME;
			}
			
			abstractDecorationEntity = optional.get();
			
			NbtCompound nbtCompound = itemStack.getNbt();
			if (nbtCompound != null)
			{
				EntityType.loadFromEntityNbt(world, playerEntity, abstractDecorationEntity, nbtCompound);
			}
			
			if (abstractDecorationEntity.canStayAttached())
			{
				if (!world.isClient)
				{
					abstractDecorationEntity.onPlace();
					world.emitGameEvent(playerEntity, GameEvent.ENTITY_PLACE, abstractDecorationEntity.getPos());
					world.spawnEntity(abstractDecorationEntity);
				}
				
				itemStack.decrement(1);
				return ActionResult.success(world.isClient);
			}
			else
			{
				playerEntity.sendMessage(Text.translatable("msg.absolute_carnage.painting.nospace", getName()), true);
				return ActionResult.CONSUME;
			}
		}
	}
	
	///TODO: preview rendering. A box of the right size would be enough
	///TODO: replace placeholder texture
}
