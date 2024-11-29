package org.yang.interestingworld.item.rune;

import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

public class EnchantmentRuneItem extends RuneItem
{
	@Override
	public int getRuneType()
	{
		return 2;
	}

	public EnchantmentRuneItem(Settings settings)
	{
		super(settings.maxCount(1));
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		commonAppendTooltip(stack, context, tooltip, type);
	}
}
