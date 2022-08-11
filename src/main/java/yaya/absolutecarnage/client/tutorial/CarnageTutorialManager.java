package yaya.absolutecarnage.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import yaya.absolutecarnage.event.KeyInputHandler;
import yaya.absolutecarnage.utility.TranslationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class CarnageTutorialManager
{
	public static CarnageTutorialManager instance;
	private final ToastManager toastManager;
	List<CarnageTutorialToast> toasts = new ArrayList<>();
	Map<String, Integer> tutorials = new HashMap<>(){{
		put("wings", 0);
	}};
	
	public CarnageTutorialManager()
	{
		toastManager = MinecraftClient.getInstance().getToastManager();
	}
	
	public static CarnageTutorialManager getInstance()
	{
		return instance;
	}
	
	//TODO: figure out why the wings tutorial always triggers despite dodge stat. Or maybe it doesn't save correctly?
	public void startTutorial(String type)
	{
		if(tutorials.getOrDefault(type, 0) != 0)
			return;
		CarnageTutorialToast toast = null;
		switch (type)
		{
			case "wings" -> toast = new CarnageTutorialToast(this, "wings",
					TranslationUtil.getText("tutorial", "wings.title"),
					Text.translatable("tutorial.absolute_carnage.wings.desc",
							Text.translatable(KeyInputHandler.dashKey.getBoundKeyTranslationKey())), true, 0, 3, false)
					.addChild("wingsDir");
			case "wingsDir" -> toast = new CarnageTutorialToast(this, "wingsDir",
					TranslationUtil.getText("tutorial", "wings_dir.title"),
					TranslationUtil.getText("tutorial", "wings_dir.desc"), true, 0, 6, true);
			case "webs" -> { }
		}
		if(toast != null)
		{
			toastManager.add(toast);
			toasts.add(toast);
			tutorials.put(type, 1);
		}
	}
	
	public void addProgress(String type, float amount)
	{
		for (CarnageTutorialToast toast : toasts)
		{
			if(toast.getId().matches(type))
				toast.addProgress(amount);
		}
		removeFinished();
	}
	
	public void addProgress(String type)
	{
		addProgress(type, 1f);
	}
	
	public void finishTutorial(CarnageTutorialToast toast)
	{
		tutorials.put(toast.getId(), 2);
	}
	
	void removeFinished()
	{
		List<CarnageTutorialToast> toRemove = new ArrayList<>();
		for (String key : tutorials.keySet())
		{
			if(tutorials.get(key) == 2)
			{
				toasts.forEach(i -> {
					if(i.getId().matches(key))
						toRemove.add(i);
				});
				toRemove.forEach(CarnageTutorialToast::remove);
				toasts.removeIf(toast -> toast.getId().matches(key));
			}
			tutorials.put(key, 3);
		}
	}
	
	static
	{
		instance = new CarnageTutorialManager();
	}
}
