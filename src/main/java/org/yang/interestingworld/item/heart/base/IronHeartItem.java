package org.yang.interestingworld.item.heart.base;

import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.util.heartflag.HeartDataFlag;
import org.yang.interestingworld.util.style.Color;

public class IronHeartItem extends BaseHeart
{
	public IronHeartItem(Settings settings)
	{
		super(settings.component(IWComponents.HEART_FLAG, HeartDataFlag.copyEmpty().setMaterialLevel(2)));
	}

	@Override
	public int getNameColorRGB()
	{
		return Color.IronColorRGB;
	}


	@Override
	public int getEnchantability()
	{
		return 14;
	}


}
