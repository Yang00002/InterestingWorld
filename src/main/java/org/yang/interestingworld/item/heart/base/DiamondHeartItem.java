package org.yang.interestingworld.item.heart.base;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.yang.interestingworld.util.style.Color;

public class DiamondHeartItem extends BaseHeart
{
	public DiamondHeartItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public Text getName(ItemStack stack)
	{
		return Text.translatable(getTranslationKey()).withColor(Color.DiamondColorRGB);
	}

	@Override
	public int getEnchantability()
	{
		return 13;
	}

	@Override
	public int getMaxSupportLevel()
	{
		return 4;
	}

	@Override
	public int getModelIndex()
	{
		return 3;
	}
}
