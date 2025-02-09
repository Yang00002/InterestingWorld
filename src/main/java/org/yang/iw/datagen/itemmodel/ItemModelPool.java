package org.yang.iw.datagen.itemmodel;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.ItemModelGenerator;

import java.util.LinkedList;
import java.util.List;

public class ItemModelPool
{
	static List<ItemModelProvider> providers = new LinkedList<>();

	@Environment(EnvType.CLIENT)
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
