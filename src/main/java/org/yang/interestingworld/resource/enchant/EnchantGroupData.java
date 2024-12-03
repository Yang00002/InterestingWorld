package org.yang.interestingworld.resource.enchant;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EnchantGroupData
{
	static Map<String, Set<RuneEnchantData>> Data;

	static class IO
	{
		List<String> values = null;
	}

	@Nullable
	public static Set<RuneEnchantData> getEnchantGroupOfItemStack(ItemStack stack)
	{
		String key = ToolGroupData.getGroupOfItemStack(stack);
		if (key == null) return null;
		var set = Data.getOrDefault(key, null);
		if (set == null) return null;
		return Collections.unmodifiableSet(set);
	}

	@Nullable
	public static Set<RuneEnchantData> copyEnchantGroupOfItemStack(ItemStack stack)
	{
		Item item = stack.getItem();
		String key = ToolGroupData.getGroupOfItemStack(stack);
		if (key == null) return null;
		var set = Data.getOrDefault(key, null);
		if (set == null) return null;
		return new HashSet<>(set);
	}
}
