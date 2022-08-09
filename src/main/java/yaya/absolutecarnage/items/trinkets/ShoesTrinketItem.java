package yaya.absolutecarnage.items.trinkets;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
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
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtInt;
import net.minecraft.util.math.Quaternion;

import java.util.UUID;

public class ShoesTrinketItem extends TrinketItem implements TrinketRenderer
{
	public ShoesTrinketItem(Settings settings)
	{
		super(settings);
	}
	
	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid)
	{
		//TODO: move to constructor or make it a builder
		Multimap<EntityAttribute, EntityAttributeModifier> map = super.getModifiers(stack, slot, entity, uuid);
		map.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
				new EntityAttributeModifier(UUID.randomUUID().toString(), 0.2, EntityAttributeModifier.Operation.ADDITION));
		return map;
	}
	
	//TODO: Add a system for adding lore text
	//TODO: Add lore text to setae shoes that explains their web properties
	
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
