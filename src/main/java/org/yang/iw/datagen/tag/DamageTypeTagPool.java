package org.yang.iw.datagen.tag;

import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DamageTypeTagPool
{
	static Map<TagKey<DamageType>, List<Either<RegistryKey<DamageType>, TagKey<DamageType>>>> tagMap = new HashMap<>();

	public static void generatePool(Function<TagKey<DamageType>, FabricTagProvider<DamageType>.FabricTagBuilder> provider)
	{
		for (var kv : tagMap.entrySet())
		{
			var c = provider.apply(kv.getKey());
			kv.getValue().forEach(either -> either.map(c::addOptional, c::addOptionalTag));
		}
	}

	public static void add(TagKey<DamageType> tag, RegistryKey<DamageType> damageType)
	{
		var m = tagMap.getOrDefault(tag, null);
		if (m == null)
		{
			List<Either<RegistryKey<DamageType>, TagKey<DamageType>>> l = new LinkedList<>();
			l.add(Either.left(damageType));
			tagMap.put(tag, l);
		}
		else m.add(Either.left(damageType));
	}

	public static void add(TagKey<DamageType> tag, TagKey<DamageType> items)
	{
		var m = tagMap.getOrDefault(tag, null);
		if (m == null)
		{
			List<Either<RegistryKey<DamageType>, TagKey<DamageType>>> l = new LinkedList<>();
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
