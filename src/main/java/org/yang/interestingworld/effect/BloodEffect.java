package org.yang.interestingworld.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import org.yang.interestingworld.IWDamageTypes;
import org.yang.interestingworld.IWUtil;

public class BloodEffect extends StatusEffect
{
    public BloodEffect()
    {
        super(StatusEffectCategory.HARMFUL, IWUtil.TextStyle.RED_RGB);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier)
    {
        return duration % 20 == 0;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier)
    {
        entity.damage(IWUtil.Registry.createDamageSource(entity.getWorld(), IWDamageTypes.BLOOD_EFFECT), amplifier + 1);
        return super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public void onEntityDamage(LivingEntity entity, int amplifier, DamageSource source, float amount)
    {
        if (source.getTypeRegistryEntry().matchesKey(IWDamageTypes.BLOOD_EFFECT))
        {
            IWUtil.Network.spawnDamageIndicatorParticle(entity, amount);
        }
    }
}
