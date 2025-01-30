package org.yang.iw.item.heart.base;

import net.minecraft.item.Item;
import org.yang.iw.IWComponents;
import org.yang.iw.util.heartflag.HeartDataFlag;
import org.yang.iw.util.style.Color;

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
