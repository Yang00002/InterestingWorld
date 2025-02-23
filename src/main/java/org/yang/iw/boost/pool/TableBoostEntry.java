package org.yang.iw.boost.pool;

import org.yang.iw.boost.AbstractBoost;

public class TableBoostEntry
{
	short currentLevel;
	short maxLevel;
	int currentCost;
	int nextLevelCost;
	int weight;
	boolean unlimit;
	AbstractBoost boost;
	TableBoostEntry pre;
	TableBoostEntry next;

	TableBoostEntry()
	{
		currentLevel = 0;
		currentCost = 0;
		nextLevelCost = 0;
		weight = 0;
		boost = null;
		pre = null;
		next = null;
		maxLevel = 0;
	}

	TableBoostEntry(int nextLevelCost, int weight, AbstractBoost boost, TableBoostEntry pre, TableBoostEntry next)
	{
		this.currentLevel = 0;
		this.maxLevel = boost.maxTableLevel();
		this.weight = weight;
		this.currentCost = 0;
		this.nextLevelCost = nextLevelCost;
		this.unlimit = false;
		this.boost = boost;
		this.pre = pre;
		this.next = next;
	}

	public short costAchieveLevel(int cost)
	{
		if (maxLevel <= currentLevel) return currentLevel;
		return boost.costAchieveLevel((short) (currentLevel + 1), cost + currentCost);
	}

	public int setLevel(short level)
	{
		int cost = nextLevelCost + boost.xpCostBetweenLevels((short) (currentLevel + 1), level);
		currentCost += cost;
		currentLevel = level;
		if (currentLevel >= maxLevel) return cost;
		nextLevelCost = boost.xpCostBetweenLevels(currentLevel, (short) (currentLevel + 1));
		return cost;
	}

	public boolean setUnlimit()
	{
		if (boost.maxRandomLevel() == maxLevel) return false;
		unlimit = true;
		maxLevel = boost.maxRandomLevel();
		return true;
	}


	public boolean reachMax()
	{
		return currentLevel == maxLevel;
	}
}
