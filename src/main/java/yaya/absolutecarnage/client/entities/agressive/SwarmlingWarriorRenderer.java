package yaya.absolutecarnage.client.entities.agressive;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.SwarmlingWarriorEntity;

public class SwarmlingWarriorRenderer extends GeoEntityRenderer<SwarmlingWarriorEntity>
{
	public SwarmlingWarriorRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx, new SwarmlingWarriorModel());
	}
	
	@Override
	public Identifier getTextureResource(SwarmlingWarriorEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/swarmling_warrior.png");
	}
	
	@Override
	public RenderLayer getRenderType(SwarmlingWarriorEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation)
	{
		return RenderLayer.getEntityTranslucent(textureLocation);
	}
}
