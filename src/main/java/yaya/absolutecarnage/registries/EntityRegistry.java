package yaya.absolutecarnage.registries;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.client.entities.projectile.ToxicSpit;
import yaya.absolutecarnage.entities.ChomperEntity;
import yaya.absolutecarnage.entities.ChompyEntity;
import yaya.absolutecarnage.entities.SwarmCluster;

public class EntityRegistry
{
	public static final EntityType<ChompyEntity> CHOMPY = Registry.register(Registry.ENTITY_TYPE,
			new Identifier(AbsoluteCarnage.MOD_ID, "chompy"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ChompyEntity::new)
					.dimensions(EntityDimensions.fixed(0.7f, 1f)).build());
	public static final EntityType<ChomperEntity> CHOMPER = Registry.register(Registry.ENTITY_TYPE,
			new Identifier(AbsoluteCarnage.MOD_ID, "chomper"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ChomperEntity::new)
					.dimensions(EntityDimensions.fixed(1.75f, 2f)).build());
	public static final EntityType<ToxicSpit> TOXIC_SPIT = Registry.register(Registry.ENTITY_TYPE,
			new Identifier(AbsoluteCarnage.MOD_ID, "toxic_spit"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, ToxicSpit::new)
					.dimensions(EntityDimensions.fixed(0.25F, 0.25F))
					.trackRangeBlocks(4).trackedUpdateRate(10).build());
	public static final EntityType<SwarmCluster> SWARM_CLUSTER = Registry.register(Registry.ENTITY_TYPE,
			new Identifier(AbsoluteCarnage.MOD_ID, "swarm_cluster"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SwarmCluster::new)
					.dimensions(EntityDimensions.fixed(0.75f, 1f)).build());
	
	public static void registerAttributes()
	{
		FabricDefaultAttributeRegistry.register(CHOMPY, ChompyEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(CHOMPER, ChomperEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(SWARM_CLUSTER, SwarmCluster.setAttributes());
	}
}
