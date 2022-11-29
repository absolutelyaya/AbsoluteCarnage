package yaya.absolutecarnage.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yaya.absolutecarnage.registries.StatusEffectRegistry;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T>
{
	@Shadow @Final public ModelPart rightArm;
	
	@Shadow @Final public ModelPart leftArm;
	
	@Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;body:Lnet/minecraft/client/model/ModelPart;"), cancellable = true)
	public void setAngles(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci)
	{
		if(livingEntity.hasStatusEffect(StatusEffectRegistry.WEBBED))
		{
			rightArm.setAngles(0f, 0f, 0f);
			leftArm.setAngles(0f, 0f, 0f);
			leftArm.pivotY = 2f;
			rightArm.pivotY = 2f;
			ci.cancel();
		}
	}
}
