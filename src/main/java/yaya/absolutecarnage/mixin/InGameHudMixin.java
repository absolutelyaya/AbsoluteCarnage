package yaya.absolutecarnage.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.registries.StatusEffectRegistry;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin
{
	private static final Identifier COCOON_OVERLAY = new Identifier(AbsoluteCarnage.MOD_ID, "textures/misc/cocoon_outline.png");
	
	@Shadow @Final private MinecraftClient client;
	
	@Shadow protected abstract void renderOverlay(Identifier texture, float opacity);
	
	@SuppressWarnings("ConstantConditions")
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getFrozenTicks()I"))
	void render(MatrixStack matrices, float tickDelta, CallbackInfo ci)
	{
		if(client.player != null && client.player.hasStatusEffect(StatusEffectRegistry.WEBBED))
		{
			StatusEffectInstance webbedInstance = client.player.getStatusEffect(StatusEffectRegistry.WEBBED);
			float opacity = Math.min(1f, webbedInstance.getDuration() / 400f);
			renderOverlay(COCOON_OVERLAY, opacity);
		}
	}
}
