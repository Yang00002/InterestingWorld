package org.yang.iw.api.tag;

public interface IntrusiveTagHolder<T>
{
	IntrusiveTag<T> asTag();

	default void addInclude(T target)
	{
		asTag().addInclude(target);
	}

	default void addInclude(IntrusiveTag<T> target)
	{
		asTag().addInclude(target);
	}

	default boolean include(T target)
	{
		return asTag().include(target);
	}

	default boolean include(IntrusiveTag<T> target)
	{
		return asTag().include(target);
	}
}
