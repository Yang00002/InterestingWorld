package org.yang.iw.enchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.yang.iw.util.Base;
import org.yang.iw.util.Server.LoadOnceRegistryEntry;

import static org.yang.iw.util.Server.getLoadOnceRegistryEntry;

public class IWEnchantments
{
	public static final RegistryKey<Enchantment> FAST_HIT = RegistryKey.of(RegistryKeys.ENCHANTMENT,
			Identifier.of(Base.MOD_ID, "fast_hit"));

	//public static final RegistryKey<Enchantment> FAST_ATTACK = RegistryKey.of(RegistryKeys.ENCHANTMENT,
	//		Identifier.of(Base.MOD_ID, "fast_attack"));
	//public static final RegistryKey<Enchantment> SHARPNESS = RegistryKey.of(RegistryKeys.ENCHANTMENT,
	//		Identifier.of(Base.MOD_ID, "sharpness"));

	public static final RegistryKey<Enchantment> BALANCE = RegistryKey.of(RegistryKeys.ENCHANTMENT,
			Identifier.of(Base.MOD_ID, "balance"));
	public static final RegistryKey<Enchantment> PLENTIFUL = RegistryKey.of(RegistryKeys.ENCHANTMENT,
			Identifier.of(Base.MOD_ID, "plentiful"));
	public static final RegistryKey<Enchantment> RUNE_BOOST = RegistryKey.of(RegistryKeys.ENCHANTMENT,
			Identifier.of(Base.MOD_ID, "rune_boost"));
	public static final RegistryKey<Enchantment> ENERGY_EFFICIENCY = RegistryKey.of(RegistryKeys.ENCHANTMENT,
			Identifier.of(Base.MOD_ID, "energy_efficiency"));
	public static final RegistryKey<Enchantment> LUCKY = RegistryKey.of(RegistryKeys.ENCHANTMENT,
			Identifier.of(Base.MOD_ID, "lucky"));

	public static LoadOnceRegistryEntry<Enchantment> BALANCE_entry = enchantmentLoadOnceRegistryEntry(BALANCE);
	public static LoadOnceRegistryEntry<Enchantment> PLENTIFUL_entry = enchantmentLoadOnceRegistryEntry(PLENTIFUL);
	public static LoadOnceRegistryEntry<Enchantment> RUNE_BOOST_entry = enchantmentLoadOnceRegistryEntry(RUNE_BOOST);
	public static LoadOnceRegistryEntry<Enchantment> ENERGY_EFFICIENCY_entry = enchantmentLoadOnceRegistryEntry(
			ENERGY_EFFICIENCY);
	public static LoadOnceRegistryEntry<Enchantment> LUCKY_entry = enchantmentLoadOnceRegistryEntry(LUCKY);
	public static LoadOnceRegistryEntry<Enchantment> FAST_HIT_entry = enchantmentLoadOnceRegistryEntry(FAST_HIT);
	public static LoadOnceRegistryEntry<Enchantment> FIRE_ASPECT_entry = enchantmentLoadOnceRegistryEntry(
			Enchantments.FIRE_ASPECT);

	public static void initialize()
	{
	}

	private static LoadOnceRegistryEntry<Enchantment> enchantmentLoadOnceRegistryEntry(RegistryKey<Enchantment> enchantmentRegistryKey)
	{
		return getLoadOnceRegistryEntry(enchantmentRegistryKey, RegistryKeys.ENCHANTMENT);
	}
}
