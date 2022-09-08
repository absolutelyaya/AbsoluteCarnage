package yaya.absolutecarnage.entities.goals;

import net.minecraft.entity.ai.goal.Goal;
import yaya.absolutecarnage.entities.SwarmEntity;

public abstract class SwarmGoal extends Goal
{
	private final SwarmEntity mob;
	
	public SwarmGoal(SwarmEntity mob)
	{
		this.mob = mob;
	}
	
	@Override
	public boolean canStart()
	{
		return mob.swarm.size() > 0;
	}
}
