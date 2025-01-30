package org.yang.iw.item.heart.base;

import org.yang.iw.IWComponents;
import org.yang.iw.util.heartflag.HeartDataFlag;
import org.yang.iw.util.style.Color;

public class NetheriteHeartItem extends BaseHeart
{
	public NetheriteHeartItem(Settings settings)
	{
		super(settings.component(IWComponents.HEART_FLAG, HeartDataFlag.copyEmpty().setMaterialLevel(5)));
	}

	@Override
	public int getNameColorRGB()
	{
		return Color.NetheriteColorRGB;
	}

	@Override
	public int getEnchantability()
	{
		return 15;
	}


}
