package yaya.absolutecarnage.client.entities.block;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;
import yaya.absolutecarnage.entities.blocks.InfestedChestBlockEntity;

public class InfestedChestRenderer extends GeoBlockRenderer<InfestedChestBlockEntity>
{
	public InfestedChestRenderer(BlockEntityRendererFactory.Context ignored)
	{
		super(new InfestedChestModel());
	}
	
	@Override
	public RenderLayer getRenderType(InfestedChestBlockEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation)
	{
		return RenderLayer.getEntityTranslucent(getTextureResource(animatable));
	}
}
