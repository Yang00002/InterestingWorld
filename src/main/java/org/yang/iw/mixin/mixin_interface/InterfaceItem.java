package org.yang.iw.mixin.mixin_interface;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.yang.iw.boost.BoostContainer;

public interface InterfaceItem
{
	default @Nullable BoostContainer interestingWorld$uniqueBoostFor(ItemStack stack)
	{
		return null;
	}
}
