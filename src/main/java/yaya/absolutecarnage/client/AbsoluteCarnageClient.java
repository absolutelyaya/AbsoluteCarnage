package yaya.absolutecarnage.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import yaya.absolutecarnage.client.entities.agressive.ChomperRenderer;
import yaya.absolutecarnage.client.entities.block.InfestedChestRenderer;
import yaya.absolutecarnage.client.entities.other.SwarmClusterRenderer;
import yaya.absolutecarnage.event.KeyInputHandler;
import yaya.absolutecarnage.registries.EntityRegistry;
import yaya.absolutecarnage.client.items.block.AnimatedBlockItemRenderer;
import yaya.absolutecarnage.particles.FliesParticle;
import yaya.absolutecarnage.registries.*;
import yaya.absolutecarnage.client.entities.neutral.ChompyRenderer;

@Environment(EnvType.CLIENT)
public class AbsoluteCarnageClient implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		//Entity renderers
		EntityRendererRegistry.register(EntityRegistry.CHOMPY, ChompyRenderer::new);
		EntityRendererRegistry.register(EntityRegistry.CHOMPER, ChomperRenderer::new);
		EntityRendererRegistry.register(EntityRegistry.TOXIC_SPIT, FlyingItemEntityRenderer::new);
		EntityRendererRegistry.register(EntityRegistry.SWARM_CLUSTER, SwarmClusterRenderer::new);
		EntityRendererRegistry.register(EntityRegistry.FLAME_PROJECTILE, FlyingItemEntityRenderer::new);
		//BlockEntity + Item renderers
		BlockEntityRendererRegistry.register(BlockEntityRegistry.INFESTED_CHEST, InfestedChestRenderer::new);
		GeoItemRenderer.registerItemRenderer(ItemRegistry.INFESTED_CHEST, new AnimatedBlockItemRenderer());
		//Block render layers
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockRegistry.HANGING_WEB);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockRegistry.FLOOR_WEB_DECAL);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockRegistry.WALL_WEB_DECAL);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockRegistry.DANGLING_EGG);
		//Particles
		ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
		registry.register(ParticleRegistry.FLIES, FliesParticle.FliesParticleFactory::new);
		//Model predicates
		ModelPredicateRegistry.registerModels();
		
		KeyInputHandler.register();
	}
}
