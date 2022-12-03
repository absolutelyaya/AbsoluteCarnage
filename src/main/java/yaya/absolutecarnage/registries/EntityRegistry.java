package yaya.absolutecarnage.registries;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.*;
import yaya.absolutecarnage.entities.projectile.FlameProjectile;
import yaya.absolutecarnage.entities.projectile.ToxicSpit;
import yaya.absolutecarnage.entities.projectile.WebProjectile;

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
	public static final EntityType<WebProjectile> WEB_PROJECTILE = Registry.register(Registry.ENTITY_TYPE,
			new Identifier(AbsoluteCarnage.MOD_ID, "web_projectile"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, WebProjectile::new).disableSummon()
					.dimensions(EntityDimensions.fixed(0.25f, 0.25f))
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
					.dimensions(EntityDimensions.fixed(0.75f, 0.25f))
					.spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, SwarmlingSpawnEntity::canSpawn)
					.build());
	public static final EntityType<SwarmlingWarriorEntity> SWARMLING_WARRIOR = Registry.register(Registry.ENTITY_TYPE,
			new Identifier(AbsoluteCarnage.MOD_ID, "swarmling_warrior"),
			FabricEntityTypeBuilder.createMob().spawnGroup(SpawnGroup.MONSTER).entityFactory(SwarmlingWarriorEntity::new)
					.dimensions(EntityDimensions.fixed(1.5f, 1.0f))
					.spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, SwarmlingWarriorEntity::canSpawn)
					.build());
	public static final EntityType<SwarmlingWeaverEntity> SWARMLING_WEAVER = Registry.register(Registry.ENTITY_TYPE,
			new Identifier(AbsoluteCarnage.MOD_ID, "swarmling_weaver"),
			FabricEntityTypeBuilder.createMob().spawnGroup(SpawnGroup.MONSTER).entityFactory(SwarmlingWeaverEntity::new)
					.dimensions(EntityDimensions.fixed(1.5f, 1.0f))
					.spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, SwarmlingWeaverEntity::canSpawn)
					.build());
	public static final EntityType<WaterstriderEntity> WATERSTRIDER = Registry.register(Registry.ENTITY_TYPE,
			new Identifier(AbsoluteCarnage.MOD_ID, "waterstrider"),
			FabricEntityTypeBuilder.createMob().spawnGroup(SpawnGroup.AMBIENT).entityFactory(WaterstriderEntity::new)
					.dimensions(EntityDimensions.fixed(2f, 0.5f))
					.spawnRestriction(SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING, WaterstriderEntity::canSpawn)
					.build());
	
	public static final EntityType<CarnagePaintingEntity> CARNAGE_PAINTING = Registry.register(Registry.ENTITY_TYPE,
			new Identifier(AbsoluteCarnage.MOD_ID, "carnage_painting"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, CarnagePaintingEntity::new).build());
	
	@SuppressWarnings("ConstantConditions")
	public static void registerAttributes()
	{
		FabricDefaultAttributeRegistry.register(CHOMPY, ChompyEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(CHOMPER, ChomperEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(SWARM_CLUSTER, SwarmClusterEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(SWARMLING_SPAWN, SwarmlingSpawnEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(SWARMLING_WARRIOR, SwarmlingWarriorEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(SWARMLING_WEAVER, SwarmlingWeaverEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(WATERSTRIDER, WaterstriderEntity.setAttributes());
	}
}
