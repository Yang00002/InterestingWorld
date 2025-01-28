package org.yang.interestingworld.item.heart.base;

import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.util.heartflag.HeartDataFlag;
import org.yang.interestingworld.util.style.Color;

public class EnderiteHeartItem extends BaseHeart
{
	public EnderiteHeartItem(Settings settings)
	{
		super(settings.component(IWComponents.HEART_FLAG, HeartDataFlag.copyEmpty().setMaterialLevel(6)));
	}

	@Override
	public int getNameColorRGB()
	{
		return Color.EnderiteColorRGB;
	}

	@Override
	public int getEnchantability()
	{
		return 5;
	}

}
