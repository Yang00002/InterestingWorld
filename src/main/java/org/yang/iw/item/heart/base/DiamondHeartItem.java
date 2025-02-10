package org.yang.iw.item.heart.base;

import org.yang.iw.util.style.Color;

public class DiamondHeartItem extends BaseHeart
{
	public DiamondHeartItem(Settings settings)
	{
		super(settings, i -> 10, 4);
	}

	@Override
	public int getNameColorRGB()
	{
		return Color.DiamondColorRGB;
	}


}
