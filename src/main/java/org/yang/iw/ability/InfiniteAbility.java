package org.yang.iw.ability;

import org.apache.commons.lang3.mutable.MutableFloat;
import org.yang.iw.api.tag.IntrusiveTag;
import org.yang.iw.item.heart.AbilityHeart;
import org.yang.iw.util.style.Color;

import static org.yang.iw.util.Return.RETURNTRUE;

public class InfiniteAbility extends CommonAbility
{
	InfiniteAbility(IntrusiveTag<AbilityHeart> intrusiveTag)
	{
		super(intrusiveTag);
	}

	@Override
	public int level()
	{
		return 10;
	}

	@Override
	public int getColor()
	{
		return Color.CYAN_RGB;
	}

	@Override
	public boolean canEnergyItemBarVisible()
	{
		return false;
	}

	@Override
	public byte extractPower(MutableFloat value)
	{
		return RETURNTRUE;
	}
}
