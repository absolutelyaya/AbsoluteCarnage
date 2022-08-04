package yaya.absolutecarnage.client.items.block;

import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import yaya.absolutecarnage.items.AnimatedBlockItem;

public class AnimatedBlockItemRenderer extends GeoItemRenderer<AnimatedBlockItem>
{
	public AnimatedBlockItemRenderer()
	{
		super(new AnimatedBlockItemModel());
	}
}
