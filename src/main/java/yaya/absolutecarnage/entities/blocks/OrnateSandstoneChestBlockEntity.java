package yaya.absolutecarnage.entities.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import yaya.absolutecarnage.registries.BlockEntityRegistry;
import yaya.absolutecarnage.registries.ParticleRegistry;

import java.util.Random;

public class OrnateSandstoneChestBlockEntity extends AbstractChestBlockEntity
{
	public OrnateSandstoneChestBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.ORNATE_SANDSTONE_CHEST, pos, state, "ornate_sandstone_chest");
	}
	
	@Override
	protected Text getContainerName()
	{
		return Text.translatable("container.absolute_carnage.ornate_sandstone_chest");
	}
	
	@Override
	protected void onOpen() {
	}
	
	@Override
	protected void onClose() {
	
	}
	
	
}
