package org.yang.iw.item.heart.base;

import org.yang.iw.util.style.Color;

public class GoldHeartItem extends BaseHeart
{
	public GoldHeartItem(Settings settings)
	{
		super(settings, i -> 22, 3);
	}

	@Override
	public int getNameColorRGB()
	{
		return Color.GoldColorRGB;
	}



}
