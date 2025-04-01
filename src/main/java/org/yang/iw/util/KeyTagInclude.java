package org.yang.iw.util;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class KeyTagInclude<T>
{
	List<Supplier<RegistryKey<T>>> items = null;
	List<TagKey<T>> tags = null;

	public KeyTagInclude<T> add(Supplier<RegistryKey<T>> item)
	{
		if (items == null) items = new ArrayList<>();
		items.add(item);
		return this;
	}

	public KeyTagInclude<T> add(TagKey<T> tag)
	{
		if (tags == null) tags = new ArrayList<>();
		tags.add(tag);
		return this;
	}

	public void map(Consumer<RegistryKey<T>> itemConsumer, Consumer<TagKey<T>> tagKeyConsumer)
	{
		if (items != null) items.forEach(i -> itemConsumer.accept(i.get()));
		if (tags != null) tags.forEach(tagKeyConsumer);
	}

}
