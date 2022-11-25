package yaya.absolutecarnage.registries;

import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import yaya.absolutecarnage.AbsoluteCarnage;

import java.util.HashMap;

public class PaintingRegistry
{
	public static final HashMap<String, PaintingVariant> Hyroglyphics = new HashMap<>() {
		{
			put("back", registerPainting("hyroglyphics/back", new PaintingVariant(16, 16)));
			put("monolith", registerPainting("hyroglyphics/monolith", new PaintingVariant(32, 32)));
		}
	};
	
	private static PaintingVariant registerPainting(String name, PaintingVariant variant)
	{
		return Registry.register(Registry.PAINTING_VARIANT, new Identifier(AbsoluteCarnage.MOD_ID, name), variant);
	}
	
	public static void registerPaintings()
	{
	
	}
}
