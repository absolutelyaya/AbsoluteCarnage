package yaya.absolutecarnage.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import yaya.absolutecarnage.registries.BlockTagRegistry;

public class SwarmlingSpawnEntity extends HostileEntity implements IAnimatable
{
	final AnimationFactory factory = new AnimationFactory(this);
	private final static AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("idle", true);
	
	public SwarmlingSpawnEntity(EntityType<? extends HostileEntity> entityType, World world)
	{
		super(entityType, world);
		setNoGravity(true);
	}
	
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event)
	{
		event.getController().setAnimation(IDLE_ANIM);
		return PlayState.CONTINUE;
	}
	
	@Override
	public void registerControllers(AnimationData animationData)
	{
		AnimationController<SwarmlingSpawnEntity> controller = new AnimationController<>(this, "controller",
				2, this::predicate);
		animationData.addAnimationController(controller);
	}
	
	@Override
	public AnimationFactory getFactory()
	{
		return factory;
	}
	
	public static DefaultAttributeContainer.Builder setAttributes()
	{
		return TameableEntity.createMobAttributes()
					   .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
					   .add(EntityAttributes.GENERIC_ARMOR, 4.0D);
	}
	
	@SuppressWarnings("unused")
	public static boolean canSpawn(EntityType<? extends LivingEntity> type, ServerWorldAccess world, SpawnReason reason, BlockPos pos, Random random)
	{
		return world.getBlockState(pos.down()).isIn(BlockTagRegistry.SWARMLING_SPAWNABLE);
	}
}
