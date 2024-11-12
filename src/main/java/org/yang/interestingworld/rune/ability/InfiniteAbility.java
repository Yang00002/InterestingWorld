package org.yang.interestingworld.rune.ability;

import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.rune.IWRuneAbility;

import static org.yang.interestingworld.IWUtil.Return.RETURNTRUE;
import static org.yang.interestingworld.IWUtil.TextStyle.CYAN_RGB;

public class InfiniteAbility extends IWRuneAbility
{
    @Override
    public int level()
    {
        return 0;
    }

    @Override
    public int getColor()
    {
        return CYAN_RGB;
    }

    @Override
    public boolean canEnergyItemBarVisible()
    {
        return false;
    }

    @Override
    public byte extractPower(IWUtil.Return.FloatHolder value)
    {
        return RETURNTRUE;
    }
}
