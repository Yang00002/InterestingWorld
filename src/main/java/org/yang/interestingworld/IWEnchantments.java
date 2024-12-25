package org.yang.interestingworld;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.util.Base;

import java.util.Optional;

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

	public static RegistryEntry<Enchantment> ENTRY_BALANCE = null;
	public static RegistryEntry<Enchantment> ENTRY_PLENTIFUL = null;
	public static RegistryEntry<Enchantment> ENTRY_RUNE_BOOST = null;
	public static RegistryEntry<Enchantment> ENTRY_ENERGY_EFFICIENCY = null;
	public static RegistryEntry<Enchantment> ENTRY_LUCKY = null;

	public static void initialize()
	{
	}

	public static void load_entry(RegistryWrapper<Enchantment> wrapper)
	{
		Optional<RegistryEntry.Reference<Enchantment>> registryEntry = wrapper.getOptional(BALANCE);
		ENTRY_BALANCE = registryEntry.orElse(null);
		registryEntry = wrapper.getOptional(PLENTIFUL);
		ENTRY_PLENTIFUL = registryEntry.orElse(null);
		registryEntry = wrapper.getOptional(RUNE_BOOST);
		ENTRY_RUNE_BOOST = registryEntry.orElse(null);
		registryEntry = wrapper.getOptional(ENERGY_EFFICIENCY);
		ENTRY_ENERGY_EFFICIENCY = registryEntry.orElse(null);
		registryEntry = wrapper.getOptional(LUCKY);
		ENTRY_LUCKY = registryEntry.orElse(null);
	}

	public static void pop_entry()
	{
		ENTRY_BALANCE = null;
		ENTRY_PLENTIFUL = null;
		ENTRY_RUNE_BOOST = null;
		ENTRY_ENERGY_EFFICIENCY = null;
		ENTRY_LUCKY = null;
	}
}
