package yaya.absolutecarnage.client.entities.other;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.SwarmCluster;

public class SwarmClusterRenderer extends GeoEntityRenderer<SwarmCluster>
{
	public SwarmClusterRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx, new SwarmClusterModel());
	}
	
	@Override
	public Identifier getTextureResource(SwarmCluster object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/swarm_cluster.png");
	}
}
