package yaya.absolutecarnage.entities.Goals;

import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.NoPenaltySolidTargeting;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import yaya.absolutecarnage.entities.DualMotionEntity;

public class DualWanderingGoal extends WanderAroundGoal
{
	final Random random;
	final DualMotionEntity mob;
	
	public DualWanderingGoal(DualMotionEntity mob, double speed)
	{
		super((PathAwareEntity)mob, speed);
		this.mob = mob;
		this.random = ((PathAwareEntity)mob).getRandom();
	}
	
	@Override
	public boolean canStart()
	{
		return super.canStart();
	}
	
	@Nullable
	@Override
	protected Vec3d getWanderTarget()
	{
		if(mob.isGroundNavigating())
			return super.getWanderTarget();
		else
		{
			Vec3d vec3d = ((PathAwareEntity)mob).getRotationVec(0.0F);
			Vec3d vec3d2 = AboveGroundTargeting.find((PathAwareEntity)mob, 8, 7, vec3d.x, vec3d.z, 1.57F, 6, 4);
			return vec3d2 != null ? vec3d2 : NoPenaltySolidTargeting.find((PathAwareEntity)mob, 8, 4, -2, vec3d.x, vec3d.z, 1.57);
		}
	}
	
	@Override
	public void stop()
	{
		super.stop();
		if(random.nextInt(mob.isGroundNavigating() ? 4 : 8) == 0)
			mob.switchNavigator();
	}
}
