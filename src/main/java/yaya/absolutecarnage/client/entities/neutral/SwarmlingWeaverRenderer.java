package yaya.absolutecarnage.client.entities.neutral;

import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.SwarmlingWeaverEntity;

public class SwarmlingWeaverRenderer extends GeoEntityRenderer<SwarmlingWeaverEntity>
{
	private static final Identifier ROPE_TEXTURE = new Identifier(AbsoluteCarnage.MOD_ID, "textures/block/dangling_egg2.png");
	
	public SwarmlingWeaverRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx, new SwarmlingWeaverModel());
		addLayer(new SwarmlingWeaverEyesLayer(this));
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
	
	@Override
	public void render(GeoModel model, SwarmlingWeaverEntity animatable, float partialTicks, RenderLayer type, MatrixStack matrixStack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		animatable.alpha = MathHelper.lerp(partialTicks / 30, animatable.alpha, animatable.isRopeClimbing() ? 0.25f : 1f);
		
		matrixStack.push();
		float rot = MathHelper.lerp(partialTicks, animatable.lastClimbingRot, animatable.getDataTracker().get(SwarmlingWeaverEntity.CLIMBING_ROTATION));
		if(rot > 0f)
		{
			matrixStack.multiply(Quaternion.fromEulerXyzDegrees(new Vec3f(90f * rot, 0f, 0f)));
			matrixStack.translate(0f, -rot * 0.75f, 0f);
			animatable.lastClimbingRot = rot;
		}
		super.render(model, animatable, partialTicks, type, matrixStack, renderTypeBuffer, vertexBuilder, packedLightIn,
				packedOverlayIn, red, green, blue, alpha * animatable.alpha);
		matrixStack.pop();
		if(animatable.hasRopeAttachmentPos() && animatable.getAnimationTicks() >= 16 && model.getBone("RopeAttachment").isPresent())
		{
			GeoBone ropeAttachment = model.getBone("RopeAttachment").get();
			
			Vec3d start = new Vec3d(ropeAttachment.getPositionX() / 16f, ropeAttachment.getPositionY() / 16f, ropeAttachment.getPositionZ() / 16f);
			Vec3d end = new Vec3d(start.x, animatable.getRopeAttachmentPos().getY() - animatable.getY(), start.z);
			
			Vec3d dir = end.subtract(start).normalize();
			Vec3d sideX = dir.withAxis(Direction.Axis.X, 0.5).multiply(1, 0, 1);
			Vec3d sideZ = dir.withAxis(Direction.Axis.Z, 0.5).multiply(1, 0, 1);
			float dist = (float)start.distanceTo(end);
			
			matrixStack.push();
			
			VertexConsumer vertexConsumer = renderTypeBuffer.getBuffer(RenderLayer.getEntityCutoutNoCull(ROPE_TEXTURE));
			MatrixStack.Entry entry = matrixStack.peek();
			Matrix4f matrix4f = entry.getPositionMatrix();
			Matrix3f matrix3f = entry.getNormalMatrix();
			vertex(vertexConsumer, matrix4f, matrix3f, start.add(sideX), packedLightIn, 0, 0);
			vertex(vertexConsumer, matrix4f, matrix3f, end.add(sideX), packedLightIn, 0, dist);
			vertex(vertexConsumer, matrix4f, matrix3f, end.subtract(sideX), packedLightIn, 1, dist);
			vertex(vertexConsumer, matrix4f, matrix3f, start.subtract(sideX), packedLightIn, 1, 0);
			
			vertex(vertexConsumer, matrix4f, matrix3f, start.add(sideZ), packedLightIn, 0, 0);
			vertex(vertexConsumer, matrix4f, matrix3f, end.add(sideZ), packedLightIn, 0, dist);
			vertex(vertexConsumer, matrix4f, matrix3f, end.subtract(sideZ), packedLightIn, 1, dist);
			vertex(vertexConsumer, matrix4f, matrix3f, start.subtract(sideZ), packedLightIn, 1, 0);
			matrixStack.pop();
		}
	}
	
	private static void vertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, Vec3d pos, int light, float u, float v) {
		vertexConsumer.vertex(positionMatrix, (float)pos.x, (float)pos.y, (float)pos.z)
				.color(255, 255, 255, 255)
				.texture(u, v).overlay(OverlayTexture.DEFAULT_UV)
				.light(light)
				.normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
	}
	
	@Override
	protected void applyRotations(SwarmlingWeaverEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
	{
		//if(entityLiving.getAnimation() != 3) //do not rotate during web attack animation
			super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
	}
}