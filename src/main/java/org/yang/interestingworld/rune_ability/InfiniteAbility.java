package org.yang.interestingworld.rune_ability;

import org.apache.commons.lang3.mutable.MutableFloat;
import org.yang.interestingworld.util.style.Color;

import static org.yang.interestingworld.util.Return.RETURNTRUE;

public class InfiniteAbility extends RuneAbility
{
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
