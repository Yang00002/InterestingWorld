package org.yang.interestingworld.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class IWKeyBindings
{
	public static final KeyBinding ABILITY_OPEN_CLOSE_KEYBIND = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("iw.key.ability_oc", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "iw.key.category"));

	public static void initialize()
	{
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (ABILITY_OPEN_CLOSE_KEYBIND.wasPressed() && client.player != null)
				client.player.getIWClientPlayerData().switchAbilityOpen();
		});
	}
}
