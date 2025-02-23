package org.yang.iw.mixin.mixin_interface;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;

public interface InterfaceStatusEffectInstance
{
	default void interestingWorld$setDuration(int tick)
	{
	}

	default float interestingWorld$getDamageValueModifierOnDamage(ServerWorld world, DamageSource source, float amount)
	{
		return 0;
	}
}
