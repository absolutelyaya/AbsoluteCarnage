package yaya.absolutecarnage.registries;

import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import yaya.absolutecarnage.AbsoluteCarnage;

public class StatRegistry
{
	public static final Identifier DODGE = new Identifier(AbsoluteCarnage.MOD_ID, "dodge");
	
	public static void registerStats()
	{
		Registry.register(Registry.CUSTOM_STAT, "dodge", DODGE);
		Stats.CUSTOM.getOrCreateStat(DODGE, StatFormatter.DEFAULT);
	}
}
