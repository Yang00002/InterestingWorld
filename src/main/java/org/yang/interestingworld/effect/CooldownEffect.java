package org.yang.interestingworld.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.util.style.Color;

public class CooldownEffect extends StatusEffect
{
	public CooldownEffect()
	{
		super(StatusEffectCategory.HARMFUL, Color.YELLOW_RGB);
	}

}

