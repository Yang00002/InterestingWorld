package org.yang.interestingworld.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import org.yang.interestingworld.IWDamageTypes;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.rune.ability.InfiniteCurseAbility;

import static org.yang.interestingworld.util.Base.iwlogger;

public class InfiniteCurseEffect extends StatusEffect
{
	public InfiniteCurseEffect()
	{
		super(StatusEffectCategory.HARMFUL, IWUtil.TextStyle.CYAN_RGB);
	}

	//TODO 这个不知道为什么没有可以执行但没有起到作用
	//TODO playerData 可以换成接口注入
	@Override
	public void updateOnEntityDamage(LivingEntity entity, DamageSource source, float amount,
									 StatusEffectInstance instance)
	{
		iwlogger.info("hit");
		int d = instance.getDuration();
		if (d >= 0) instance.mapDuration(duration -> InfiniteCurseAbility.ExplodeTick);
	}

	@Override
	public void onRemoveEffect(LivingEntity entity, int amplifier)
	{
		entity.damage(IWUtil.Registry.createDamageSource(entity.getWorld(), IWDamageTypes.ENERGEE_EXPLODE),
				amplifier + 1);
		IWUtil.Network.spawnParticleAtPos(entity.getWorld(), ParticleTypes.EXPLOSION, entity.getX(), entity.getY(),
				entity.getZ(), 3, 0.5, 0.5, 0.5, 0);
	}
}
