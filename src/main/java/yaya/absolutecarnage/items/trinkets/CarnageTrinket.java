package yaya.absolutecarnage.items.trinkets;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CarnageTrinket extends TrinketItem implements TrinketRenderer
{
	protected final List<String> lore;
	private final List<AttributeBuilder.Attribute> attributes;
	
	public CarnageTrinket(Settings settings, String... lore)
	{
		this(settings, new AttributeBuilder(), lore);
	}
	
	public CarnageTrinket(Settings settings, AttributeBuilder attributes, String... lore)
	{
		super(settings);
		this.lore = Arrays.stream(lore).toList();
		this.attributes = attributes.build();
	}
	
	@Override
	public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch)
	{}
	
	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid)
	{
		Multimap<EntityAttribute, EntityAttributeModifier> map = super.getModifiers(stack, slot, entity, uuid);
		attributes.forEach(i -> map.put(i.attribute, i.modifier));
		return map;
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
	
	public static class AttributeBuilder
	{
		private final List<Attribute> attributes = new ArrayList<>();
		
		public List<Attribute> build()
		{
			return attributes;
		}
		
		public AttributeBuilder addAttribute(EntityAttribute attribute, double value, EntityAttributeModifier.Operation op)
		{
			attributes.add(new Attribute(attribute, new EntityAttributeModifier(UUID.randomUUID().toString(), value, op)));
			return this;
		}
		
		private record Attribute(EntityAttribute attribute, EntityAttributeModifier modifier)
		{
		
		}
	}
}
