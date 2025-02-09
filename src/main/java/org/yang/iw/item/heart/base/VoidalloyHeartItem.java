package org.yang.iw.item.heart.base;

import net.minecraft.item.Item;
import org.yang.iw.util.style.Color;

public class VoidalloyHeartItem extends BaseHeart
{
	public VoidalloyHeartItem(Item.Settings settings)
	{
		super(settings, i -> 18, 8);
	}

	@Override
	public int getNameColorRGB()
	{
		return Color.VoidalloyColorRGB;
	}

}
