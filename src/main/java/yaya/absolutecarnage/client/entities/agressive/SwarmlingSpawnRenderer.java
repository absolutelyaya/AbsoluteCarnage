package yaya.absolutecarnage.client.entities.agressive;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.SwarmlingSpawnEntity;

public class SwarmlingSpawnRenderer extends GeoEntityRenderer<SwarmlingSpawnEntity>
{
	public SwarmlingSpawnRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx, new SwarmlingSpawnModel());
	}
	
	@Override
	public Identifier getTextureResource(SwarmlingSpawnEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/swarmling_spawn.png");
	}
	
	@Override
	public RenderLayer getRenderType(SwarmlingSpawnEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation)
	{
		return RenderLayer.getEntityTranslucent(textureLocation);
	}
}
