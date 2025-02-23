package org.yang.iw.datagen.itemmodel;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.ItemModelGenerator;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

public class ItemModelPool
{
	static Queue<Supplier<ItemModelProvider>> providers = new LinkedList<>();

	@Environment(EnvType.CLIENT)
	public static void generatePool(ItemModelGenerator generator)
	{
		while (!providers.isEmpty()) providers.poll().get().use(generator);
	}

	public static void addModel(Supplier<ItemModelProvider> provider)
	{
		providers.add(provider);
	}

	public static void clearPool()
	{
		providers = null;
	}
}
