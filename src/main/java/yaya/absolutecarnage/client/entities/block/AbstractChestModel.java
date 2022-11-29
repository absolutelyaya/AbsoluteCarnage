package yaya.absolutecarnage.client.entities.block;

import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.blocks.CarnageChestBlock;
import yaya.absolutecarnage.entities.blocks.AbstractChestBlockEntity;

public class AbstractChestModel extends AnimatedGeoModel<AbstractChestBlockEntity>
{
	@Override
	public Identifier getModelResource(AbstractChestBlockEntity object)
	{
		ChestType type = object.getCachedState().get(ChestBlock.CHEST_TYPE);
		return new Identifier(AbsoluteCarnage.MOD_ID, "geo/entities/block/" +
			(type != ChestType.SINGLE && !((CarnageChestBlock)object.getCachedState().getBlock()).isSingleOnly() ? "large_" : "") + object.id + ".geo.json");
	}
	
	@Override
	public Identifier getTextureResource(AbstractChestBlockEntity object)
	{
		ChestType type = object.getCachedState().get(ChestBlock.CHEST_TYPE);
		return new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/block/" +
			(type != ChestType.SINGLE && !((CarnageChestBlock)object.getCachedState().getBlock()).isSingleOnly() ? "large_" : "") + object.id + ".png");
	}
	
	@Override
	public Identifier getAnimationResource(AbstractChestBlockEntity animatable)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "animations/entities/block/" + animatable.id + ".animation.json");
	}
	
	@Override
	public void setLivingAnimations(AbstractChestBlockEntity entity, Integer uniqueID, AnimationEvent customPredicate)
	{
		ChestType type = entity.getCachedState().get(ChestBlock.CHEST_TYPE);
		if(type != ChestType.SINGLE && !((CarnageChestBlock)entity.getCachedState().getBlock()).isSingleOnly())
		{
			IBone base_left = this.getAnimationProcessor().getBone("base_left");
			IBone base_right = this.getAnimationProcessor().getBone("base");
			base_right.setHidden(type == ChestType.LEFT);
			base_left.setHidden(type == ChestType.RIGHT);
			base_left.setPositionX(-8);
			base_right.setPositionX(8);
			
			IBone lid = this.getAnimationProcessor().getBone("lid");
			IBone llid = this.getAnimationProcessor().getBone("lid_left");
			llid.setPositionX(lid.getPositionX());
			llid.setPositionY(lid.getPositionY());
			llid.setPositionZ(lid.getPositionZ());
			llid.setRotationX(lid.getRotationX());
			llid.setRotationY(lid.getRotationY());
			llid.setRotationZ(lid.getRotationZ());
		}
	}
}
