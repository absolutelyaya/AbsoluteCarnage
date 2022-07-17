package yaya.absolutecarnage.client.entities.agressive;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.entities.ChomperEntity;

public class ChomperRenderer extends GeoEntityRenderer<ChomperEntity>
{
	public ChomperRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx, new ChomperModel());
	}
	
	@Override
	public Identifier getTextureResource(ChomperEntity object)
	{
		return new Identifier(AbsoluteCarnage.MOD_ID, "textures/entities/chomper.png");
	}
}
