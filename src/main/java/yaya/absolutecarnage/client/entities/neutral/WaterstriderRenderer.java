package yaya.absolutecarnage.client.entities.neutral;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.WaterstriderEntity;

public class WaterstriderRenderer extends GeoEntityRenderer<WaterstriderEntity>
{
	public WaterstriderRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx, new WaterstriderModel());
	}
	
	@Override
	public Identifier getTextureResource(WaterstriderEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/waterstrider.png");
	}
	
	@Override
	public RenderLayer getRenderType(WaterstriderEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation)
	{
		if(animatable.isBaby())
			stack.scale(0.5f, 0.5f, 0.5f);
		
		return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
	}
}
