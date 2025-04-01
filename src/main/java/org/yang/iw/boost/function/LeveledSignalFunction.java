package org.yang.iw.boost.function;

public abstract class LeveledSignalFunction
{
	public enum Signal
	{
		MENDING, ENERGY_SAVING
	}

	public abstract Signal type();

	public abstract int level();
}
