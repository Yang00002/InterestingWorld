package org.yang.iw.boost;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import org.yang.iw.api.register.LoadTime;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentToBoostFunction
{
	private static final Map<RegistryKey<Enchantment>, AbstractBoost> EnchantmentBoostMap;

	static
	{
		LoadTime.assertLoaded(IWBoosts.class);
		EnchantmentBoostMap = new HashMap<>();
		EnchantmentBoostMap.put(Enchantments.FIRE_ASPECT, IWBoosts.FIRE_ASPECT);
		EnchantmentBoostMap.put(Enchantments.LOOTING, IWBoosts.LOOTING);
		EnchantmentBoostMap.put(Enchantments.SHARPNESS, IWBoosts.SHARPNESS);
		EnchantmentBoostMap.put(Enchantments.UNBREAKING, IWBoosts.UNBREAKING);
		EnchantmentBoostMap.put(Enchantments.SWEEPING_EDGE, IWBoosts.SWEEPING_EDGE);
		EnchantmentBoostMap.put(Enchantments.MENDING, IWBoosts.MENDING);
	}

	public static AbstractBoost fromEnchantToBoost(RegistryEntry<Enchantment> registryEntry)
	{
		return registryEntry.getKey().map(key -> EnchantmentBoostMap.getOrDefault(key, null)).orElse(null);
	}
}
