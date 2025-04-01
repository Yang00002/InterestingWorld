package org.yang.iw.api.java;

import java.util.ArrayList;

public class Booleans
{
	ArrayList<Boolean> arrayList = new ArrayList<>();
	final boolean defaultValue;

	public Booleans(boolean defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	public void add(boolean value)
	{
		arrayList.add(value);
	}

	public boolean or()
	{
		if (arrayList.isEmpty()) return defaultValue;
		for (var val : arrayList) if (val) return true;
		return false;
	}

	public boolean and()
	{
		if (arrayList.isEmpty()) return defaultValue;
		for (var val : arrayList) if (!val) return false;
		return true;
	}
}
