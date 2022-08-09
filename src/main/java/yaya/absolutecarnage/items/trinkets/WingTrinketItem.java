package yaya.absolutecarnage.items.trinkets;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.client.tutorial.CarnageTutorialManager;
import yaya.absolutecarnage.client.tutorial.CarnageTutorialToast;
import yaya.absolutecarnage.registries.StatRegistry;

import java.util.List;
import java.util.Map;

public class WingTrinketItem extends TrinketItem implements TrinketRenderer
{
	final Identifier texture;
	final int wingCount;
	final Vec2f pos, size, texSize;
	
	int tick, flapAnim, lastDir;
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
		float animRot = lastDir == 0 ? -1f : 0.25f;
		int animTick = flapAnim - tick;
		if(animTick <= 0 && animTick > -60)
			delta /= 10;
		return MathHelper.lerp(delta, lastWingRot, animTick > 0 ? animRot : (-0.5f - (float)Math.sin(tick / 20f) * 0.25f));
	}
	
	public void onUse(PlayerEntity user, int dir)
	{
		flapAnim = tick + 30;
		lastDir = dir;
		CarnageTutorialManager.getInstance().addProgress("wings");
	}
	
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		
		tooltip.add(Text.of(I18n.translate("item.absolute_carnage.wings.desc", getPower())));
	}
	
	@Override
	public void tick(ItemStack stack, SlotReference slot, LivingEntity entity)
	{
		tick++;
		if(flapAnim - tick > 0 && !entity.isOnGround())
		{
			Vec3d pos = entity.getPos();
			Vec3d mov = entity.getVelocity().normalize().multiply(0.25);
			Random random = entity.getRandom();
			pos = pos.add(random.nextFloat() * 0.5 - 0.25, random.nextFloat() * 2, random.nextFloat() * 0.5 - 0.25);
			entity.world.addParticle(ParticleTypes.CLOUD, pos.x - mov.x, pos.y, pos.z - mov.z, -mov.x, 0, -mov.z);
		}
	}
	
	@Override
	public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity)
	{
		if(entity instanceof PlayerEntity)
		{
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			if(player != null)
			{
				StatHandler handler = player.getStatHandler();
				if(handler.getStat(Stats.CUSTOM.getOrCreateStat(StatRegistry.DODGE)) == 0)
					CarnageTutorialManager.getInstance().startTutorial("wings");
			}
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch)
	{
		if(entity instanceof AbstractClientPlayerEntity player)
		{
			lastWingRot = getDestRot(tickDelta);
			VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(texture));
			matrices.push();
			
			TrinketRenderer.translateToChest(matrices, (PlayerEntityModel<AbstractClientPlayerEntity>)contextModel, player);
			matrices.translate(0, -9.5 / 16, 0.3);
			matrices.multiply(Quaternion.fromEulerYxz(0, -0.2f, 0));
			
			for (int i = 0; i < wingCount; i++)
			{
				int pair = i / 2;
				if(wingCount > 2)
					pair--;
				int d = i % 2 == 0 ? -1 : 1;
				matrices.push();
				matrices.translate(d / 16f, 0, 0);
				ModelPart left = new ModelPart(List.of(new ModelPart.Cuboid(0, (-pair - 2) * (int)(texSize.y / (wingCount / 2)), d * pos.x, pos.y, 0,
						d * size.x, size.y, 0, 0, 0, 0, false, texSize.x,  texSize.y)), Map.of());
				matrices.multiply(Quaternion.fromEulerYxz(d * lastWingRot + (pair * d * 0.1f), 0.25f, pair * d * -0.45f));
				left.render(matrices, consumer, light, 1);
				matrices.pop();
			}
			matrices.pop();
		}
	}
}
