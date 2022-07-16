package yaya.absolutecarnage.registries;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.ChompyEntity;

public class EntityRegistry
{
	public static final EntityType<ChompyEntity> CHOMPY = Registry.register(Registry.ENTITY_TYPE,
			new Identifier(AbsoluteCarnage.MOD_ID, "chompy"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ChompyEntity::new)
					.dimensions(EntityDimensions.fixed(0.7f, 1f)).build());
	
	public static void registerAttributes()
	{
		FabricDefaultAttributeRegistry.register(CHOMPY, ChompyEntity.setAttributes());
	}
}
