package yaya.absolutecarnage.client.entities.feature;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

import java.util.List;

// Made with Blockbench 4.5.1
// Exported for Minecraft version 1.17+ for Yarn
public class CocoonModel<T extends LivingEntity> extends AnimalModel<T>
{
	private final ModelPart torso;
	private final ModelPart bb_main;
	private final boolean thinArms;
	
	public CocoonModel(ModelPart root, boolean thinArms)
	{
		this.torso = root.getChild("torso");
		this.bb_main = root.getChild("bb_main");
		this.thinArms = thinArms;
	}
	
	public static TexturedModelData getTexturedModelData()
	{
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("torso", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -24.0F, -2.0F, 16.0F, 12.0F, 4.0F, new Dilation(0.3F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 16).cuboid(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.3F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	
	@Override
	public void setAngles(LivingEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch)
	{
	
	}
	
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha)
	{
		if(thinArms)
		{
			matrices.push();
			matrices.scale(0.85f, 1f, 1f);
		}
		torso.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		if(thinArms)
			matrices.pop();
		bb_main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
	
	@Override
	protected Iterable<ModelPart> getHeadParts()
	{
		return List.of();
	}
	
	@Override
	protected Iterable<ModelPart> getBodyParts()
	{
		return List.of();
	}
}
