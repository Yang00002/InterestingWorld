package org.yang.interestingworld.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.effect.BloodEffect;
import org.yang.interestingworld.effect.CooldownEffect;
import org.yang.interestingworld.effect.HurtingEffect;
import org.yang.interestingworld.effect.InfiniteCurseEffect;
import org.yang.interestingworld.util.Base;

import static org.yang.interestingworld.util.Base.MOD_ID;

public class IWEffects
{
	public static final RegistryEntry<StatusEffect> BLOOD = Registry.registerReference(Registries.STATUS_EFFECT,
			Identifier.of(MOD_ID, "blood"), new BloodEffect());
	public static final RegistryEntry<StatusEffect> COOLDOWN = Registry.registerReference(Registries.STATUS_EFFECT,
			Identifier.of(Base.MOD_ID, "cooldown"), new CooldownEffect());
	public static final RegistryEntry<StatusEffect> HURTING = Registry.registerReference(Registries.STATUS_EFFECT,
			Identifier.of(Base.MOD_ID, "hurting"), new HurtingEffect());
	public static final RegistryEntry<StatusEffect> INFINITECURSE =
			Registry.registerReference(Registries.STATUS_EFFECT,
			Identifier.of(Base.MOD_ID, "infinitecurse"), new InfiniteCurseEffect());

	public static void initialize()
	{
	}
}