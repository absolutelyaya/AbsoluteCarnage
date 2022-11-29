package yaya.absolutecarnage.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FogShape;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yaya.absolutecarnage.blocks.QuicksandBlock;
import yaya.absolutecarnage.registries.BlockRegistry;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin
{
	@Inject(method = "applyFog", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogStart(F)V"), cancellable = true)
	private static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci)
	{
		Entity entity = camera.getFocusedEntity();
		BlockPos bpos = camera.getBlockPos();
		BlockState state = entity.world.getBlockState(bpos);
		if(state.isOf(BlockRegistry.QUICKSAND) &&
				   ((QuicksandBlock)state.getBlock()).getOutline(state.get(QuicksandBlock.INDENT)).getBoundingBox()
						   .contains(camera.getPos().subtract(bpos.getX(), bpos.getY(), bpos.getZ())))
		{
			float start;
			float end;
			
			if (entity.isSpectator())
			{
				start = -8f;
				end = viewDistance * 0.5f;
			}
			else
			{
				start = 0f;
				end = 2f;
			}
			
			RenderSystem.setShaderFogColor(1f, 0.8f, 0.4f, 1f);
			RenderSystem.setShaderFogStart(start);
			RenderSystem.setShaderFogEnd(end);
			RenderSystem.setShaderFogShape(FogShape.SPHERE);
			ci.cancel();
		}
	}
}
