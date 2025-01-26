package org.yang.interestingworld.item.heart.base;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.yang.interestingworld.util.style.Color;

public class EnderiteHeartItem extends BaseHeart
{
	public EnderiteHeartItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public Text getName(ItemStack stack)
	{
		return Text.translatable(getTranslationKey()).withColor(Color.EnderiteColorRGB);
	}

	@Override
	public int getEnchantability()
	{
		return 21;
	}

	@Override
	public int getMaxSupportLevel()
	{
		return 6;
	}

	@Override
	public int getModelIndex()
	{
		return 5;
	}
}
