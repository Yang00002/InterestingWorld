package org.yang.iw.boost;

import net.minecraft.item.ItemStack;
import org.yang.iw.component.BoostComponent;
import org.yang.iw.component.IWComponents;

public class BoostHelper
{
	public static int maxEnergy(ItemStack stack)
	{
		return stack.getOrDefault(IWComponents.BOOST, BoostComponent.DEFAULT).getModifiedMaxEnergy(stack);
	}
}
