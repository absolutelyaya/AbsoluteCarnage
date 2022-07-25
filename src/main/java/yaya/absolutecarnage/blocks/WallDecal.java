package yaya.absolutecarnage.blocks;

import net.minecraft.block.DeadCoralWallFanBlock;

public class WallDecal extends DeadCoralWallFanBlock
{
	public WallDecal(Settings settings)
	{
		super(settings);
		setDefaultState(getStateManager().getDefaultState().with(WATERLOGGED, false));
	}
}
