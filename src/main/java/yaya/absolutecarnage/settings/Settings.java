package yaya.absolutecarnage.settings;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import yaya.yayconfig.settings.BooleanSetting;
import yaya.yayconfig.settings.ChoiceSetting;
import yaya.yayconfig.settings.SettingsCategory;
import yaya.yayconfig.settings.SliderSetting;

import java.util.List;

public class Settings extends yaya.yayconfig.settings.Settings
{
	public Settings(Class<? extends SettingsCategory> category)
	{
		super(category);
	}
	
	//Particles
	public static final ChoiceSetting SURFACEALIGNED_RENDERMODE = new ChoiceSetting("particle.sa-rendermode", List.of("Fancy", "Fast"), true);
	public static final BooleanSetting SURFACEALIGNED_EDGEWRAP = new BooleanSetting("particle.sa-edgewrap", true, true);
	public static final SliderSetting GOOP_MAXSIZE = new SliderSetting("particle.goop-maxsize", 4.0, 1.0, 10.0, 0.1f, true);
	//Developer
	public static final BooleanSetting DEBUG_SURFACEALIGNED_PARTICLE = new BooleanSetting("debug.surface-aligned", false, true);
	
	static
	{
		SETTINGS.put(Category.GENERAL, List.of());
		SETTINGS.put(Category.PARTICLES, List.of(SURFACEALIGNED_RENDERMODE, SURFACEALIGNED_EDGEWRAP, GOOP_MAXSIZE));
		SETTINGS.put(Category.DEVELOPER, List.of(DEBUG_SURFACEALIGNED_PARTICLE));
	}
	
	public enum Category implements yaya.yayconfig.settings.SettingsCategory
	{
		GENERAL(Text.translatable("screen.absolute_carnage.options.main.title")),
		PARTICLES(Text.translatable("screen.absolute_carnage.options.particles.title")),
		DEVELOPER(Text.translatable("screen.absolute_carnage.options.dev.title"));
		
		private final Text title;
		
		Category(Text title)
		{
			this.title = title;
		}
		
		@Override
		public MutableText getTitle()
		{
			return title.copy();
		}
		
		@Override
		public SettingsCategory[] getValues()
		{
			return new SettingsCategory[0];
		}
	}
}
