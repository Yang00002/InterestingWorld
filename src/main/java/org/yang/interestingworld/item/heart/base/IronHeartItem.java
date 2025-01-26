package org.yang.interestingworld.item.heart.base;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.yang.interestingworld.util.style.Color;

public class IronHeartItem extends BaseHeart
{
	public IronHeartItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public Text getName(ItemStack stack)
	{
		return Text.translatable(getTranslationKey()).withColor(Color.IronColorRGB);
	}

	@Override
	public int getEnchantability()
	{
		return 5;
	}

	@Override
	public int getMaxSupportLevel()
	{
		return 2;
	}

	@Override
	public int getModelIndex()
	{
		return 1;
	}
}
