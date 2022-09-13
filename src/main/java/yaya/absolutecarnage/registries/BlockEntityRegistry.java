package yaya.absolutecarnage.registries;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.blocks.InfestedChestBlockEntity;
import yaya.absolutecarnage.entities.blocks.OrnateSandstoneChestBlockEntity;

public class BlockEntityRegistry
{
	public static BlockEntityType<InfestedChestBlockEntity> INFESTED_CHEST;
	public static BlockEntityType<OrnateSandstoneChestBlockEntity> ORNATE_SANDSTONE_CHEST;
	
	public static void registerBlockEntities()
	{
		INFESTED_CHEST = Registry.register(Registry.BLOCK_ENTITY_TYPE,
				new Identifier(AbsoluteCarnage.MOD_ID, "infested_chest"),
				FabricBlockEntityTypeBuilder.create(InfestedChestBlockEntity::new, BlockRegistry.INFESTED_CHEST)
						.build());
		ORNATE_SANDSTONE_CHEST = Registry.register(Registry.BLOCK_ENTITY_TYPE,
				new Identifier(AbsoluteCarnage.MOD_ID, "ornate_sandstone_chest"),
				FabricBlockEntityTypeBuilder.create(OrnateSandstoneChestBlockEntity::new, BlockRegistry.ORNATE_SANDSTONE_CHEST)
						.build());
	}
}
