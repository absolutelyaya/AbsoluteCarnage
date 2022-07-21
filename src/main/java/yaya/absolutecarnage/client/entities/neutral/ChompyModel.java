package yaya.absolutecarnage.client.entities.neutral;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.ChompyEntity;

public class ChompyModel extends AnimatedGeoModel<ChompyEntity>
{
	@Override
	public Identifier getModelResource(ChompyEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "geo/entities/neutral/chompy.geo.json");
	}
	
	@Override
	public Identifier getTextureResource(ChompyEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/chompy.png");
	}
	
	@Override
	public Identifier getAnimationResource(ChompyEntity animatable)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "animations/entities/neutral/chompy.animation.json");
	}
	
	@SuppressWarnings({"unchecked"})
	@Override
	public void setLivingAnimations(ChompyEntity entity, Integer uniqueID, AnimationEvent customPredicate)
	{
		super.setLivingAnimations(entity, uniqueID, customPredicate);
		IBone head = this.getAnimationProcessor().getBone("head");
		
		EntityModelData extraData = (EntityModelData)customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
		if(head != null)
		{
			head.setRotationX(head.getRotationX() + extraData.headPitch * ((float) Math.PI / 180F));
			head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
			head.setRotationZ(extraData.netHeadYaw * ((float) Math.PI / 180F));
		}
	}
}
