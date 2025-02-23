package org.yang.iw.mixin.mixin_interface;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public interface InterfaceLivingEntity
{
	default boolean interestingWorld$removeStatusEffectVanilla(RegistryEntry<StatusEffect> effect)
	{
		return false;
	}
}
