package org.yang.iw.api.register;

import java.util.ArrayList;
import java.util.List;

public class LoadTime
{

	public enum Type
	{
		BEFORE_INITIALIZE, ON_INITIALIZE, AFTER_INITIALIZE
	}

	private static Type CURRENT = Type.BEFORE_INITIALIZE;

	private static List<Object> loadedClass = new ArrayList<>();

	public static Type currentLoadTime()
	{
		return CURRENT;
	}

	public static void finishInitialize()
	{
		CURRENT = Type.AFTER_INITIALIZE;
	}

	public static void startInitialize()
	{
		CURRENT = Type.ON_INITIALIZE;
	}

	public static void assertTime(Type... expected)
	{
		for (Type type : expected)
		{
			if (type == CURRENT) return;
		}
		throw new ExceptionInInitializerError("Accidentally load class at " + CURRENT + ".");
	}

	public static void assertLoaded(Class<?> cla)
	{
		if (loadedClass.contains(cla)) return;
		throw new ExceptionInInitializerError("Load Time assert error.");
	}

	public static void setLoaded(Class<?> cla)
	{
		loadedClass.add(cla);
	}

}
