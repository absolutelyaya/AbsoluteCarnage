package yaya.absolutecarnage.client.entities.neutral;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.custom.ChompyEntity;

public class ChompyRenderer extends GeoEntityRenderer<ChompyEntity>
{
	public ChompyRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx, new ChompyModel());
	}
	
	@Override
	public Identifier getTextureResource(ChompyEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/chompy.png");
	}
}
