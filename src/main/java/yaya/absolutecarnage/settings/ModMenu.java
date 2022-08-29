package yaya.absolutecarnage.settings;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import yaya.absolutecarnage.screens.MainSettingsScreen;

public class ModMenu implements ModMenuApi
{
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory()
	{
		return MainSettingsScreen::new;
	}
}
