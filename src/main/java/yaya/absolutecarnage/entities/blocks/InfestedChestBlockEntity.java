package yaya.absolutecarnage.entities.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import yaya.absolutecarnage.registries.BlockEntityRegistry;
import yaya.absolutecarnage.registries.ParticleRegistry;

import java.util.Random;

public class InfestedChestBlockEntity extends ChestBlockEntity implements IAnimatable, LidOpenable
{
	private final AnimationFactory factory = new AnimationFactory(this);
	private static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("idle", true);
	private static final AnimationBuilder OPEN_ANIM = new AnimationBuilder().addAnimation("open", false);
	private static final AnimationBuilder OPENLOOP_ANIM = new AnimationBuilder().addAnimation("openloop", true);
	private static final AnimationBuilder CLOSE_ANIM = new AnimationBuilder().addAnimation("close", false);
	protected byte animation = ANIMATION_IDLE;
	private static final byte ANIMATION_IDLE = 0;
	private static final byte ANIMATION_OPEN = 1;
	private static final byte ANIMATION_OPENLOOP = 2;
	private static final byte ANIMATION_CLOSE = 3;
	
	public InfestedChestBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.INFESTED_CHEST, pos, state);
	}
	
	@Override
	public void registerControllers(AnimationData animationData)
	{
		animationData.addAnimationController(new AnimationController<>(this, "controller", 2, this::predicate));
	}
	
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event)
	{
		if(world != null)
		{
			if(animation == ANIMATION_OPEN && event.getController().getAnimationState() == AnimationState.Stopped)
				animation = ANIMATION_OPENLOOP;
			if(animation == ANIMATION_CLOSE && event.getController().getAnimationState() == AnimationState.Stopped)
				animation = ANIMATION_IDLE;
			
			switch(animation)
			{
				case ANIMATION_IDLE -> event.getController().setAnimation(IDLE_ANIM);
				case ANIMATION_OPEN -> event.getController().setAnimation(OPEN_ANIM);
				case ANIMATION_OPENLOOP -> event.getController().setAnimation(OPENLOOP_ANIM);
				case ANIMATION_CLOSE -> event.getController().setAnimation(CLOSE_ANIM);
			}
		}
		return PlayState.CONTINUE;
	}
	
	@Override
	public AnimationFactory getFactory()
	{
		return this.factory;
	}
	
	@Override
	public void onScheduledTick()
	{
		super.onScheduledTick();
	}
	
	@Override
	public boolean onSyncedBlockEvent(int type, int data)
	{
		if (type == 1)
		{
			boolean open = data > 0;
			
			if(open)
			{
				if(animation != ANIMATION_OPENLOOP)
				{
					animation = ANIMATION_OPEN;
					if(world != null)
					{
						Random r = new Random();
						Vector3d pos = new Vector3d(getPos().getX(), getPos().getY(), getPos().getZ());
						for(int i = 0; i < 6; i++)
							world.addParticle(ParticleRegistry.FLIES, pos.x + r.nextDouble(), pos.y + 0.9, pos.z + r.nextDouble(),
									0.0, r.nextFloat() * 0.025 + 0.05, 0.0);
					}
				}
			}
			else
			{
				animation = ANIMATION_CLOSE;
			}
		}
		return super.onSyncedBlockEvent(type, data);
	}
}
