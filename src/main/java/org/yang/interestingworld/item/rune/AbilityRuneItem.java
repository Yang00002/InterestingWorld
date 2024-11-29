package org.yang.interestingworld.item.rune;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.yang.interestingworld.IWUtil;

import java.util.List;

import static org.yang.interestingworld.IWUtil.RuneAbility.getAbility;
import static org.yang.interestingworld.IWUtil.RuneAbility.getColor;

public class AbilityRuneItem extends RuneItem
{
	@Override
	public int getRuneType()
	{
		return 1;
	}

	@Override
	public Text getName(ItemStack stack)
	{
		var ab = getAbility(stack);
		return ab.getTitleText().append(Text.translatable("abilityrune.suffix"))
				.setStyle(IWUtil.TextStyle.getBoldTextStyle(getColor(stack)));
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		commonAppendTooltip(stack, context, tooltip, type);
		var ab = getAbility(stack);
		ab.appendToolTip(tooltip);
	}

	@Override
	public int getLevel(ItemStack stack)
	{
		var ab = getAbility(stack);
		return ab.level();
	}

	public AbilityRuneItem(Item.Settings settings)
	{
		super(settings.maxCount(1));
	}
}
