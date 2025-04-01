package org.yang.iw.api.java;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TableStringBuilder
{
	final int perTabLen;
	final int perEngLen;
	final int perChiLen;

	int currentTab;

	private class Element
	{
		String data = "";
		int tabCount = 0;
		int len;

		public void add(String string)
		{
			for (char c : string.toCharArray())
			{
				if (Character.toString(c).matches("[\\u0080-\\uFFFF]"))
				{
					len += perChiLen;
				}
				else
				{
					len += perEngLen;
				}
			}
			data += string;
		}

		void dump(StringBuilder builder)
		{
			builder.append(data);
			if (tabCount > 0) builder.append("\t".repeat(tabCount));
		}
	}

	private class Line
	{
		List<Element> data = new LinkedList<>();
		Element currentElement = new Element();
		int spaceCount;

		Line(int count)
		{
			spaceCount = count;
		}

		void addCurrent(String str)
		{
			currentElement.add(str);
		}

		void newElement()
		{
			data.add(currentElement);
			currentElement = new Element();
		}

		void dump(StringBuilder builder)
		{
			if (spaceCount > 0) builder.append("\t".repeat(spaceCount));
			for (var ele : data) ele.dump(builder);
			currentElement.dump(builder);
		}
	}

	List<Line> lines = new LinkedList<>();

	Line currentLine;

	public TableStringBuilder(int perTabLen, int perEngLen, int perChiLen, int tabCount)
	{
		this.perTabLen = perTabLen;
		this.perEngLen = perEngLen;
		this.perChiLen = perChiLen;
		currentTab = Math.max(tabCount, 0);
		currentLine = new Line(currentTab);
	}

	public static TableStringBuilder jetBrianMono(int tabCount)
	{
		return new TableStringBuilder(28, 7, 11, tabCount);
	}

	public TableStringBuilder newLine()
	{
		lines.add(currentLine);
		currentLine = new Line(currentTab);
		return this;
	}

	public TableStringBuilder tab()
	{
		currentTab++;
		return this;
	}

	public TableStringBuilder reTab()
	{
		currentTab--;
		return this;
	}

	public TableStringBuilder append(String string)
	{
		String[] stringLines = string.split("\n", -1);
		if (stringLines.length == 0) return this;
		String[] elements0 = stringLines[0].split("\t", -1);
		var pre0 = elements0[0];
		currentLine.addCurrent(pre0);
		for (int j = 1; j < elements0.length; j++)
		{
			currentLine.newElement();
			currentLine.addCurrent(elements0[j]);
		}
		for (int i = 1; i < stringLines.length; i++)
		{
			newLine();
			String[] elements = stringLines[i].split("\t");
			var pre = elements[0];
			currentLine.addCurrent(pre);
			for (int j = 1; j < elements.length; j++)
			{
				currentLine.newElement();
				currentLine.addCurrent(elements[j]);
			}
		}
		return this;
	}

	public TableStringBuilder append(int integer)
	{
		String string = String.valueOf(integer);
		currentLine.addCurrent(string);
		return this;
	}

	@Override
	public String toString()
	{
		Map<Integer, List<Line>> lineMap = new HashMap<>();
		List<Line> l = new LinkedList<>();
		l.add(currentLine);
		lineMap.put(currentLine.spaceCount, l);
		for (var line : lines)
		{
			l = lineMap.getOrDefault(line.spaceCount, new LinkedList<>());
			l.add(line);
			lineMap.put(line.spaceCount, l);
		}
		lineMap.forEach((space, lineCollection) -> {
			Map<Integer, List<Element>> elementMap = new HashMap<>();
			lineCollection.forEach((line) -> {
				if (!line.data.isEmpty())
				{
					List<Element> l1;
					int idx = 0;
					for (var element : line.data)
					{
						l1 = elementMap.getOrDefault(idx, new LinkedList<>());
						l1.add(element);
						elementMap.put(idx++, l1);
					}
				}
			});
			elementMap.forEach((idx, elementCollection) -> {
				int maxSize = 0;
				for (var element : elementCollection)
					if (maxSize < element.len) maxSize = element.len;
				int tabCount = maxSize / perTabLen + 1;
				for (var element : elementCollection)
				{
					int preTabCount = (element.len + 1) / perTabLen;
					element.tabCount = tabCount - preTabCount;
				}
			});
		});
		StringBuilder builder = new StringBuilder();
		for (var line : lines)
		{
			line.dump(builder);
			builder.append('\n');
		}
		currentLine.dump(builder);
		return builder.toString();
	}
}
