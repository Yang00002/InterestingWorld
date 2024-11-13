package org.yang.interestingworld.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import org.yang.interestingworld.IWUtil;

public class CooldownEffect extends StatusEffect
{
	public CooldownEffect()
	{
		super(StatusEffectCategory.HARMFUL, IWUtil.TextStyle.YELLOW_RGB);
	}

}

