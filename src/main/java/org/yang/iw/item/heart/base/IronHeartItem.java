package org.yang.iw.item.heart.base;

import org.yang.iw.IWComponents;
import org.yang.iw.util.heartflag.HeartDataFlag;
import org.yang.iw.util.style.Color;

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
