package org.yang.iw.client;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;

public class IWInteractionUtil
{
	public static int verticalScroll(double horizontal, double vertical)
	{
		int i = (int) horizontal;
		int j = (int) vertical;
		if (j != 0) return j;
		return -i;
	}

	public static int scrollCycling(int amount, int selectedIndex, int total)
	{
		int i = (int) Math.signum(amount);
		selectedIndex -= i;
		selectedIndex = Math.max(-1, selectedIndex);

		while (selectedIndex < 0)
		{
			selectedIndex += total;
		}

		while (selectedIndex >= total)
		{
			selectedIndex -= total;
		}

		return selectedIndex;
	}

	public static int predicateSlotId(HandledScreen<?> screen, Slot slot)
	{
		if (screen instanceof CreativeInventoryScreen)
		{
			if (slot.inventory instanceof PlayerInventory)
			{
				if (slot.id > 0) return slot.getIndex() + 36;
				return slot.getIndex();
			}
			return -1;
		}
		return slot.id;
	}
}
