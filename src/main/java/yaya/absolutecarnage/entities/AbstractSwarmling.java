package yaya.absolutecarnage.entities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import yaya.absolutecarnage.registries.BlockTagRegistry;

public abstract class AbstractSwarmling extends HostileEntity implements DualMotionEntity, SwarmEntity
{
	protected boolean groundNavigation, stopAirNavigation;
	
	protected AbstractSwarmling(EntityType<? extends HostileEntity> entityType, World world)
	{
		super(entityType, world);
	}
	
	public EntityGroup getGroup() {
		return EntityGroup.ARTHROPOD;
	}
	
	@Override
	public boolean damage(DamageSource source, float amount)
	{
		if(source.equals(DamageSource.ON_FIRE))
			amount *= 3f;
		return super.damage(source, amount);
	}
	
	protected EntityNavigation createNavigation(World world)
	{
		BirdNavigation nav = new BirdNavigation(this, world);
		nav.setCanPathThroughDoors(false);
		nav.setCanSwim(false);
		nav.setCanEnterOpenDoors(true);
		return nav;
	}
	
	public void switchNavigator(boolean onLand)
	{
		if (onLand)
		{
			this.moveControl = new MoveControl(this);
			this.navigation = new MobNavigation(this, world);
		}
		else
		{
			this.moveControl = new FlightMoveControl(this, 20, true);
			this.navigation = new BirdNavigation(this, world);
		}
		groundNavigation = onLand;
	}
	
	public void switchNavigator()
	{
		if(groundNavigation)
			switchNavigator(false);
		else
		{
			this.moveControl = new FlightMoveControl(this, 20, false);
			stopAirNavigation = true;
		}
	}
	
	public boolean isGroundNavigating()
	{
		return groundNavigation;
	}
	
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}
	
	protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition)
	{
		setOnGround(onGround);
		if(stopAirNavigation)
		{
			switchNavigator(true);
			stopAirNavigation = false;
		}
	}
	
	@SuppressWarnings("unused")
	public static boolean canSpawn(EntityType<? extends LivingEntity> type, ServerWorldAccess world, SpawnReason reason, BlockPos pos, Random random)
	{
		return world.getBlockState(pos.down()).isIn(BlockTagRegistry.SWARMLING_SPAWNABLE);
	}
	
	@Override
	public void baseTick()
	{
		super.baseTick();
		if (touchingWater)
		{
			if (!groundNavigation)
				switchNavigator(true);
			speed = 0.75f;
			upwardSpeed = 0.5f;
			((CarnageEntityAccessor) this).setDrowning(true);
		} else if (((CarnageEntityAccessor) this).isDrowning())
			((CarnageEntityAccessor) this).setDrowning(false);
	}
	
	@Override
	public void tickMovement()
	{
		super.tickMovement();
		Vec3d vec3d = this.getVelocity();
		if (!this.onGround && vec3d.y < 0.0)
			this.setVelocity(vec3d.multiply(1.0, 0.8, 1.0));
	}
}
