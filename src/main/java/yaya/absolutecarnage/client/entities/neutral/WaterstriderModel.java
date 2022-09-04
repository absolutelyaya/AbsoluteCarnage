package yaya.absolutecarnage.client.entities.neutral;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.WaterstriderEntity;

public class WaterstriderModel extends AnimatedGeoModel<WaterstriderEntity>
{
	@Override
	public Identifier getModelResource(WaterstriderEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "geo/entities/neutral/waterstrider.geo.json");
	}
	
	@Override
	public Identifier getTextureResource(WaterstriderEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/waterstrider.png");
	}
	
	@Override
	public Identifier getAnimationResource(WaterstriderEntity animatable)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "animations/entities/neutral/waterstrider.animation.json");
	}
	
	@SuppressWarnings({"unchecked"})
	@Override
	public void setLivingAnimations(WaterstriderEntity entity, Integer uniqueID, AnimationEvent customPredicate)
	{
		super.setLivingAnimations(entity, uniqueID, customPredicate);
		IBone head = this.getAnimationProcessor().getBone("Head");
		
		EntityModelData extraData = (EntityModelData)customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
		if(head != null)
		{
			//head.setRotationX(head.getRotationX() + extraData.headPitch * ((float) Math.PI / 180F));
			head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
		}
	}
}
