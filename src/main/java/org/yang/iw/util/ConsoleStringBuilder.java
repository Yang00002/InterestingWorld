package org.yang.iw.util;

public class ConsoleStringBuilder
{
	String spaces = "";

	StringBuilder builder = new StringBuilder();

	public ConsoleStringBuilder newLine()
	{
		builder.append('\n');
		return this;
	}

	public ConsoleStringBuilder tab()
	{
		spaces += '\t';
		return this;
	}

	public ConsoleStringBuilder reTab()
	{
		if (!spaces.isEmpty()) spaces = spaces.substring(0, spaces.length() - 1);
		return this;
	}

	public ConsoleStringBuilder append(String string)
	{
		String[] strings = string.split("\n");
		if (strings.length == 0) return this;
		builder.append(spaces).append(strings[0]);
		for (int i = 1; i < strings.length; i++)
		{
			builder.append('\n');
			builder.append(spaces).append(strings[i]);
		}
		return this;
	}

	@Override
	public String toString()
	{
		return builder.toString();
	}
}
