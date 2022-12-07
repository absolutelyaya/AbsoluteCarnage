package yaya.absolutecarnage.client.entities.neutral;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.SwarmlingWeaverEntity;

public class SwarmlingWeaverModel extends AnimatedGeoModel<SwarmlingWeaverEntity>
{
	@Override
	public Identifier getModelResource(SwarmlingWeaverEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "geo/entities/neutral/swarmling_weaver.geo.json");
	}
	
	@Override
	public Identifier getTextureResource(SwarmlingWeaverEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/swarmling_weaver.png");
	}
	
	@Override
	public Identifier getAnimationResource(SwarmlingWeaverEntity animatable)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "animations/entities/neutral/swarmling_weaver.animation.json");
	}
	
	@SuppressWarnings({"unchecked"})
	@Override
	public void setLivingAnimations(SwarmlingWeaverEntity entity, Integer uniqueID, AnimationEvent customPredicate)
	{
		super.setLivingAnimations(entity, uniqueID, customPredicate);
		IBone head = this.getAnimationProcessor().getBone("Head");
		
		float f = ((float) Math.PI / 180F);
		if(MinecraftClient.getInstance().isPaused())
			return;
		
		EntityModelData extraData = (EntityModelData)customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
		if(head != null && entity.getAnimation() != 3)
		{
			head.setRotationX(head.getRotationX() + extraData.headPitch * f);
			head.setRotationY(extraData.netHeadYaw * f);
			head.setRotationZ(extraData.netHeadYaw * f * (head.getRotationX() / 180));
		}
	}
}
