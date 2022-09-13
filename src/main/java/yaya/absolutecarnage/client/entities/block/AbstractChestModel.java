package yaya.absolutecarnage.client.entities.block;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.blocks.AbstractChestBlockEntity;

public class AbstractChestModel extends AnimatedGeoModel<AbstractChestBlockEntity>
{
	//TODO: Add support for double chests
	//TODO: Make actual assets for ornate sandstone chest
	
	@Override
	public Identifier getModelResource(AbstractChestBlockEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "geo/entities/block/" + object.id + ".geo.json");
	}
	
	@Override
	public Identifier getTextureResource(AbstractChestBlockEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/block/" + object.id + ".png");
	}
	
	@Override
	public Identifier getAnimationResource(AbstractChestBlockEntity animatable)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "animations/entities/block/" + animatable.id + ".animation.json");
	}
}
