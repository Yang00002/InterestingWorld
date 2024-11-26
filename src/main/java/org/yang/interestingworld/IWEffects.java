package org.yang.interestingworld;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.effect.BloodEffect;
import org.yang.interestingworld.effect.CooldownEffect;
import org.yang.interestingworld.effect.HurtingEffect;

public class IWEffects
{
	public static final RegistryEntry<StatusEffect> BLOOD = Registry.registerReference(Registries.STATUS_EFFECT,
			Identifier.of(IWUtil.Base.MOD_ID, "blood"), new BloodEffect());
	public static final RegistryEntry<StatusEffect> COOLDOWN = Registry.registerReference(Registries.STATUS_EFFECT,
			Identifier.of(IWUtil.Base.MOD_ID, "cooldown"), new CooldownEffect());
	public static final RegistryEntry<StatusEffect> HURTING = Registry.registerReference(Registries.STATUS_EFFECT,
			Identifier.of(IWUtil.Base.MOD_ID, "hurting"), new HurtingEffect());
	public static void initialize()
	{
	}
}