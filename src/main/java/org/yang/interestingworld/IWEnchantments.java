package org.yang.interestingworld;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.util.Base;

public class IWEnchantments
{
	public static final RegistryKey<Enchantment> FAST_HIT = RegistryKey.of(RegistryKeys.ENCHANTMENT,
			Identifier.of(Base.MOD_ID, "fast_hit"));
	public static final RegistryKey<Enchantment> FAST_ATTACK = RegistryKey.of(RegistryKeys.ENCHANTMENT,
			Identifier.of(Base.MOD_ID, "fast_attack"));
	public static final RegistryKey<Enchantment> SHARPNESS = RegistryKey.of(RegistryKeys.ENCHANTMENT,
			Identifier.of(Base.MOD_ID, "sharpness"));

	public static void initialize()
	{
	}
}
