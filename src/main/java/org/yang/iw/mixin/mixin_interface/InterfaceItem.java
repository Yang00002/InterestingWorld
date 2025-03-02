package org.yang.iw.mixin.mixin_interface;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.yang.iw.boost.BoostContainer;
import org.yang.iw.boost.function.BoostFunctionMap;

public interface InterfaceItem
{
	default @Nullable BoostFunctionMap interestingWorld$uniqueBoostFor(ItemStack stack)
	{
		return null;
	}
}
