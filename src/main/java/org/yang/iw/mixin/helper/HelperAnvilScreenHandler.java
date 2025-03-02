package org.yang.iw.mixin.helper;

import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.boost.EnchantmentToBoostFunction;
import org.yang.iw.component.BoostComponent;

public class HelperAnvilScreenHandler
{


	public static boolean entryMatch(RegistryEntry<Enchantment> entry, ItemStack target)
	{
		return entry.value().isPrimaryItem(target);
	}

	public static BoostComponent hitAddBoost(ItemStack left, ItemStack right)
	{
		Object2ShortOpenHashMap<AbstractBoost> boostObject2ShortOpenHashMap = new Object2ShortOpenHashMap<>();
		if (!left.interestingWorld$getBoosts().isEmpty())
		{
			var entries = right.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT)
					.getEnchantmentEntries();
			for (var entry : entries)
			{
				var key = entry.getKey();
				if (entryMatch(key, left)) continue;
				var boost = EnchantmentToBoostFunction.fromEnchantToBoost(key);
				if (boost == null) continue;
				int lvl = Math.clamp(entry.getIntValue(), 1, boost.maxAllowLevel());
				boostObject2ShortOpenHashMap.put(boost, (short) lvl);
			}
			if (boostObject2ShortOpenHashMap.isEmpty()) return null;
			return BoostComponent.createLegal(boostObject2ShortOpenHashMap, true);
		}
		if (!left.getEnchantments().isEmpty())
		{
			var entries = right.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT)
					.getEnchantmentEntries();
			for (var entry : entries)
			{
				var key = entry.getKey();
				if (entryMatch(key, left)) return BoostComponent.DEFAULT;
			}
			return null;
		}
		boolean append = false;
		var entries = right.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT)
				.getEnchantmentEntries();
		for (var entry : entries)
		{
			var key = entry.getKey();
			if (entryMatch(key, left))
			{
				append = true;
				continue;
			}
			var boost = EnchantmentToBoostFunction.fromEnchantToBoost(key);
			if (boost == null) continue;
			int lvl = Math.clamp(entry.getIntValue(), 1, boost.maxAllowLevel());
			boostObject2ShortOpenHashMap.put(boost, (short) lvl);
		}
		if (boostObject2ShortOpenHashMap.isEmpty())
		{
			if (append) return BoostComponent.DEFAULT;
			return null;
		}
		return BoostComponent.createLegal(boostObject2ShortOpenHashMap, true);
	}
}
