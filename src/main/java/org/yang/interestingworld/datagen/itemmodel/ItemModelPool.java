package org.yang.interestingworld.datagen.itemmodel;

import net.minecraft.data.client.ItemModelGenerator;

import java.util.LinkedList;
import java.util.List;

public class ItemModelPool
{
	static List<ItemModelProvider> providers = new LinkedList<>();

	public static void generatePool(ItemModelGenerator generator)
	{
		providers.forEach(i -> i.use(generator));
	}

	public static void addModel(ItemModelProvider provider)
	{
		providers.add(provider);
	}

	public static void clearPool()
	{
		providers = null;
	}
}
