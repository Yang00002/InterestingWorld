package org.yang.interestingworld.util;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class CPPStyleIterator<T>
{
	private ListIterator<T> iterator;
	private T value;

	public static <T> CPPStyleIterator<T> beginOf(List<T> l)
	{
		CPPStyleIterator<T> i = new CPPStyleIterator<>();
		i.iterator = l.listIterator();
		if (i.iterator.hasNext()) i.value = i.iterator.next();
		else i.value = null;
		return i;
	}

	public void toNext()
	{
		if (hasNext()) value = iterator.next();
		else value = null;
	}

	public boolean hasNext()
	{
		return iterator.hasNext();
	}

	public boolean isNull()
	{
		return value == null && !hasNext();
	}

	public boolean notNull()
	{
		return value != null || hasNext();
	}

	public void remove()
	{
		iterator.remove();
		value = null;
	}

	public T getValue()
	{
		return value;
	}
}
