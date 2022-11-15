package yaya.absolutecarnage.mixin;

import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yaya.absolutecarnage.client.tutorial.CarnageTutorialManager;

@Mixin(ToastManager.class)
public class ToastManagerMixin
{
	@Inject(method = "draw", at = @At(value = "FIELD", target = "Lnet/minecraft/client/toast/ToastManager;visibleEntries:[Lnet/minecraft/client/toast/ToastManager$Entry;",
		ordinal = 2))
	public void draw(MatrixStack matrices, CallbackInfo ci)
	{
		CarnageTutorialManager.getInstance().OnRemove();
	}
}
