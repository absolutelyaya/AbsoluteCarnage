package yaya.absolutecarnage.client.entities.neutral;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.SwarmlingWeaverEntity;

public class SwarmlingWeaverRenderer extends GeoEntityRenderer<SwarmlingWeaverEntity>
{
	public SwarmlingWeaverRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx, new SwarmlingWeaverModel());
	}
	
	@Override
	public Identifier getTextureResource(SwarmlingWeaverEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/swarmling_weaver.png");
	}
	
	@Override
	public RenderLayer getRenderType(SwarmlingWeaverEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation)
	{
		return RenderLayer.getEntityTranslucent(textureLocation);
	}
}
