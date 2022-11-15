package yaya.absolutecarnage.client.tutorial;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.utility.TranslationUtil;

import java.util.ArrayList;
import java.util.List;

public class CarnageTutorialToast implements Toast
{
	private final CarnageTutorialManager manager;
	private final String id;
	private Text title, description;
	private final boolean hasProgressBar, auto;
	private final int icon, requiredSteps;
	private final List<String> children = new ArrayList<>();
	
	private long lastTime, gratulateTime = 40;
	private float lastProgress;
	private float progress;
	private Visibility visibility;
	private boolean gratulating;
	
	public CarnageTutorialToast(CarnageTutorialManager manager, String id, Text title, @Nullable Text description,
								boolean hasProgressBar, int icon, int requiredSteps, boolean auto)
	{
		this.manager = manager;
		this.id = id;
		this.title = title;
		this.description = description;
		this.hasProgressBar = hasProgressBar;
		this.icon = icon;
		this.requiredSteps = requiredSteps;
		this.auto = auto;
		visibility = Visibility.SHOW;
	}
	
	@Override
	public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime)
	{
		RenderSystem.setShaderTexture(0, new Identifier(AbsoluteCarnage.MOD_ID, "textures/gui/toasts.png"));
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		manager.drawTexture(matrices, 0, 0, 0, 0, this.getWidth(), this.getHeight());
		
		manager.drawTexture(matrices, 6, 6, 176 + (icon % 4) * 20, icon / 4 * 20, 20, 20);
		
		if (description == null) {
			manager.getClient().textRenderer.draw(matrices, this.title, 30.0F, 12.0F, ColorHelper.Argb.getArgb(255, 61, 156, 220));
		} else {
			manager.getClient().textRenderer.draw(matrices, this.title, 30.0F, 7.0F, ColorHelper.Argb.getArgb(255, 61, 156, 220));
			manager.getClient().textRenderer.draw(matrices, this.description, 30.0F, 18.0F, ColorHelper.Argb.getArgb(255, 185, 187, 190));
		}
		
		if (this.hasProgressBar)
		{
			DrawableHelper.fill(matrices, 3, 28, 157, 29, ColorHelper.Argb.getArgb(255, 93, 32, 32));
			float f = MathHelper.clampedLerp(this.lastProgress, this.progress, (float)(startTime - this.lastTime) / 100.0F);
			Vec3d fillColor;
			if (this.progress >= this.lastProgress) {
				fillColor = new Vec3d(40, 204, 223);
			} else {
				fillColor = new Vec3d(57, 120, 168);
			}
			
			DrawableHelper.fill(matrices, 3, 28, (int)(3.0F + 154.0F * f), 29,
					ColorHelper.Argb.getArgb(255, (int) fillColor.x, (int) fillColor.y, (int) fillColor.z));
			this.lastProgress = f;
			if(auto)
				this.manager.addProgress(id, (float)(startTime - this.lastTime) / 1000.0F);
			this.lastTime = startTime;
		}
		
		if(gratulating)
		{
			if(gratulateTime > 0)
			{
				gratulateTime--;
			}
			else
			{
				gratulating = false;
				remove();
			}
		}
		
		return visibility;
	}
	
	public void addProgress(float progress)
	{
		this.progress = Math.min(this.progress + progress / requiredSteps, 1);
		if(this.progress == 1)
			manager.finishTutorial(this);
	}
	
	public void gratulate()
	{
		title = TranslationUtil.getText("tutorial", "finished.title");
		description = TranslationUtil.getText("tutorial", "finished.desc");
		gratulating = true;
	}
	
	public void remove()
	{
		visibility = Visibility.HIDE;
	}
	
	public void showChildren()
	{
		for (String child : children)
		{
			this.manager.startTutorial(child);
		}
	}
	
	public CarnageTutorialToast addChild(String child)
	{
		children.add(child);
		return this;
	}
	
	public String getId()
	{
		return id;
	}
}
