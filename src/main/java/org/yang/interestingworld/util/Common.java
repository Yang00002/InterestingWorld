package org.yang.interestingworld.util;

public class Common
{
	public static String splitStrAndGetEnd(String string, String del)
	{
		var s = string.split(del);
		return s[s.length - 1];
	}

	public static String splitStrAndGetFirst(String string, String del)
	{
		var s = string.split(del);
		return s[0];
	}
}
