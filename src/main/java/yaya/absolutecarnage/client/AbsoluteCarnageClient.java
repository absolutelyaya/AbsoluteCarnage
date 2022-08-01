package yaya.absolutecarnage.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import yaya.absolutecarnage.client.entities.agressive.ChomperRenderer;
import yaya.absolutecarnage.client.entities.other.SwarmClusterRenderer;
import yaya.absolutecarnage.event.KeyInputHandler;
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
		
		KeyInputHandler.register();
	}
}
