package org.yang.iw.enchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.util.Base;
import org.yang.iw.util.Server.LoadOnceRegistryEntry;

import static org.yang.iw.util.Base.MOD_ID;
import static org.yang.iw.util.Server.getLoadOnceRegistryEntry;

public class IWEnchantments
{
	private static RegistryKey<Enchantment> enchant(String id, String translation)
	{
		var r = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Base.MOD_ID, id));
		TranslationPool.addString("enchantment.%s.%s".formatted(MOD_ID, id), translation);
		return r;
	}

	public static final RegistryKey<Enchantment> FAST_HIT = enchant("fast_hit", "迅捷打击");
	public static final RegistryKey<Enchantment> FAST_ATTACK = enchant("fast_attack", "快速挥舞");
	public static final RegistryKey<Enchantment> BALANCE = enchant("balance", "均衡");
	public static final RegistryKey<Enchantment> PLENTIFUL = enchant("plentiful", "充盈");
	public static final RegistryKey<Enchantment> RUNE_BOOST = enchant("rune_boost", "符文强化");
	public static final RegistryKey<Enchantment> ENERGY_EFFICIENCY = enchant("energy_efficiency", "聚能");
	public static final RegistryKey<Enchantment> LUCKY = enchant("lucky", "幸运");
	public static LoadOnceRegistryEntry<Enchantment> ENTRY_BALANCE = enchantmentLoadOnceRegistryEntry(BALANCE);
	public static LoadOnceRegistryEntry<Enchantment> ENTRY_PLENTIFUL = enchantmentLoadOnceRegistryEntry(PLENTIFUL);
	public static LoadOnceRegistryEntry<Enchantment> ENTRY_RUNE_BOOST = enchantmentLoadOnceRegistryEntry(RUNE_BOOST);
	public static LoadOnceRegistryEntry<Enchantment> ENTRY_ENERGY_EFFICIENCY = enchantmentLoadOnceRegistryEntry(
			ENERGY_EFFICIENCY);
	public static LoadOnceRegistryEntry<Enchantment> ENTRY_LUCKY = enchantmentLoadOnceRegistryEntry(LUCKY);
	public static LoadOnceRegistryEntry<Enchantment> ENTRY_FAST_HIT = enchantmentLoadOnceRegistryEntry(FAST_HIT);
	public static LoadOnceRegistryEntry<Enchantment> ENTRY_FIRE_ASPECT = enchantmentLoadOnceRegistryEntry(
			Enchantments.FIRE_ASPECT);

	public static void initialize()
	{
	}

	private static LoadOnceRegistryEntry<Enchantment> enchantmentLoadOnceRegistryEntry(RegistryKey<Enchantment> enchantmentRegistryKey)
	{
		return getLoadOnceRegistryEntry(enchantmentRegistryKey, RegistryKeys.ENCHANTMENT);
	}
}
