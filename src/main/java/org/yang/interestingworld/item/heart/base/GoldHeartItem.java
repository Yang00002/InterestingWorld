package org.yang.interestingworld.item.heart.base;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.yang.interestingworld.util.style.Color;

public class GoldHeartItem extends BaseHeart
{
	public GoldHeartItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public Text getName(ItemStack stack)
	{
		return Text.translatable(getTranslationKey()).withColor(Color.GoldColorRGB);
	}

	@Override
	public int getEnchantability()
	{
		return 9;
	}

	@Override
	public int getMaxSupportLevel()
	{
		return 3;
	}

	@Override
	public int getModelIndex()
	{
		return 2;
	}
}
