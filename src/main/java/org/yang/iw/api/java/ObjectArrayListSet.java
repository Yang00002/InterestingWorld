package org.yang.iw.api.java;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

public final class ObjectArrayListSet
{

	public ObjectArrayListSet()
	{
		this.map = new Object2ObjectOpenHashMap<>();
	}

	private final Object2ObjectOpenHashMap<Class<?>, ObjectArrayList<Object>> map;

	public <T> @Nullable ObjectArrayList<T> get(Class<T> type)
	{
		return (ObjectArrayList<T>) map.getOrDefault(type, null);
	}

	public <T> void add(Class<T> type, T value)
	{
		var list = map.getOrDefault(type, null);
		if (list == null) list = new ObjectArrayList<>();
		list.add(value);
		map.put(type, list);
	}

	public <T> void addList(Class<T> type, ObjectArrayList<T> values)
	{
		var list = map.getOrDefault(type, null);
		if (list == null) list = new ObjectArrayList<>();
		list.addAll(values);
		map.put(type, list);
	}

	public void addAll(ObjectArrayListSet set2)
	{
		set2.map.forEach((type, value) -> {
			var list = map.getOrDefault(type, null);
			if (list == null) list = new ObjectArrayList<>();
			list.addAll(value);
			map.put(type, list);
		});
	}
}
