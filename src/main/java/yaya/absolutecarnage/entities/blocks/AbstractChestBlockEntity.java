package yaya.absolutecarnage.entities.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public abstract class AbstractChestBlockEntity extends ChestBlockEntity implements IAnimatable, LidOpenable
{
	public final String id;
	
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
	
	public AbstractChestBlockEntity(BlockEntityType type, BlockPos pos, BlockState state, String id)
	{
		super(type, pos, state);
		this.id = id;
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
					onOpen();
				}
			}
			else
			{
				animation = ANIMATION_CLOSE;
				onClose();
			}
		}
		return super.onSyncedBlockEvent(type, data);
	}
	
	protected abstract void onOpen();
	
	protected abstract void onClose();
}
