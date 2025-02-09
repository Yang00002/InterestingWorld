package org.yang.iw.item.heart.base;

import org.yang.iw.IWComponents;
import org.yang.iw.util.heartflag.HeartDataFlag;
import org.yang.iw.util.style.Color;

public class EnderiteHeartItem extends BaseHeart
{
	public EnderiteHeartItem(Settings settings)
	{
		super(settings, i -> 5, 6);
	}

	@Override
	public int getNameColorRGB()
	{
		return Color.EnderiteColorRGB;
	}


}
