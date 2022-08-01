package yaya.absolutecarnage;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.tinyremapper.extension.mixin.common.Logger;
import software.bernie.geckolib3.GeckoLib;
import yaya.absolutecarnage.networking.ModPackets;
import yaya.absolutecarnage.registries.EntityRegistry;
import yaya.absolutecarnage.registries.ItemRegistry;

public class AbsoluteCarnage implements ModInitializer
{
	public static Logger LOGGER;
	public static String MOD_ID = "absolute_carnage";
	
	@Override
	public void onInitialize()
	{
		EntityRegistry.registerAttributes();
		ItemRegistry.registerItems();
		
		ModPackets.registerC2SPackets();
		
		GeckoLib.initialize();
	}
}
