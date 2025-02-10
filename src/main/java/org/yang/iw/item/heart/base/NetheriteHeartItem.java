package org.yang.iw.item.heart.base;

import org.yang.iw.util.style.Color;

public class NetheriteHeartItem extends BaseHeart
{
	public NetheriteHeartItem(Settings settings)
	{
		super(settings, i -> 15, 5);
	}

	@Override
	public int getNameColorRGB()
	{
		return Color.NetheriteColorRGB;
	}



}
