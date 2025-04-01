package org.yang.iw.api.tag;

import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TagInclude<T>
{
	List<Supplier<T>> items = null;
	List<TagKey<T>> tags = null;

	public TagInclude<T> add(Supplier<T> item)
	{
		if (items == null) items = new ArrayList<>();
		items.add(item);
		return this;
	}

	public TagInclude<T> add(TagKey<T> tag)
	{
		if (tags == null) tags = new ArrayList<>();
		tags.add(tag);
		return this;
	}

	public void map(Consumer<T> itemConsumer, Consumer<TagKey<T>> tagKeyConsumer)
	{
		if (items != null) items.forEach(i -> itemConsumer.accept(i.get()));
		if (tags != null) tags.forEach(tagKeyConsumer);
	}

}
