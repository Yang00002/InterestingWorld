package org.yang.interestingworld.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import org.yang.interestingworld.IWDamageTypes;
import org.yang.interestingworld.rune_ability.InfiniteCurseAbility;
import org.yang.interestingworld.util.IWParticleUtil;
import org.yang.interestingworld.util.style.Color;

public class InfiniteCurseEffect extends StatusEffect
{
	public InfiniteCurseEffect()
	{
		super(StatusEffectCategory.HARMFUL, Color.CYAN_RGB);
	}

	//TODO 使能力不能影响盔甲架
	//TODO playerData 可以换成接口注入
	@Override
	public void updateOnEntityDamage(LivingEntity entity, DamageSource source, float amount,
									 StatusEffectInstance instance)
	{
		int d = instance.getDuration();
		if (d >= 0) instance.setDuration(instance.mapDuration(duration -> InfiniteCurseAbility.ExplodeTick));
	}

	@Override
	public void onRemoveEffect(LivingEntity entity, int amplifier)
	{
		entity.damage(new DamageSource(IWDamageTypes.ENERGEE_EXPLODE_entry.get()), amplifier + 1);
		IWParticleUtil.spawnParticleAtPos(entity.getWorld(), ParticleTypes.EXPLOSION, entity.getX(), entity.getY(),
				entity.getZ(), 3, 0.5, 0.5, 0.5, 0);
	}
}
