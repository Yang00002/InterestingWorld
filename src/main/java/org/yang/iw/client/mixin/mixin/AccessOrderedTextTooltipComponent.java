package org.yang.iw.client.mixin.mixin;

import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.yang.iw.mixin.mixin_interface.InterfaceOrderedTextTooltipComponent;

@Mixin(OrderedTextTooltipComponent.class)
public class AccessOrderedTextTooltipComponent implements InterfaceOrderedTextTooltipComponent
{

	@Shadow
	@Final
	private OrderedText text;

	@Override
	public OrderedText getText()
	{
		return text;
	}

}