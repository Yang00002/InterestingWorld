package org.yang.interestingworld.resource.enchant;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public class ToolGroupData
{
	static Map<Item, String> Data;

	public static Collection<String> getGroups()
	{
		return Data.values();
	}

	@Nullable
	public static String getGroupOfItemStack(ItemStack stack)
	{
		return Data.getOrDefault(stack.getItem(), null);
	}
}
