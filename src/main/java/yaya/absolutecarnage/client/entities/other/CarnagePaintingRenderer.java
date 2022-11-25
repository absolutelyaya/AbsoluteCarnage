package yaya.absolutecarnage.client.entities.other;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PaintingEntityRenderer;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.CarnagePaintingEntity;

public class CarnagePaintingRenderer extends PaintingEntityRenderer
{
	public CarnagePaintingRenderer(EntityRendererFactory.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(PaintingEntity paintingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i)
	{
		matrixStack.push();
		matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - f));
		PaintingVariant paintingVariant = paintingEntity.getVariant().value();
		matrixStack.scale(0.0625F, 0.0625F, 0.0625F);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(this.getTexture(paintingEntity)));
		PaintingManager paintingManager = MinecraftClient.getInstance().getPaintingManager();
		renderPainting(matrixStack, vertexConsumer, paintingEntity, paintingVariant.getWidth(), paintingVariant.getHeight(),
				paintingManager.getPaintingSprite(paintingVariant), getBackSprite((CarnagePaintingEntity)paintingEntity));
		matrixStack.pop();
	}
	
	public Identifier getTexture(PaintingEntity paintingEntity)
	{
		return MinecraftClient.getInstance().getPaintingManager().getBackSprite().getAtlas().getId();
	}
	
	public Sprite getBackSprite(CarnagePaintingEntity paintingEntity)
	{
		PaintingManager paintingManager = MinecraftClient.getInstance().getPaintingManager();
		return paintingManager.getSprite(new Identifier(AbsoluteCarnage.MOD_ID, paintingEntity.getPaintingType() + "/back"));
	}
}
