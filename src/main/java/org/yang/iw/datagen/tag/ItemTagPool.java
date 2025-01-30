package org.yang.iw.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ItemTagPool
{
	static Map<TagKey<Item>, List<Item>> tagMap = new HashMap<>();

	public static void generatePool(Function<TagKey<Item>, FabricTagProvider<Item>.FabricTagBuilder> provider)
	{
		for (var kv : tagMap.entrySet())
		{
			var c = provider.apply(kv.getKey());
			kv.getValue().forEach(c::add);
		}
	}

	public static void add(TagKey<Item> tag, Item item)
	{
		var m = tagMap.getOrDefault(tag, null);
		if (m == null)
		{
			List<Item> l = new LinkedList<>();
			l.add(item);
			tagMap.put(tag, l);
		}
		else m.add(item);
	}

	public static void clearPool()
	{
		tagMap = null;
	}
}
