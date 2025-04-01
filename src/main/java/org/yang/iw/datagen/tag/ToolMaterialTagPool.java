package org.yang.iw.datagen.tag;

import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.tag.TagKey;
import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.tool.material.ToolMaterial;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ToolMaterialTagPool
{
	static Map<TagKey<ToolMaterial>, List<Either<ToolMaterial, TagKey<ToolMaterial>>>> tagMap = new HashMap<>();

	public static void generatePool(Function<TagKey<ToolMaterial>, FabricTagProvider<ToolMaterial>.FabricTagBuilder> provider)
	{
		for (var kv : tagMap.entrySet())
		{
			var c = provider.apply(kv.getKey());
			kv.getValue().forEach(either -> either.map(c::add, c::addTag));
		}
	}

	public static void add(TagKey<ToolMaterial> tag, ToolMaterial item)
	{
		var m = tagMap.getOrDefault(tag, null);
		if (m == null)
		{
			List<Either<ToolMaterial, TagKey<ToolMaterial>>> l = new LinkedList<>();
			l.add(Either.left(item));
			tagMap.put(tag, l);
		}
		else m.add(Either.left(item));
	}

	public static void add(TagKey<ToolMaterial> tag, TagKey<ToolMaterial> items)
	{
		var m = tagMap.getOrDefault(tag, null);
		if (m == null)
		{
			List<Either<ToolMaterial, TagKey<ToolMaterial>>> l = new LinkedList<>();
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
