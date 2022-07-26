package yaya.absolutecarnage;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.tinyremapper.extension.mixin.common.Logger;
import net.minecraft.particle.ParticleType;
import software.bernie.geckolib3.GeckoLib;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.TerraBlenderApi;
import yaya.absolutecarnage.biomes.SurfaceRuleData;
import yaya.absolutecarnage.registries.*;

public class AbsoluteCarnage implements ModInitializer, TerraBlenderApi
{
	public static Logger LOGGER;
	public static String MOD_ID = "absolute_carnage";
	
	@Override
	public void onInitialize()
	{
		EntityRegistry.registerAttributes();
		ItemRegistry.registerItems();
		BlockRegistry.registerBlocks();
		ParticleRegistry.registerParticles();
		
		GeckoLib.initialize();
		
		PlacedFeatureRegistry.registerPlacedFeatures();
	}
	
	@Override
	public void onTerraBlenderInitialized()
	{
		SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MOD_ID, SurfaceRuleData.makeRules());
	}
}
