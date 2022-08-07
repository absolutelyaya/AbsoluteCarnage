package yaya.absolutecarnage.items.trinkets;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import yaya.absolutecarnage.AbsoluteCarnage;

import java.util.List;
import java.util.Map;

public class WingTrinketItem extends TrinketItem implements TrinketRenderer
{
	final Identifier texture;
	final int wingCount;
	final Vec2f pos, size, texSize;
	
	int tick, flapAnim;
	float lastWingRot;
	final float power;
	
	public WingTrinketItem(Settings settings, float power, String texture, int wingCount, Vec2f pos, Vec2f size, Vec2f texSize)
	{
		super(settings);
		this.power = power;
		this.texture = new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/trinket/" + texture + ".png");
		this.wingCount = wingCount;
		this.pos = pos;
		this.size = size;
		this.texSize = texSize;
	}
	
	public float getPower()
	{
		return power;
	}
	
	float getDestRot(float delta)
	{
		return MathHelper.lerp(delta, lastWingRot, flapAnim - tick > 0 ? 0.25f : (-0.5f - (float)Math.sin(tick / 20f) * 0.25f));
	}
	
	public void onUse()
	{
		flapAnim = tick + 40;
	}
	
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		tooltip.add(Text.of(I18n.translate("item.absolute_carnage.wings.desc", getPower())));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch)
	{
		if(entity instanceof AbstractClientPlayerEntity player)
		{
			lastWingRot = getDestRot(tickDelta);
			tick++;
			VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(texture));
			matrices.push();
			
			TrinketRenderer.translateToChest(matrices, (PlayerEntityModel<AbstractClientPlayerEntity>)contextModel, player);
			matrices.translate(0, -9.5 / 16, 0.2);
			
			for (int i = 0; i < wingCount; i++)
			{
				//Todo: slightly rotate on Z axis per pair of wings
				matrices.push();
				int d = i % 2 == 0 ? -1 : 1;
				matrices.translate(d / 16f, 0, 0);
				ModelPart left = new ModelPart(List.of(new ModelPart.Cuboid(0, 0, d * pos.x, pos.y, 0,
						d * size.x, size.y, 0, 0, 0, 0, false, texSize.x, texSize.y)), Map.of());
				matrices.multiply(Quaternion.fromEulerYxz(d * lastWingRot, 0.25f, 0));
				left.render(matrices, consumer, light, 1);
				matrices.pop();
			}
			matrices.pop();
		}
	}
}
