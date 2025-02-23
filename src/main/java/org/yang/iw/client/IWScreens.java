package org.yang.iw.client;

import net.minecraft.client.gui.screen.ingame.HandledScreens;
import org.yang.iw.IWScreenHandlers;
import org.yang.iw.block.forgingblock.ForgingBlockScreen;

public class IWScreens
{
	public static void initialize()
	{
		HandledScreens.register(IWScreenHandlers.FORGINGBLOCK_SCREEN_HANDLER, ForgingBlockScreen::new);
	}
}
