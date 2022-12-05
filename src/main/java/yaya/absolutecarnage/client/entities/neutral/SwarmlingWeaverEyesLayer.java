package yaya.absolutecarnage.client.entities.neutral;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.SwarmlingWeaverEntity;

public class SwarmlingWeaverEyesLayer extends GeoLayerRenderer<SwarmlingWeaverEntity>
{
	private static final Identifier LAYER = new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/swarmling_weaver_eyes.png");
	private static final Identifier MODEL = new Identifier(AbsoluteCarnage.MOD_ID, "geo/entities/neutral/swarmling_weaver.geo.json");
	
	public SwarmlingWeaverEyesLayer(IGeoRenderer<SwarmlingWeaverEntity> entityRendererIn)
	{
		super(entityRendererIn);
	}
	
	@Override
	public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, SwarmlingWeaverEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
	{
		RenderLayer layer = RenderLayer.getEyes(LAYER);
		this.getRenderer().render(this.getEntityModel().getModel(MODEL), entitylivingbaseIn, partialTicks, layer, matrixStackIn, bufferIn,
				bufferIn.getBuffer(layer), packedLightIn, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
	}
}
