package org.yang.iw.effect;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;

public class BurningEffect extends StatusEffect
{
	protected BurningEffect()
	{
		super(StatusEffectCategory.HARMFUL, 0XFF4500);
	}

	public float getDamageValueModifierOnDamage(ServerWorld world, DamageSource source, float amount, int amplifier)
	{
		if (source.isIn(DamageTypeTags.IS_FIRE)) return (amplifier + 1) * 0.1f * amount;
		return 0;
	}
}
