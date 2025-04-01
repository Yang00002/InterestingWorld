package org.yang.iw.api.java;

import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;

public class PiledShortMap<T>
{
	Object2ShortOpenHashMap<T> map = new Object2ShortOpenHashMap<>();

	public void set(T t, int value)
	{
		map.put(t, (short) value);
	}

	public void add(T t, int value)
	{
		map.put(t, (short) (value + map.getOrDefault(t, (short) 0)));
	}

	public Object2ShortOpenHashMap<T> asMap()
	{
		return map;
	}
}
