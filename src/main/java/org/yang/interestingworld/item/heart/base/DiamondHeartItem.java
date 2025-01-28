package org.yang.interestingworld.item.heart.base;

import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.util.heartflag.HeartDataFlag;
import org.yang.interestingworld.util.style.Color;

public class DiamondHeartItem extends BaseHeart
{
	public DiamondHeartItem(Settings settings)
	{
		super(settings.component(IWComponents.HEART_FLAG, HeartDataFlag.copyEmpty().setMaterialLevel(4)));
	}

	@Override
	public int getNameColorRGB()
	{
		return Color.DiamondColorRGB;
	}

	@Override
	public int getEnchantability()
	{
		return 10;
	}

}
