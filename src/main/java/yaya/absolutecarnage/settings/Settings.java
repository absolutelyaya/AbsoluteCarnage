package yaya.absolutecarnage.settings;

import net.minecraft.text.MutableText;
import yaya.absolutecarnage.utility.TranslationUtil;
import yaya.yayconfig.settings.BooleanSetting;
import yaya.yayconfig.settings.SettingsCategory;
import yaya.yayconfig.settings.SliderSetting;

import java.util.List;

public class Settings extends yaya.yayconfig.settings.Settings
{
	public Settings(Class<? extends SettingsCategory> category)
	{
		super(category, false);
	}
	
	//Particles
	public static final BooleanSetting SURFACEALIGNED_RENDERMODE =
			new BooleanSetting("particle.sa-rendermode", true, "surface-aligned",
					"options.graphics.fancy", "options.graphics.fast", true);
	public static final BooleanSetting SURFACEALIGNED_EDGEWRAP =
			new BooleanSetting("particle.sa-edgewrap", true, "surface-aligned", true);
	public static final SliderSetting GOOP_MAXSIZE =
			new SliderSetting("particle.goop-maxsize", 4.0, 1.0, 10.0, 0.1f, 1, "", true);
	//Developer
	public static final BooleanSetting DEBUG_SURFACEALIGNED_PARTICLE =
			new BooleanSetting("debug.surface-aligned", false, "", true);
	
	static
	{
		SETTINGS.put(Category.GENERAL, List.of());
		SETTINGS.put(Category.PARTICLES, List.of(SURFACEALIGNED_RENDERMODE, SURFACEALIGNED_EDGEWRAP, GOOP_MAXSIZE));
		SETTINGS.put(Category.DEVELOPER, List.of(DEBUG_SURFACEALIGNED_PARTICLE));
	}
	
	public enum Category implements yaya.yayconfig.settings.SettingsCategory
	{
		GENERAL("main"),
		PARTICLES("particles"),
		DEVELOPER("dev");
		
		private final String title;
		
		Category(String title)
		{
			this.title = title;
		}
		
		@Override
		public MutableText getTitle()
		{
			return TranslationUtil.getText("screen", String.format("options.%s.title", title)).copy();
		}
		
		@Override
		public SettingsCategory[] getValues()
		{
			return new SettingsCategory[0];
		}
	}
}
