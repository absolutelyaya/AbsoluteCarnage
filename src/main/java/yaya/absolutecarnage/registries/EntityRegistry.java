package yaya.absolutecarnage.registries;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.SwarmClusterEntity;
import yaya.absolutecarnage.entities.SwarmlingSpawnEntity;
import yaya.absolutecarnage.entities.projectile.FlameProjectile;
import yaya.absolutecarnage.entities.projectile.ToxicSpit;
import yaya.absolutecarnage.entities.ChomperEntity;
import yaya.absolutecarnage.entities.ChompyEntity;

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
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, ToxicSpit::new).disableSummon()
					.dimensions(EntityDimensions.fixed(0.25F, 0.25F))
					.trackRangeBlocks(4).trackedUpdateRate(10).build());
	public static final EntityType<SwarmClusterEntity> SWARM_CLUSTER = Registry.register(Registry.ENTITY_TYPE,
			new Identifier(AbsoluteCarnage.MOD_ID, "swarm_cluster"),
			FabricEntityTypeBuilder.createMob().spawnGroup(SpawnGroup.MONSTER).entityFactory(SwarmClusterEntity::new)
					.dimensions(EntityDimensions.fixed(0.75f, 1f))
					.spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, SwarmClusterEntity::canSpawn)
					.build());
	public static final EntityType<FlameProjectile> FLAME_PROJECTILE = Registry.register(Registry.ENTITY_TYPE,
			new Identifier(AbsoluteCarnage.MOD_ID, "flame_projectile"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, FlameProjectile::new).disableSummon()
					.dimensions(EntityDimensions.changing(0.25F, 0.25F))
					.trackRangeBlocks(4).trackedUpdateRate(10).build());
	public static final EntityType<SwarmlingSpawnEntity> SWARMLING_SPAWN = Registry.register(Registry.ENTITY_TYPE,
			new Identifier(AbsoluteCarnage.MOD_ID, "swarmling_spawn"),
			FabricEntityTypeBuilder.createMob().spawnGroup(SpawnGroup.MONSTER).entityFactory(SwarmlingSpawnEntity::new)
					.dimensions(EntityDimensions.fixed(0.75f, 1f))
					.spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, SwarmlingSpawnEntity::canSpawn)
					.build());
	
	@SuppressWarnings("ConstantConditions")
	public static void registerAttributes()
	{
		FabricDefaultAttributeRegistry.register(CHOMPY, ChompyEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(CHOMPER, ChomperEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(SWARM_CLUSTER, SwarmClusterEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(SWARMLING_SPAWN, SwarmlingSpawnEntity.setAttributes());
	}
}
