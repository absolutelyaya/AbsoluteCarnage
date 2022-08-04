package yaya.absolutecarnage.client.entities.block;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.blocks.InfestedChestBlockEntity;

public class InfestedChestModel extends AnimatedGeoModel<InfestedChestBlockEntity>
{
	@Override
	public Identifier getModelResource(InfestedChestBlockEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "geo/entities/block/infested_chest.geo.json");
	}
	
	@Override
	public Identifier getTextureResource(InfestedChestBlockEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/block/infested_chest.png");
	}
	
	@Override
	public Identifier getAnimationResource(InfestedChestBlockEntity animatable)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "animations/entities/block/infested_chest.animation.json");
	}
}
