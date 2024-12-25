package org.yang.interestingworld.playerdatamanager;

import org.yang.interestingworld.rune.IWAbstractRuneAbility;
import org.yang.interestingworld.rune.IWRuneAbilitys;

public class ClientPlayerDataManager
{
	public IWAbstractRuneAbility WeaponAbility = IWRuneAbilitys.DEFAULT_ABILITY;
	public int shown_energy = 0;
	public int charge_rate16 = 0;
	public boolean charged = false;
	public int shown_number = 0;
}
