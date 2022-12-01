package yaya.absolutecarnage.client.entities.agressive;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.SwarmlingWarriorEntity;

public class SwarmlingWarriorModel extends AnimatedGeoModel<SwarmlingWarriorEntity>
{
	double lastTime;
	Vec3d lastWingPos;
	
	@Override
	public Identifier getModelResource(SwarmlingWarriorEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "geo/entities/aggressive/swarmling_warrior.geo.json");
	}
	
	@Override
	public Identifier getTextureResource(SwarmlingWarriorEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/swarmling_warrior.png");
	}
	
	@Override
	public Identifier getAnimationResource(SwarmlingWarriorEntity animatable)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "animations/entities/aggressive/swarmling_warrior.animation.json");
	}
	
	@SuppressWarnings({"unchecked"})
	@Override
	public void setLivingAnimations(SwarmlingWarriorEntity entity, Integer uniqueID, AnimationEvent customPredicate)
	{
		super.setLivingAnimations(entity, uniqueID, customPredicate);
		IBone head = this.getAnimationProcessor().getBone("Head");
		IBone wingL = this.getAnimationProcessor().getBone("WingLeft");
		IBone wingR = this.getAnimationProcessor().getBone("WingRight");
		
		float f = ((float) Math.PI / 180F);
		double delta = getCurrentTick() - lastTime;
		if(MinecraftClient.getInstance().isPaused())
			return;
		
		EntityModelData extraData = (EntityModelData)customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
		if(head != null)
		{
			head.setRotationX(head.getRotationX() + extraData.headPitch * f);
			head.setRotationY(extraData.netHeadYaw * f);
			head.setRotationZ(extraData.netHeadYaw * f * (head.getRotationX() / 180));
		}
		
		if(wingL != null && wingR != null)
		{
			if(lastWingPos == null)
				lastWingPos = new Vec3d(wingR.getRotationX(), wingR.getRotationY(), wingR.getRotationZ());
			
			Vec3d pos1 = new Vec3d(-15, -42, -5);
			Vec3d pos2 = new Vec3d(-25, -10, 30);
			if(!entity.isOnGround())
			{
				double time = Math.sin(lastTime) * 0.5 + 0.5;
				
				Vec3d pos = new Vec3d(MathHelper.lerp(time, pos1.x, pos2.x), MathHelper.lerp(time, pos1.y, pos2.y),
						MathHelper.lerp(time, pos1.z, pos2.z));
				
				wingL.setRotationX(-(float)pos.x * f);
				wingL.setRotationY(-(float)pos.y * f);
				wingL.setRotationZ(-(float)pos.z * f);
				wingR.setRotationX(-(float)pos.x * f);
				wingR.setRotationY((float)pos.y * f);
				wingR.setRotationZ((float)pos.z * f);
				//TODO: improve animation.
			}
			else
			{
				Vec3d pos = new Vec3d(MathHelper.lerp(delta, lastWingPos.x, -15), MathHelper.lerp(delta, lastWingPos.y, -52),
						MathHelper.lerp(delta, lastWingPos.z, -5));
				
				wingL.setRotationX((float)pos.x * f);
				wingL.setRotationY(-(float)pos.y * f);
				wingL.setRotationZ(-(float)pos.z * f);
				wingR.setRotationX((float)pos.x * f);
				wingR.setRotationY((float)pos.y * f);
				wingR.setRotationZ((float)pos.z * f);
				
				lastWingPos = pos;
			}
			lastTime = getCurrentTick();
		}
	}
}
