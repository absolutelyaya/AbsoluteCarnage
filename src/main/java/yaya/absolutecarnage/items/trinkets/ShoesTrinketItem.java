package yaya.absolutecarnage.items.trinkets;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtInt;
import net.minecraft.util.math.Quaternion;

public class ShoesTrinketItem extends CarnageTrinket
{
	public ShoesTrinketItem(Settings settings, AttributeBuilder attributes, String... lore)
	{
		super(settings, attributes, lore);
	}
	
	public ShoesTrinketItem(Settings settings, String... lore)
	{
		this(settings, new AttributeBuilder(), lore);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch)
	{
		if(entity instanceof AbstractClientPlayerEntity player)
		{
			ItemRenderer render = MinecraftClient.getInstance().getItemRenderer();
			ItemStack stack2 = stack.copy();
			stack2.setSubNbt("CustomModelData", NbtInt.of(1));
			matrices.push();
			TrinketRenderer.translateToLeftLeg(matrices, (PlayerEntityModel<AbstractClientPlayerEntity>)contextModel, player);
			matrices.multiply(Quaternion.fromEulerYxz(0, 0, 3.125f));
			render.renderItem(stack2, ModelTransformation.Mode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);
			matrices.pop();
			matrices.push();
			TrinketRenderer.translateToRightLeg(matrices, (PlayerEntityModel<AbstractClientPlayerEntity>)contextModel, player);
			matrices.multiply(Quaternion.fromEulerYxz(0, 0, 3.125f));
			render.renderItem(stack2, ModelTransformation.Mode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);
			matrices.pop();
		}
	}
}
