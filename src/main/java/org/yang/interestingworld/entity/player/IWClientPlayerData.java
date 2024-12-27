package org.yang.interestingworld.entity.player;

import org.yang.interestingworld.rune_ability.AbstractRuneAbility;
import org.yang.interestingworld.rune_ability.IWRuneAbilities;

public class IWClientPlayerData
{
	public AbstractRuneAbility WeaponAbility = IWRuneAbilities.DEFAULT_ABILITY;
	public int shown_energy = 0;
	public int charge_rate16 = 0;
	public boolean charged = false;
	public int shown_number = 0;
}
