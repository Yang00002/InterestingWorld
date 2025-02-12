package org.yang.iw.mixin.mixin_interface;

import net.minecraft.text.OrderedText;

public interface InterfaceOrderedTextTooltipComponent
{
	default OrderedText getText()
	{
		return null;
	}
}
