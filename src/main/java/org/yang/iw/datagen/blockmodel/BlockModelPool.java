package org.yang.iw.datagen.blockmodel;

import net.minecraft.data.client.BlockStateModelGenerator;

import java.util.LinkedList;
import java.util.List;

public class BlockModelPool
{
	static List<BlockModelProvider> providers = new LinkedList<>();

	public static void generatePool(BlockStateModelGenerator generator)
	{
		providers.forEach(i -> i.use(generator));
	}

	public static void addModel(BlockModelProvider provider)
	{
		providers.add(provider);
	}

	public static void clearPool()
	{
		providers = null;
	}
}
