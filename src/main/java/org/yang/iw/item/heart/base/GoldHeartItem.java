package org.yang.iw.item.heart.base;

import org.yang.iw.IWComponents;
import org.yang.iw.util.heartflag.HeartDataFlag;
import org.yang.iw.util.style.Color;

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
