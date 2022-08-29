package yaya.absolutecarnage.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import yaya.absolutecarnage.settings.Settings;
import yaya.yayconfig.screens.settings.AbstractSettingsScreen;
import yaya.yayconfig.screens.settings.SettingsScreen;

import java.util.Arrays;

public class MainSettingsScreen extends AbstractSettingsScreen
{
	public MainSettingsScreen(Screen parent)
	{
		super(Text.translatable("screen.absolute_carnage.options.main.title"), parent);
	}
	
	@Override
	protected void init()
	{
		super.init();
		
		for (int i = 1; i < Settings.Category.values().length; i++)
		{
			int x = (i - 1) % 2;
			int y = (i - 1) / 2;
			Settings.Category cat = Arrays.stream(Settings.Category.values()).toList().get(i);
			
			this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160 * x, this.height / 6 + 42 + 24 * y, 150, 20,
					((MutableText)cat.getTitle()).append("..."), button -> this.client.setScreen(new SettingsScreen(this, cat))));
		}
		
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100,
				this.height - (client != null && client.world != null ? 56 : 26), 200, 20,
				ScreenTexts.DONE, button -> this.client.setScreen(this.parent)));
	}
}
