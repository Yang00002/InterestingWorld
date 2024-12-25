package org.yang.interestingworld.mixin_interface;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;

public interface InterfaceStatusEffect
{
	default void onRemoveEffect(LivingEntity entity, int amplifier)
	{
	}

	default void updateOnEntityDamage(LivingEntity entity, DamageSource source, float amount,
									  StatusEffectInstance instance)
	{

	}
}
