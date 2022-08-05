package yaya.absolutecarnage.items.trinkets;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class WingTrinketItem extends TrinketItem implements TrinketRenderer
{
	final float power;
	
	public WingTrinketItem(Settings settings, float power)
	{
		super(settings);
		this.power = power;
	}
	
	public float getPower()
	{
		return power;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch)
	{
		if(entity instanceof AbstractClientPlayerEntity player)
		{
			matrices.push();
			
			TrinketRenderer.translateToChest(matrices, (PlayerEntityModel<AbstractClientPlayerEntity>)contextModel, player);
			matrices.translate(0, 0, 0.3);
			
			MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);
			matrices.pop();
		}
	}
}
