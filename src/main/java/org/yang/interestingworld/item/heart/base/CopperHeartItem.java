package org.yang.interestingworld.item.heart.base;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.yang.interestingworld.util.style.Color;

public class CopperHeartItem extends BaseHeart
{
	public CopperHeartItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public Text getName(ItemStack stack)
	{
		return Text.translatable(getTranslationKey()).withColor(Color.CopperColorRGB);
	}

	@Override
	public int getMaxSupportLevel()
	{
		return 1;
	}
}
