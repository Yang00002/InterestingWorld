package org.yang.interestingworld.item.heart.base;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.yang.interestingworld.util.style.Color;

public class NetheriteHeartItem extends BaseHeart
{
	public NetheriteHeartItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public Text getName(ItemStack stack)
	{
		return Text.translatable(getTranslationKey()).withColor(Color.NetheriteColorRGB);
	}

	@Override
	public int getEnchantability()
	{
		return 17;
	}

	@Override
	public int getMaxSupportLevel()
	{
		return 5;
	}

	@Override
	public int getModelIndex()
	{
		return 4;
	}
}
