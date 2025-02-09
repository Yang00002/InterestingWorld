package org.yang.iw.datagen.blockmodel;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.BlockStateModelGenerator;

import java.util.LinkedList;
import java.util.List;

public class BlockModelPool
{
	static List<BlockModelProvider> providers = new LinkedList<>();

	@Environment(EnvType.CLIENT)
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
