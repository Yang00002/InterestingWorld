package org.yang.iw.mixin.mixin_interface;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.yang.iw.boost.function.BoostFunctionMap;

public interface InterfaceItem
{
	default @Nullable BoostFunctionMap interestingWorld$uniqueBoostFor(ItemStack stack)
	{
		return null;
	}

	default boolean mouseScroll(double horizontalAmount, double verticalAmount, HandledScreen<?> screen, Slot slot,
								MinecraftClient client)
	{
		return false;
	}
}
