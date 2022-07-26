package yaya.absolutecarnage.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import yaya.absolutecarnage.registries.ParticleRegistry;

import static net.minecraft.util.math.Direction.Type.HORIZONTAL;

public class NestBlock extends Block
{
	public static final BooleanProperty HOLES = BooleanProperty.of("holes");
	
	public NestBlock(Settings settings)
	{
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(HOLES, false));
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(HOLES);
	}
	
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random)
	{
		if(state.get(HOLES))
		{
			boolean exposed = false;
			for (Direction dir : HORIZONTAL)
			{
				if (world.isAir(pos.offset(dir)))
				{
					exposed = true;
					break;
				}
			}
			if (exposed && random.nextInt(5) == 0)
			{
				Vec3d ParticlePos = new Vec3d(pos.getX(), pos.getY(), pos.getZ())
						.add(random.nextDouble(), random.nextDouble() * 0.5 + 0.25, random.nextDouble());
				world.addParticle(ParticleRegistry.FLIES, ParticlePos.x, ParticlePos.y, ParticlePos.z, 0, 0, 0);
			}
		}
	}
}
