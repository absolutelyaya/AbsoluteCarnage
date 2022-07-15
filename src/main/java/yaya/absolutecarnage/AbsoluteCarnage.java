package yaya.absolutecarnage;

import net.fabricmc.api.ModInitializer;
import software.bernie.geckolib3.GeckoLib;
import yaya.absolutecarnage.entities.EntityRegistry;

public class AbsoluteCarnage implements ModInitializer
{
	public static String MOD_ID = "absolute_carnage";
	
	@Override
	public void onInitialize()
	{
		EntityRegistry.registerAttributes();
		
		GeckoLib.initialize();
	}
}
