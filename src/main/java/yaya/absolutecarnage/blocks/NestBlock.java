package yaya.absolutecarnage.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;

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
	
	//TODO: add particles imitating small insects for hole variant
}
