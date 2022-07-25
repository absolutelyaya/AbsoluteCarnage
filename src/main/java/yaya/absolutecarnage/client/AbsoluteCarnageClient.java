package yaya.absolutecarnage.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import yaya.absolutecarnage.client.entities.agressive.ChomperRenderer;
import yaya.absolutecarnage.client.entities.other.SwarmClusterRenderer;
import yaya.absolutecarnage.registries.BlockRegistry;
import yaya.absolutecarnage.registries.EntityRegistry;
import yaya.absolutecarnage.client.entities.neutral.ChompyRenderer;

@Environment(EnvType.CLIENT)
public class AbsoluteCarnageClient implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		EntityRendererRegistry.register(EntityRegistry.CHOMPY, ChompyRenderer::new);
		EntityRendererRegistry.register(EntityRegistry.CHOMPER, ChomperRenderer::new);
		EntityRendererRegistry.register(EntityRegistry.TOXIC_SPIT, FlyingItemEntityRenderer::new);
		EntityRendererRegistry.register(EntityRegistry.SWARM_CLUSTER, SwarmClusterRenderer::new);
		EntityRendererRegistry.register(EntityRegistry.FLAME_PROJECTILE, FlyingItemEntityRenderer::new);
		
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockRegistry.HANGING_WEB);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockRegistry.FLOOR_WEB_DECAL);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockRegistry.WALL_WEB_DECAL);
	}
}
