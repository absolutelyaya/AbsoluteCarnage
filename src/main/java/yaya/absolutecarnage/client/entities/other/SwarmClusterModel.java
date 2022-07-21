package yaya.absolutecarnage.client.entities.other;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.SwarmCluster;

public class SwarmClusterModel extends AnimatedGeoModel<SwarmCluster>
{
	@Override
	public Identifier getModelResource(SwarmCluster object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "geo/entities/other/swarm_cluster.geo.json");
	}
	
	@Override
	public Identifier getTextureResource(SwarmCluster object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/swarm_cluster.png");
	}
	
	@Override
	public Identifier getAnimationResource(SwarmCluster animatable)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "animations/entities/other/swarm_cluster.animation.json");
	}
}
