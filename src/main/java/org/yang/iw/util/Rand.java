package org.yang.iw.util;

import java.util.Random;

public class Rand extends Random
{
	public Rand(int randomSeed)
	{
		super(randomSeed);
	}
	public Rand()
	{
	}

	public int nextEvenInt(int min, int max)
	{
		return nextInt(min, max + 1);
	}

	public int nextTriangleInt(int min, int max)
	{
		int m = max + 1;
		return (nextInt(min, m) + nextInt(min, m)) >> 1;
	}
}
