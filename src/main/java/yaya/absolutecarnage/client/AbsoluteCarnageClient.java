package yaya.absolutecarnage.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.client.entities.agressive.ChomperRenderer;
import yaya.absolutecarnage.client.entities.agressive.SwarmlingWarriorRenderer;
import yaya.absolutecarnage.client.entities.agressive.SwarmlingSpawnRenderer;
import yaya.absolutecarnage.client.entities.block.AbstractChestRenderer;
import yaya.absolutecarnage.client.entities.feature.CocoonModel;
import yaya.absolutecarnage.client.entities.neutral.SwarmlingWeaverRenderer;
import yaya.absolutecarnage.client.entities.neutral.WaterstriderRenderer;
import yaya.absolutecarnage.client.entities.other.CarnagePaintingRenderer;
import yaya.absolutecarnage.client.entities.other.SwarmClusterRenderer;
import yaya.absolutecarnage.event.KeyInputHandler;
import yaya.absolutecarnage.particles.GoopDropParticle;
import yaya.absolutecarnage.particles.GoopParticle;
import yaya.absolutecarnage.particles.GoopStringParticle;
import yaya.absolutecarnage.registries.EntityRegistry;
import yaya.absolutecarnage.client.items.block.AnimatedBlockItemRenderer;
import yaya.absolutecarnage.particles.FliesParticle;
import yaya.absolutecarnage.registries.*;
import yaya.absolutecarnage.client.entities.neutral.ChompyRenderer;

@Environment(EnvType.CLIENT)
public class AbsoluteCarnageClient implements ClientModInitializer
{
	public static final EntityModelLayer COCOON_LAYER = new EntityModelLayer(new Identifier(AbsoluteCarnage.MOD_ID, "cocoon"), "main");
	@Override
	public void onInitializeClient()
	{
		//Entity renderers
		EntityRendererRegistry.register(EntityRegistry.CHOMPY, ChompyRenderer::new);
		EntityRendererRegistry.register(EntityRegistry.CHOMPER, ChomperRenderer::new);
		EntityRendererRegistry.register(EntityRegistry.TOXIC_SPIT, FlyingItemEntityRenderer::new);
		EntityRendererRegistry.register(EntityRegistry.FLAME_PROJECTILE, FlyingItemEntityRenderer::new);
		EntityRendererRegistry.register(EntityRegistry.WEB_PROJECTILE, FlyingItemEntityRenderer::new);
		EntityRendererRegistry.register(EntityRegistry.WATERSTRIDER, WaterstriderRenderer::new);
		EntityRendererRegistry.register(EntityRegistry.SWARM_CLUSTER, SwarmClusterRenderer::new);
		EntityRendererRegistry.register(EntityRegistry.SWARMLING_SPAWN, SwarmlingSpawnRenderer::new);
		EntityRendererRegistry.register(EntityRegistry.SWARMLING_WARRIOR, SwarmlingWarriorRenderer::new);
		EntityRendererRegistry.register(EntityRegistry.SWARMLING_WEAVER, SwarmlingWeaverRenderer::new);
		EntityRendererRegistry.register(EntityRegistry.CARNAGE_PAINTING, CarnagePaintingRenderer::new);
		//BlockEntity + Item renderers
		BlockEntityRendererRegistry.register(BlockEntityRegistry.INFESTED_CHEST, AbstractChestRenderer::new);
		GeoItemRenderer.registerItemRenderer(ItemRegistry.INFESTED_CHEST, new AnimatedBlockItemRenderer());
		BlockEntityRendererRegistry.register(BlockEntityRegistry.ORNATE_SANDSTONE_CHEST, AbstractChestRenderer::new);
		GeoItemRenderer.registerItemRenderer(ItemRegistry.ORNATE_SANDSTONE_CHEST, new AnimatedBlockItemRenderer());
		//Block render layers
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockRegistry.HANGING_WEB, BlockRegistry.FLOOR_WEB_DECAL,
				BlockRegistry.WALL_WEB_DECAL, BlockRegistry.DANGLING_EGG);
		//Entity model layers
		EntityModelLayerRegistry.registerModelLayer(COCOON_LAYER, CocoonModel::getTexturedModelData);
		//Particles
		ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
		registry.register(ParticleRegistry.FLIES, FliesParticle.FliesParticleFactory::new);
		registry.register(ParticleRegistry.GOOP_DROP, GoopDropParticle.GoopDropParticleFactory::new);
		registry.register(ParticleRegistry.GOOP, GoopParticle.GoopParticleFactory::new);
		registry.register(ParticleRegistry.GOOP_STRING, GoopStringParticle.GoopStringParticleFactory::new);
		//Model predicates
		ModelPredicateRegistry.registerModels();
		
		KeyInputHandler.register();
	}
}
