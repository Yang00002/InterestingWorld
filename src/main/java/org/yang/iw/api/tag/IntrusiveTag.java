package org.yang.iw.api.tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class IntrusiveTag<T>
{
	final List<IntrusiveTag<T>> subTags = new ArrayList<>();
	final List<T> targets = new ArrayList<>();

	public IntrusiveTag<T> addInclude(T target)
	{
		if (include(target)) return this;
		targets.add(target);
		return this;
	}

	public IntrusiveTag<T> addInclude(IntrusiveTag<T> target)
	{
		if (this == target) return this;
		if (include(target) || target.include(this)) return this;
		subTags.add(target);
		return this;
	}

	public boolean include(T target)
	{
		for (var i : targets)
			if (i == target) return true;
		for (var i : subTags)
			if (i.include(target)) return true;
		return false;
	}

	public boolean include(IntrusiveTag<T> target)
	{
		for (var i : subTags)
			if (i == target || i.include(target)) return true;
		return false;
	}

	public void iterateContent(Consumer<T> consumer)
	{
		Set<T> applied = new HashSet<>();
		iterateContentInner(consumer, applied);
	}

	private void iterateContentInner(Consumer<T> consumer, Set<T> applied)
	{
		for (var i : targets)
		{
			if (!applied.contains(i))
			{
				applied.add(i);
				consumer.accept(i);
			}
		}
		for (var i : subTags)
			i.iterateContentInner(consumer, applied);
	}
}
