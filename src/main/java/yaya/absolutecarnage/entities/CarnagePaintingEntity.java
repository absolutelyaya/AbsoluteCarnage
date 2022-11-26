package yaya.absolutecarnage.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.decoration.painting.PaintingVariants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import yaya.absolutecarnage.registries.EntityRegistry;
import yaya.absolutecarnage.registries.ItemRegistry;

import java.util.List;
import java.util.Optional;

public class CarnagePaintingEntity extends PaintingEntity
{
	private static final TrackedData<String> PaintingType = DataTracker.registerData(CarnagePaintingEntity.class, TrackedDataHandlerRegistry.STRING);
	
	public boolean checkObstruction = true;
	
	public CarnagePaintingEntity(EntityType<? extends PaintingEntity> entityType, World world)
	{
		super(entityType, world);
	}
	
	private CarnagePaintingEntity(World world, BlockPos pos)
	{
		super(EntityRegistry.CARNAGE_PAINTING, world);
		this.attachmentPos = pos;
	}
	
	protected void initDataTracker()
	{
		super.initDataTracker();
		this.dataTracker.startTracking(PaintingType, "hyroglyphics");
	}
	
	public void onTrackedDataSet(TrackedData<?> data)
	{
		super.onTrackedDataSet(data);
		if (PaintingType.equals(data))
		{
			this.updateAttachmentPosition();
		}
	}
	
	private void setPaintingType(String type)
	{
		this.dataTracker.set(PaintingType, type);
	}
	
	public String getPaintingType()
	{
		return this.dataTracker.get(PaintingType);
	}
	
	@Override
	public void writeCustomDataToNbt(NbtCompound nbt)
	{
		nbt.putString("painting_type", getPaintingType());
		super.writeCustomDataToNbt(nbt);
	}
	
	@Override
	public void readCustomDataFromNbt(NbtCompound nbt)
	{
		setPaintingType(nbt.getString("painting_type"));
		super.readCustomDataFromNbt(nbt);
	}
	
	@Override
	public void onBreak(@Nullable Entity entity) {
		if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
		{
			
			switch (getPaintingType())
			{
				case "hyroglyphics" -> this.playSound(SoundEvents.BLOCK_STONE_BREAK, 1.0F, 1.0F);
				default -> this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);
			}
			if (entity instanceof PlayerEntity playerEntity)
			{
				if (playerEntity.getAbilities().creativeMode)
					return;
			}
			this.dropStack(getDropItem());
		}
	}
	
	public boolean setVariant(Identifier variant)
	{
		RegistryKey<PaintingVariant> registryKey = RegistryKey.of(Registry.PAINTING_VARIANT_KEY, variant);
		Optional<RegistryEntry<PaintingVariant>> entry = Registry.PAINTING_VARIANT.getEntry(registryKey);
		entry.ifPresent(this::setVariant);
		return entry.isPresent();
	}
	
	public void setVariant(RegistryEntry<PaintingVariant> variant)
	{
		List<DataTracker.Entry<?>> list = getDataTracker().getAllEntries();
		if(list == null)
			return;
		for (DataTracker.Entry<?> entry : list)
		{
			if(entry.get() instanceof RegistryEntry<?>)
			{
				if(((RegistryEntry<?>)entry.get()).value() instanceof PaintingVariant)
					dataTracker.set(((TrackedData<RegistryEntry>)entry.getData()), variant);
			}
		}
		this.updateAttachmentPosition();
	}
	
	ItemStack getDropItem()
	{
		Item item = switch(getPaintingType())
		{
			case "hyroglyphics" -> ItemRegistry.HYROGLYPHICS;
			default -> Items.PAINTING;
		};
		
		ItemStack stack = new ItemStack(item);
		NbtCompound nbt = stack.getOrCreateSubNbt("absolute_carnage");
		nbt.put("variant", NbtString.of(this.getVariant().getKey().orElse(PaintingVariants.KEBAB).getValue().toString()));
		stack.setSubNbt("absolute_carnage", nbt);
		return stack;
	}
	
	@Override
	public ItemStack getPickBlockStack()
	{
		return getDropItem();
	}
	
	public static Optional<CarnagePaintingEntity> placePainting(World world, BlockPos pos, Direction facing, Identifier variant)
	{
		CarnagePaintingEntity entity = new CarnagePaintingEntity(world, pos);
		
		entity.setFacing(facing);
		if (!entity.setVariant(variant))
			return Optional.empty();
		
		return entity.canStayAttached() ? Optional.of(entity) : Optional.empty();
	}
	
	@Override
	public void onPlace()
	{
		switch (getPaintingType())
		{
			case "hyroglyphics" -> this.playSound(SoundEvents.BLOCK_STONE_PLACE, 1.0F, 1.0F);
			default -> this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
		}
	}
	
	@Override
	public void tick()
	{
		if(checkObstruction)
			super.tick();
	}
}
