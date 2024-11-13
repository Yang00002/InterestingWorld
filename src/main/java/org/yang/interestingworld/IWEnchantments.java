package org.yang.interestingworld;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class IWEnchantments
{
	public static final RegistryKey<Enchantment> FAST_HIT = RegistryKey.of(RegistryKeys.ENCHANTMENT,
			Identifier.of(IWUtil.Base.MOD_ID, "fast_hit"));

	public static void initialize()
	{
	}
}
