package yaya.absolutecarnage.client.items.block;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.items.AnimatedBlockItem;

public class AnimatedBlockItemModel extends AnimatedGeoModel<AnimatedBlockItem>
{
	@Override
	public Identifier getModelResource(AnimatedBlockItem object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "geo/entities/block/infested_chest.geo.json");
	}
	
	@Override
	public Identifier getTextureResource(AnimatedBlockItem object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/block/infested_chest.png");
	}
	
	@Override
	public Identifier getAnimationResource(AnimatedBlockItem animatable)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "animations/entities/block/infested_chest.animation.json");
	}
}
