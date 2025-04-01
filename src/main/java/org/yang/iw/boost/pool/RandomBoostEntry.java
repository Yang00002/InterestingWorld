package org.yang.iw.boost.pool;

import net.minecraft.registry.tag.TagKey;
import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.util.constants.Numbers;

public class RandomBoostEntry
{
	short currentLevel;
	short maxLevel;
	int currentCost_B;
	int nextLevelCost_B;
	int weight;
	float costMultiplier = 1.0f;
	int baseCost = 0;
	AbstractBoost boost;
	RandomBoostEntry pre;
	RandomBoostEntry next;

	RandomBoostEntry()
	{
		currentLevel = 0;
		currentCost_B = 0;
		nextLevelCost_B = 0;
		weight = 0;
		boost = null;
		pre = null;
		next = null;
		maxLevel = 0;
	}

	RandomBoostEntry(int nextLevelCost_s, int weight, AbstractBoost boost, RandomBoostEntry pre, RandomBoostEntry next)
	{
		this.currentLevel = 0;
		this.maxLevel = boost.maxRandomLevel();
		this.weight = weight;
		this.currentCost_B = 0;
		this.nextLevelCost_B = nextLevelCost_s;
		this.boost = boost;
		this.pre = pre;
		this.next = next;
	}

	public boolean isIn(TagKey<AbstractBoost> key)
	{
		return boost.isIn(key);
	}

	public short costAchieveLevel(int cost_B)
	{
		if (maxLevel <= currentLevel) return currentLevel;
		cost_B += currentCost_B;
		int tryCost_s = (int) ((cost_B - baseCost) / costMultiplier);
		int lvl = boost.costAchieveLevel(currentLevel, maxLevel, tryCost_s);
		if (lvl < maxLevel && boost.xpCostOfLevel((short) (lvl + 1)) <= cost_B) return (short) (lvl + 1);
		return (short) lvl;
	}

	public int setLevel(short level)
	{
		int cost = (int) (boost.xpCostOfLevel(level) * costMultiplier) + baseCost - currentCost_B;
		currentCost_B += cost;
		currentLevel = level;
		if (currentLevel >= maxLevel) return cost;
		nextLevelCost_B = ((int) (boost.xpCostOfLevel((short) (currentLevel + 1)) * costMultiplier)) -
						  ((int) (boost.xpCostOfLevel(currentLevel) * costMultiplier));
		return cost;
	}

	public void setWeight(RandomBoostGenerator generator, int nextWeight)
	{
		if (nextWeight < 0) nextWeight = 0;
		generator.maxWeight += nextWeight - weight;
		weight = nextWeight;
	}

	public int getWeight()
	{
		return weight;
	}

	public void setCostMultiplier(float multiplier)
	{
		if (multiplier > 1.0F - Numbers.FLOAT_EPSILON)
		{
			costMultiplier = multiplier;
			if (currentLevel == 0) nextLevelCost_B = ((int) (boost.xpCostOfLevel((short) 1) * multiplier)) + baseCost;
			else nextLevelCost_B = ((int) (boost.xpCostOfLevel((short) (currentLevel + 1)) * multiplier)) -
								   ((int) (boost.xpCostOfLevel(currentLevel) * multiplier));
		}
	}

	public float getCostMultiplier()
	{
		return costMultiplier;
	}

	public int getBaseCost()
	{
		return baseCost;
	}

	public void setBaseCost(int cost)
	{
		if (cost >= 0)
		{
			baseCost = cost;
			if (currentLevel == 0) nextLevelCost_B = ((int) (boost.xpCostOfLevel((short) 1) * costMultiplier)) + cost;
			else nextLevelCost_B = ((int) (boost.xpCostOfLevel((short) (currentLevel + 1)) * costMultiplier)) -
								   ((int) (boost.xpCostOfLevel(currentLevel) * costMultiplier));
		}
	}

	public boolean reachMax()
	{
		return currentLevel == maxLevel;
	}
}
