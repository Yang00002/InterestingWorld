package org.yang.interestingworld.item.heart.base;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.yang.interestingworld.util.style.Color;

public class VoidalloyHeartItem extends BaseHeart
{
	public VoidalloyHeartItem(Item.Settings settings)
	{
		super(settings);
	}

	@Override
	public Text getName(ItemStack stack)
	{
		return Text.translatable(getTranslationKey()).withColor(Color.VoidalloyColorRGB);
	}

	@Override
	public int getEnchantability()
	{
		return 25;
	}

	@Override
	public int getMaxSupportLevel()
	{
		return 8;
	}

	@Override
	public int getModelIndex()
	{
		return 6;
	}
}
