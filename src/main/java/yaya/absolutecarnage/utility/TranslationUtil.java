package yaya.absolutecarnage.utility;

import net.minecraft.text.Text;
import yaya.absolutecarnage.AbsoluteCarnage;

import java.util.ArrayList;
import java.util.List;

public class TranslationUtil
{
	public static LoreBuilder getLoreBuilder(String name)
	{
		return new LoreBuilder(name);
	}
	
	public static String getKey(String type, String name)
	{
		return String.format("%s.%s.%s", type, AbsoluteCarnage.MOD_ID, name);
	}
	
	public static Text getText(String type, String name)
	{
		return Text.translatable(getKey(type, name));
	}
	
	public static class LoreBuilder
	{
		final String name;
		final List<String> keys = new ArrayList<>();
		
		private LoreBuilder(String name)
		{
			this.name = name;
		}
		
		public LoreBuilder addLines(int lines)
		{
			for (int i = 0; i < lines; i++)
				keys.add(getKey("item", String.format("%s.desc%s", name, i + 1)));
			return this;
		}
		
		public LoreBuilder addExtra(int lines )
		{
			for (int i = 0; i < lines; i++)
				keys.add("e#" +getKey("extra", name + (i + 1)));
			return this;
		}
		
		public String[] build()
		{
			return keys.toArray(new String[0]);
		}
	}
}
