package org.yang.iw.datagen.tag;

import com.mojang.datafixers.util.Either;
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
	static Map<TagKey<Item>, List<Either<Item, TagKey<Item>>>> tagMap = new HashMap<>();

	public static void generatePool(Function<TagKey<Item>, FabricTagProvider<Item>.FabricTagBuilder> provider)
	{
		for (var kv : tagMap.entrySet())
		{
			var c = provider.apply(kv.getKey());
			kv.getValue().forEach(either -> either.map(c::add, c::addTag));
		}
	}

	public static void add(TagKey<Item> tag, Item item)
	{
		var m = tagMap.getOrDefault(tag, null);
		if (m == null)
		{
			List<Either<Item, TagKey<Item>>> l = new LinkedList<>();
			l.add(Either.left(item));
			tagMap.put(tag, l);
		}
		else m.add(Either.left(item));
	}

	public static void add(TagKey<Item> tag, TagKey<Item> items)
	{
		var m = tagMap.getOrDefault(tag, null);
		if (m == null)
		{
			List<Either<Item, TagKey<Item>>> l = new LinkedList<>();
			l.add(Either.right(items));
			tagMap.put(tag, l);
		}
		else m.add(Either.right(items));
	}

	public static void clearPool()
	{
		tagMap = null;
	}
}
