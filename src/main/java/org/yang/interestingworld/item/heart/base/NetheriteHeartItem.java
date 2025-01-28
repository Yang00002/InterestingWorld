package org.yang.interestingworld.item.heart.base;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.util.heartflag.HeartDataFlag;
import org.yang.interestingworld.util.style.Color;

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
