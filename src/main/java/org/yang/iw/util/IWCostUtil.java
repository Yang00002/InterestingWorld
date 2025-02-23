package org.yang.iw.util;

import org.jetbrains.annotations.NotNull;

import java.util.Random;


public class IWCostUtil
{

	public static byte getWorldLevelOfXpCost(int cost)
	{
		if (cost >= 2045)
		{
			if (cost >= 4020)
			{
				if (cost >= 5345) return 10;
				return 9;
			}
			if (cost >= 2920) return 8;
			return 7;
		}
		if (cost >= 550)
		{
			if (cost >= 1395) return 6;
			if (cost >= 910) return 5;
			return 4;
		}
		if (cost >= 160)
		{
			if (cost >= 315) return 3;
			return 2;
		}
		if (cost >= 55) return 1;
		return 0;
	}

	//									   0   1    2    3    4    5     6     7     8   9     10   11
	private static final int[] xp_costs = {0, 55, 160, 315, 550, 910, 1395, 2045, 2920, 4020, 5345, 8670};

	public static int getMaxAllowXpCostOfWorldLevel(int level)
	{
		level = Math.min(level, 10);
		return xp_costs[level + 1] - 1;
	}

	public static int getRandomXpCostOfWorldLevel(int level, @NotNull Random random)
	{
		level = Math.min(level, 10);
		return Math.max(random.nextInt(xp_costs[level], xp_costs[level + 1]),
				random.nextInt(xp_costs[level], xp_costs[level + 1]));
	}

	public static int getLevelFromExperience(int cost)
	{
		int count = 0;
		int level = 1;
		while (getExperienceFromLevel(level) < cost)
		{
			level <<= 1;
			if (level <= 0) return 0;
		}
		if (level <= 2) return level;
		int left = level >> 1;
		int right = level;
		while (true)
		{
			count++;
			if (count > 10000) throw new RuntimeException("infinite Loop");
			int mid = (left + right) >> 1;
			int c = getExperienceFromLevel(mid);
			if (c >= cost)
			{
				if ((mid & 1) == 1) return mid;
				right = mid;
			}
			else
			{
				if ((mid & 1) == 1) return mid + 1;
				left = mid;
			}
		}
	}

	private static int getExperienceFromLevel(int level)
	{
		if (level < 17) return (level + 6) * level;
		else if (level < 32) return (int) (((2.5f * level - 40.5f) * level) + 360);
		else return (int) ((4.5f * level - 162.5f) * level) + 2220;
	}

	public static int getExperienceFromLevel(int level, float frac)
	{
		int base;
		if (level < 17)
		{
			base = (level + 6) * level;
			if (level < 16) return base + (int) ((2 * level + 7) * frac);
			else return base + (int) ((5 * level - 38) * frac);
		}
		else if (level < 32)
		{
			base = (int) ((2.5f * level - 40.5f) * level) + 360;
			if (level < 31) return base + (int) ((5 * level - 38) * frac);
			else return base + (int) ((9 * level - 158) * frac);
		}
		else
		{
			base = (int) ((4.5f * level - 162.5f) * level) + 2220;
			return base + (int) ((9 * level - 158) * frac);
		}
	}
}