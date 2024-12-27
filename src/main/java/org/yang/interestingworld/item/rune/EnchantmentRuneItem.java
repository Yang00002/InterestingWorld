package org.yang.interestingworld.item.rune;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.util.style.Color;
import org.yang.interestingworld.util.style.TextStyle;

import java.util.List;

public class EnchantmentRuneItem extends RuneItem
{

	public EnchantmentRuneItem(Settings settings)
	{
		super(settings.maxCount(1));
	}

	@Override
	public int getLevel(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelDataComponent.DEFAULT).value();
	}

	@Override
	public Text getName(ItemStack stack)
	{
		return Text.translatable(getTranslationKey()).setStyle(TextStyle.BOLD_STYLE)
				.withColor(Color.getLevelColor(getLevel(stack)));
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		commonAppendTooltip(stack, context, tooltip, type);
	}

	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return false;
	}

	@Override
	public int getEnchantability()
	{
		return 0;
	}
}
