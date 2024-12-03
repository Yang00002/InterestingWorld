package org.yang.interestingworld.util;

import java.util.Random;

public class Rand extends Random
{
	public int nextEvenInt(int min, int max)
	{
		if (min == max) return min;
		return nextInt(min, max + 1);
	}

	public int nextTriangleInt(int min, int max)
	{
		if (min == max) return min;
		int m = max + 1;
		return (nextInt(min, m) + nextInt(min, m)) / 2;
	}
}
