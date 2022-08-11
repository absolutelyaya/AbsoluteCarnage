package yaya.absolutecarnage.items.trinkets;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CarnageTrinket extends TrinketItem implements TrinketRenderer
{
	protected final List<String> lore;
	
	public CarnageTrinket(Settings settings, String... lore)
	{
		super(settings);
		this.lore = Arrays.stream(lore).toList();
	}
	
	@Override
	public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch)
	{
	
	}
	
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		lore.forEach(line ->
		{
			if(!line.startsWith("e#"))
				tooltip.add(Text.translatable(line));
		});
		if(lore.stream().anyMatch(line -> line.startsWith("e#")) && context.isAdvanced())
		{
			tooltip.add(Text.empty());
			lore.forEach(line ->
			{
				if(line.startsWith("e#"))
					tooltip.add(Text.translatable(line.replace("e#", "")));
			});
			tooltip.add(Text.empty());
		}
	}
}
