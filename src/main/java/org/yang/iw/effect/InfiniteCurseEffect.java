package org.yang.iw.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import org.yang.iw.IWDamageTypes;
import org.yang.iw.ability.InfiniteCurseAbility;
import org.yang.iw.util.IWParticleUtil;
import org.yang.iw.util.style.Color;

public class InfiniteCurseEffect extends StatusEffect
{
	public InfiniteCurseEffect()
	{
		super(StatusEffectCategory.HARMFUL, Color.CYAN_RGB);
	}

	@Override
	public void updateOnEntityDamage(LivingEntity entity, DamageSource source, float amount,
									 StatusEffectInstance instance)
	{
		int d = instance.getDuration();
		if (d >= 0) instance.interestingWorld$setDuration(instance.mapDuration(duration -> InfiniteCurseAbility.ExplodeTick));
	}

	@Override
	public void onRemoveEffect(LivingEntity entity, int amplifier)
	{
		entity.damage((ServerWorld) entity.getWorld(), new DamageSource(IWDamageTypes.ENERGY_EXPLODE_ENTRY.get()),
				amplifier + 1);
		IWParticleUtil.spawnParticleAtPos(entity.getWorld(), ParticleTypes.EXPLOSION, entity.getX(), entity.getY(),
				entity.getZ(), 3, 0.5, 0.5, 0.5, 0);
	}
}
