package yaya.absolutecarnage.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import yaya.absolutecarnage.networking.ModPackets;

public class KeyInputHandler
{
	public static final String KEY_CATEGORY_ABSOLUTE_CARNAGE = "key.category.absolute_carnage";
	public static final String KEY_DIRECTIONAL_DASH = "key.absolute_carnage.directional_dash";
	
	public static KeyBinding dashKey;
	
	public static void registerKeyInputs()
	{
		ClientTickEvents.END_CLIENT_TICK.register(client ->
		{
			GameOptions options = client.options;
			if(dashKey.wasPressed())
			{
				int direction = 0;
				if(options.rightKey.isPressed())
					direction = 1;
				else if(options.leftKey.isPressed())
					direction = 2;
				if(options.backKey.isPressed())
					direction = 3;
				ClientPlayNetworking.send(ModPackets.DASH_ID, PacketByteBufs.create().writeVarInt(direction));
			}
		});
	}
	
	public static void register()
	{
		dashKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(KEY_DIRECTIONAL_DASH, InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_LEFT_ALT, KEY_CATEGORY_ABSOLUTE_CARNAGE));
		
		registerKeyInputs();
	}
}
