package org.yang.interestingworld.item.heart.base;

import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.util.heartflag.HeartDataFlag;
import org.yang.interestingworld.util.style.Color;

public class GoldHeartItem extends BaseHeart
{
	public GoldHeartItem(Settings settings)
	{
		super(settings.component(IWComponents.HEART_FLAG, HeartDataFlag.copyEmpty().setMaterialLevel(3)));
	}

	@Override
	public int getNameColorRGB()
	{
		return Color.GoldColorRGB;
	}

	@Override
	public int getEnchantability()
	{
		return 22;
	}


}
