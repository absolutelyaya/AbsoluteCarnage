package yaya.absolutecarnage.settings;

import net.minecraft.text.Text;
import yaya.yayconfig.settings.BooleanSetting;
import yaya.yayconfig.settings.SettingsCategory;

import java.util.List;

public class Settings extends yaya.yayconfig.settings.Settings
{
	public Settings(Class<? extends SettingsCategory> category)
	{
		super(category);
	}
	
	public static final BooleanSetting DEBUG_SURFACEALIGNED_PARTICLE = new BooleanSetting("debug.surface-aligned", false, true);
	
	static
	{
		SETTINGS.put(Category.GENERAL, List.of());
		SETTINGS.put(Category.DEVELOPER, List.of(DEBUG_SURFACEALIGNED_PARTICLE));
	}
	
	public enum Category implements yaya.yayconfig.settings.SettingsCategory
	{
		GENERAL(Text.translatable("screen.absolute_carnage.options.main.title")),
		DEVELOPER(Text.translatable("screen.absolute_carnage.options.dev.title"));
		
		private final Text title;
		
		Category(Text title)
		{
			this.title = title;
		}
		
		@Override
		public Text getTitle()
		{
			return this.title;
		}
		
		@Override
		public SettingsCategory[] getValues()
		{
			return new SettingsCategory[0];
		}
	}
}
