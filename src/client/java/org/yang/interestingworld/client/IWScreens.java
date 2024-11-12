package org.yang.interestingworld.client;

import net.minecraft.client.gui.screen.ingame.HandledScreens;
import org.yang.interestingworld.IWScreenHandlers;
import org.yang.interestingworld.client.block.forging_block.ForgingBlockScreen;

public class IWScreens
{
	public static void initialize()
	{
		HandledScreens.register(IWScreenHandlers.FORGINGBLOCK_SCREEN_HANDLER, ForgingBlockScreen::new);
	}

}
