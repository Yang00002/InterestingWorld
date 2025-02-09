package org.yang.iw.item.heart.base;

import org.yang.iw.util.style.Color;

public class CopperHeartItem extends BaseHeart
{
	public CopperHeartItem(Settings settings)
	{
		super(settings, i -> 15, 1);
	}

	@Override
	public int getNameColorRGB()
	{
		return Color.CopperColorRGB;
	}
}
