package org.yang.iw.boost;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.effect.IWEffects;

public class FireAspectBoost extends AbstractBoost
{

	FireAspectBoost(Identifier identifier)
	{
		super(identifier);
	}

	public short maxAllowLevel()
	{
		return 255;
	}

	public void onTargetDamaged(int level, ServerWorld world, LivingEntity target, DamageSource damageSource)
	{
		target.setOnFireFor(2 + (level << 1));
		int burningLevel = (level - 1) << 1;
		if (burningLevel > 0) target.addStatusEffect(
				new StatusEffectInstance(IWEffects.BURNING, 10 * (2 + (level << 1)), burningLevel - 1),
				damageSource.getSource());
	}

	@Override
	public int xpCostOfLevel(short level)
	{
		return (135 + 15 * level) * (int) Math.sqrt(level);
	}
}