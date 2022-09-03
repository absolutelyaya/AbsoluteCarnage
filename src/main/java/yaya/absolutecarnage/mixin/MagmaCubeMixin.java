package yaya.absolutecarnage.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MagmaCubeEntity.class)
public abstract class MagmaCubeMixin extends SlimeMixin
{
	public MagmaCubeMixin(EntityType<? extends SlimeEntity> entityType, World world)
	{
		super(entityType, world);
	}
	
	@Override
	public Vec3f getGoopColor()
	{
		return new Vec3f(0.9f, 0.35f, 0.05f);
	}
}
