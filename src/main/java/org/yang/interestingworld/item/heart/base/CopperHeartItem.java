package org.yang.interestingworld.item.heart.base;

import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.util.heartflag.HeartDataFlag;
import org.yang.interestingworld.util.style.Color;

public class CopperHeartItem extends BaseHeart
{
	public CopperHeartItem(Settings settings)
	{
		super(settings.component(IWComponents.HEART_FLAG, HeartDataFlag.copyEmpty().setMaterialLevel(1)));
	}

	@Override
	public int getEnchantability()
	{
		return 15;
	}

	@Override
	public int getNameColorRGB()
	{
		return Color.CopperColorRGB;
	}
}
