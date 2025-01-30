package org.yang.iw.item.heart.common;

import net.minecraft.item.ItemStack;
import org.yang.iw.IWComponents;
import org.yang.iw.item.heart.AbstractHeart;
import org.yang.iw.util.heartflag.HeartDataFlag;

public abstract class CommonHeart extends AbstractHeart
{
	@Override
	public int getEnchantability()
	{
		return 1;
	}

	public CommonHeart(Settings settings)
	{
		super(settings.component(IWComponents.HEART_FLAG, HeartDataFlag.copyEmpty().setMaterialLevel(8)));
	}

	public boolean supportEnchant()
	{
		return true;
	}

	public boolean supportAbility()
	{
		return true;
	}

	public ItemStack boostStackByLevel(int lvl, ItemStack stack)
	{
		return stack;
	}

	@Override
	public ItemStack getDefaultStack(int materialLevel)
	{
		return boostStackByLevel(materialLevel, super.getDefaultStack(materialLevel));
	}
}
