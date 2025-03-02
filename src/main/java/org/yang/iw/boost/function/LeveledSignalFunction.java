package org.yang.iw.boost.function;

public abstract class LeveledSignalFunction
{
	public enum Signal
	{
		MENDING
	}

	public abstract Signal type();

	public abstract int level();
}
