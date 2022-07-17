package yaya.absolutecarnage.client.entities.agressive;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.ChomperEntity;

public class ChomperModel extends AnimatedGeoModel<ChomperEntity>
{
	@Override
	public Identifier getModelResource(ChomperEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "geo/aggressive/chomper.geo.json");
	}
	
	@Override
	public Identifier getTextureResource(ChomperEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/chomper.png");
	}
	
	@Override
	public Identifier getAnimationResource(ChomperEntity animatable)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "animations/aggressive/chomper.animation.json");
	}
	
	@SuppressWarnings({"unchecked"})
	@Override
	public void setLivingAnimations(ChomperEntity entity, Integer uniqueID, AnimationEvent customPredicate)
	{
		super.setLivingAnimations(entity, uniqueID, customPredicate);
		IBone head = this.getAnimationProcessor().getBone("Head");
		
		EntityModelData extraData = (EntityModelData)customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
		if(head != null && entity.isLookingAround())
		{
			head.setRotationX(head.getRotationX() + extraData.headPitch * ((float) Math.PI / 180F));
			head.setRotationZ(extraData.netHeadYaw * ((float) Math.PI / 180F));
		}
	}
}
