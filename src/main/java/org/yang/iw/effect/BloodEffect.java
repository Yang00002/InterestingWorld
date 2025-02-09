package org.yang.iw.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;
import org.yang.iw.IWDamageTypes;
import org.yang.iw.util.IWParticleUtil;
import org.yang.iw.util.style.Color;

public class BloodEffect extends StatusEffect
{
	public BloodEffect()
	{
		super(StatusEffectCategory.HARMFUL, Color.RED_RGB);
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier)
	{
		return duration % 20 == 0;
	}

	@Override
	public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier)
	{
		entity.damage(world, new DamageSource(IWDamageTypes.BLOOD_EFFECT_ENTRY.get()), amplifier + 1);
		return true;
	}

	@Override
	public void onEntityDamage(ServerWorld world, LivingEntity entity, int amplifier, DamageSource source,
							   float amount)
	{
		if (source.getTypeRegistryEntry().matchesKey(IWDamageTypes.BLOOD_EFFECT))
		{
			IWParticleUtil.spawnDamageIndicatorParticle(entity, amount);
		}
	}
}
