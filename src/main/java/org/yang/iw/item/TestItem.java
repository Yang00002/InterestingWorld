package org.yang.iw.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.util.style.Color;

import java.util.List;

public class TestItem extends Item
{
	public TestItem(Settings settings)
	{
		super(settings);
	}

	public static void testAppendTooltipForLevelColor(List<Text> tooltip)
	{
		for (int i = 0; i < 11; i++)
			tooltip.add(Text.translatable(TranslationPool.TOOLTIP_HEART_CONTAINER_LEVEL).withColor(Color.GRAY_RGB)
					.append(Text.literal(String.valueOf(i)).withColor(Color.getLevelColor(i))));
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		testAppendTooltipForLevelColor(tooltip);
	}
}
