package yaya.absolutecarnage.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yaya.absolutecarnage.entities.CarnageEntityAccessor;
import yaya.absolutecarnage.registries.BlockTagRegistry;

@Mixin(LivingEntity.class)
public abstract class DrowningMixin extends Entity implements CarnageEntityAccessor
{
	@Shadow public abstract void baseTick();
	
	public boolean drowning;
	
	public DrowningMixin(EntityType<?> type, World world)
	{
		super(type, world);
	}
	
	//This could potentially cause conflicts, but it's the only way I know how to make things drown in anything other than water.
	@Redirect(method = "baseTick", at = @At(target = "Lnet/minecraft/entity/LivingEntity;isSubmergedIn(Lnet/minecraft/tag/TagKey;)Z", value = "INVOKE"))
	public boolean shouldDrown(LivingEntity instance, TagKey<Fluid> tagKey)
	{
		double d = instance.getEyeY();
		BlockPos blockPos = new BlockPos(this.getX(), d, this.getZ());
		if(instance.world.getBlockState(blockPos).isIn(BlockTagRegistry.DROWN) || drowning)
			return true;
		return instance.isSubmergedIn(tagKey);
	}
	//TODO: Make this a mixin to baseTick instead of a redirect. Maybe that's better??? Probably not.
	
	public void setDrowning(boolean drowning)
	{
		this.drowning = drowning;
	}
	
	public boolean isDrowning()
	{
		return drowning;
	}
}
