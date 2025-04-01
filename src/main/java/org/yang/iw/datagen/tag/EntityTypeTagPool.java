package org.yang.iw.datagen.tag;

import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.tag.TagKey;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class EntityTypeTagPool
{
	static Map<TagKey<EntityType<?>>, List<Either<EntityType<?>, TagKey<EntityType<?>>>>> tagMap = new HashMap<>();

	public static void generatePool(Function<TagKey<EntityType<?>>, FabricTagProvider<EntityType<?>>.FabricTagBuilder> provider)
	{
		for (var kv : tagMap.entrySet())
		{
			var c = provider.apply(kv.getKey());
			kv.getValue().forEach(either -> either.map(c::add, c::addOptionalTag));
		}
	}

	public static void add(TagKey<EntityType<?>> tag, EntityType<?> entity)
	{
		var m = tagMap.getOrDefault(tag, null);
		if (m == null)
		{
			List<Either<EntityType<?>, TagKey<EntityType<?>>>> l = new LinkedList<>();
			l.add(Either.left(entity));
			tagMap.put(tag, l);
		}
		else m.add(Either.left(entity));
	}

	public static void add(TagKey<EntityType<?>> tag, TagKey<EntityType<?>> entities)
	{
		var m = tagMap.getOrDefault(tag, null);
		if (m == null)
		{
			List<Either<EntityType<?>, TagKey<EntityType<?>>>> l = new LinkedList<>();
			l.add(Either.right(entities));
			tagMap.put(tag, l);
		}
		else m.add(Either.right(entities));
	}

	public static void clearPool()
	{
		tagMap = null;
	}
}
