package org.yang.interestingworld.item.heart.base;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BaseHeart extends Item
{
	public BaseHeart(Settings settings)
	{
		super(settings);
	}

	public boolean isEnchantable(ItemStack stack)
	{
		return true;
	}

	@Override
	public int getEnchantability()
	{
		return 1;
	}

	public int getMaxSupportLevel()
	{
		return 0;
	}

	public int getModelIndex()
	{
		return 0;
	}
}
