package org.yang.iw;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.yang.iw.api.register.RegistryCollector;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.util.Base;

import static org.yang.iw.util.Base.MOD_ID;

@RegistryCollector
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
	public static final RegistryKey<Enchantment> RUNE_BOOST = enchant("rune_boost", "符文强化");
	public static final RegistryKey<Enchantment> ENERGY_EFFICIENCY = enchant("energy_efficiency", "聚能");
	public static final RegistryKey<Enchantment> LUCKY = enchant("lucky", "幸运");
}
