package yaya.absolutecarnage.client.entities.agressive;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.ChomperEntity;
import yaya.absolutecarnage.entities.SwarmlingSpawnEntity;

public class SwarmlingSpawnModel extends AnimatedGeoModel<SwarmlingSpawnEntity>
{
	@Override
	public Identifier getModelResource(SwarmlingSpawnEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "geo/entities/aggressive/swarmling_spawn.geo.json");
	}
	
	@Override
	public Identifier getTextureResource(SwarmlingSpawnEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/swarmling_spawn.png");
	}
	
	@Override
	public Identifier getAnimationResource(SwarmlingSpawnEntity animatable)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "animations/entities/aggressive/swarmling_spawn.animation.json");
	}
	
	@SuppressWarnings({"unchecked"})
	@Override
	public void setLivingAnimations(SwarmlingSpawnEntity entity, Integer uniqueID, AnimationEvent customPredicate)
	{
		super.setLivingAnimations(entity, uniqueID, customPredicate);
		IBone head = this.getAnimationProcessor().getBone("Head");
		IBone wingL = this.getAnimationProcessor().getBone("WingL");
		IBone wingR = this.getAnimationProcessor().getBone("WingR");
		
		float f = ((float) Math.PI / 180F);
		
		EntityModelData extraData = (EntityModelData)customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
		if(head != null)
		{
			head.setRotationX(head.getRotationX() + extraData.headPitch * f);
			head.setRotationZ(extraData.netHeadYaw * f);
		}
		
		if(wingL != null && wingR != null)
		{
			Vec3d pos1 = new Vec3d(15, -42, -5);
			Vec3d pos2 = new Vec3d(25, -10, 30);
			double time = Math.sin(getCurrentTick() * 1.5) * 0.5 + 0.5;
			
			Vec3d pos = new Vec3d(MathHelper.lerp(time, pos1.x, pos2.x), MathHelper.lerp(time, pos1.y, pos2.y),
					MathHelper.lerp(time, pos1.z, pos2.z));
			
			wingL.setRotationX(-(float)pos.x * f);
			wingL.setRotationY(-(float)pos.y * f);
			wingL.setRotationZ(-(float)pos.z * f);
			wingR.setRotationX(-(float)pos.x * f);
			wingR.setRotationY((float)pos.y * f);
			wingR.setRotationZ((float)pos.z * f);
		}
	}
}
