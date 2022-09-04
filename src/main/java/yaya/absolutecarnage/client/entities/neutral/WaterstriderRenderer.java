package yaya.absolutecarnage.client.entities.neutral;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.WaterstriderEntity;

public class WaterstriderRenderer extends GeoEntityRenderer<WaterstriderEntity>
{
	public WaterstriderRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx, new WaterstriderModel());
	}
	
	@Override
	public Identifier getTextureResource(WaterstriderEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/waterstrider.png");
	}
}
