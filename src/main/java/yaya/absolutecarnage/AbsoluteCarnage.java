package yaya.absolutecarnage;

import yaya.yayconfig.settings.SettingsManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.tinyremapper.extension.mixin.common.Logger;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.GeckoLib;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.TerraBlenderApi;
import yaya.absolutecarnage.biomes.SurfaceRuleData;
import yaya.absolutecarnage.biomes.TestRegion;
import yaya.absolutecarnage.registries.*;
import yaya.absolutecarnage.networking.ModPackets;
import yaya.absolutecarnage.registries.EntityRegistry;
import yaya.absolutecarnage.registries.ItemRegistry;
import yaya.absolutecarnage.settings.Settings;
import yaya.yayconfig.utilities.TranslationUtil;

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
		BlockEntityRegistry.registerBlockEntities();
		ParticleRegistry.registerParticles();
		StatRegistry.registerStats();
		StatusEffectRegistry.registerEffects();
		
		ModPackets.registerC2SPackets();
		
		GeckoLib.initialize();
		
		PlacedFeatureRegistry.registerPlacedFeatures();
		
		TranslationUtil.setActiveGroupID(MOD_ID);
		SettingsManager.init(MOD_ID);
		new Settings(Settings.Category.class);
		SettingsManager.load();
	}
	
	@Override
	public void onTerraBlenderInitialized()
	{
		Regions.register(new TestRegion(new Identifier(MOD_ID, "test_region"), 4));
		
		SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MOD_ID, SurfaceRuleData.makeRules());
	}
}
