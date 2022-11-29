package yaya.absolutecarnage.client.entities.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.client.AbsoluteCarnageClient;
import yaya.absolutecarnage.registries.StatusEffectRegistry;

@Environment(EnvType.CLIENT)
public class CocoonFeature<T extends LivingEntity, M extends PlayerEntityModel<T>> extends FeatureRenderer<T, M>
{
	private static final Identifier TEXTURE = new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/cocoon.png");
	private final CocoonModel<T> cocoon;
	
	public CocoonFeature(FeatureRendererContext<T, M> context, EntityModelLoader loader, boolean thinArms)
	{
		super(context);
		cocoon = new CocoonModel<>(loader.getModelPart(AbsoluteCarnageClient.COCOON_LAYER), thinArms);
		///TODO: add toggleable cocoon parts over armor if any is worn
	}
	
	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch)
	{
		if (entity.hasStatusEffect(StatusEffectRegistry.WEBBED))
		{
			matrices.push();
			matrices.translate(0.0, 0.0, 0);
			this.getContextModel().copyStateTo(this.cocoon);
			this.cocoon.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
			VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(TEXTURE), false, false);
			this.cocoon.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
			matrices.pop();
		}
	}
}
