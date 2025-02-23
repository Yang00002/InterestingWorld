package org.yang.iw.mixin.mixin_interface;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;

public interface InterfaceStatusEffect
{
	default void onRemoveEffect(LivingEntity entity, int amplifier)
	{
	}

	default void updateOnEntityDamage(LivingEntity entity, DamageSource source, float amount,
									  StatusEffectInstance instance)
	{

	}

	default float getDamageValueModifierOnDamage(ServerWorld world, DamageSource source, float amount, int amplifier)
	{
		return 0;
	}

}
