package org.yang.iw.item.heart.base;

import org.yang.iw.IWComponents;
import org.yang.iw.util.heartflag.HeartDataFlag;
import org.yang.iw.util.style.Color;

public class EnderiteHeartItem extends BaseHeart
{
	public EnderiteHeartItem(Settings settings)
	{
		super(settings.component(IWComponents.HEART_FLAG, HeartDataFlag.copyEmpty().setMaterialLevel(6)));
	}

	@Override
	public int getNameColorRGB()
	{
		return Color.EnderiteColorRGB;
	}

	@Override
	public int getEnchantability()
	{
		return 5;
	}

}
