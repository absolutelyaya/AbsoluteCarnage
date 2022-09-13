package yaya.absolutecarnage.entities.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import yaya.absolutecarnage.registries.BlockEntityRegistry;
import yaya.absolutecarnage.registries.ParticleRegistry;

import java.util.Random;

public class InfestedChestBlockEntity extends AbstractChestBlockEntity
{
	public InfestedChestBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.INFESTED_CHEST, pos, state, "infested_chest");
	}
	
	@Override
	protected Text getContainerName()
	{
		return Text.translatable("container.absolute_carnage.infested_chest");
	}
	
	@Override
	protected void onOpen()
	{
		if(world != null)
		{
			Random r = new Random();
			Vector3d pos = new Vector3d(getPos().getX(), getPos().getY(), getPos().getZ());
			for(int i = 0; i < 6; i++)
				world.addParticle(ParticleRegistry.FLIES, pos.x + r.nextDouble(), pos.y + 0.9, pos.z + r.nextDouble(),
						0.0, r.nextFloat() * 0.025 + 0.05, 0.0);
		}
	}
	
	@Override
	protected void onClose() {
	
	}
}
