package org.yang.interestingworld.item.heart.base;

import net.minecraft.item.Item;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.util.heartflag.HeartDataFlag;
import org.yang.interestingworld.util.style.Color;

public class VoidalloyHeartItem extends BaseHeart
{
	public VoidalloyHeartItem(Item.Settings settings)
	{
		super(settings.component(IWComponents.HEART_FLAG, HeartDataFlag.copyEmpty().setMaterialLevel(8)));
	}

	@Override
	public int getNameColorRGB()
	{
		return Color.VoidalloyColorRGB;
	}

	@Override
	public int getEnchantability()
	{
		return 18;
	}


}
