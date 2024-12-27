package org.yang.interestingworld.enchant.util;

import org.yang.interestingworld.enchant.resource.EnchantData;

public class TableEnchantEntry
{
	int weight;
	boolean choosen;
	boolean remove;
	byte maxLevel;
	byte currentLevel;
	EnchantData enchantData;

	public static TableEnchantEntry of(EnchantData data, int maxCost, int worldGate)
	{
		byte maxTableLevel = data.getMaxTableLevel();
		if (data.getAddLevelCost(0, 1) > maxCost) return null;
		if (data.getWorldLevelGate() > worldGate) return null;
		TableEnchantEntry ret = new TableEnchantEntry();
		ret.enchantData = data;
		ret.choosen = false;
		ret.currentLevel = 0;
		ret.maxLevel = maxTableLevel;
		ret.weight = data.getWeight();
		ret.remove = false;
		return ret;
	}

	public boolean isBoosted()
	{
		return enchantData.getMaxRandomLevel() <= maxLevel;
	}

	public void boost()
	{
		maxLevel = enchantData.getMaxRandomLevel();
	}

	public boolean canAddLevelIfBoosted(int cost)
	{
		if (enchantData.getMaxRandomLevel() <= currentLevel) return false;
		return enchantData.getAddLevelCost(currentLevel, currentLevel + 1) <= cost;
	}

	public boolean canAddLevel(int cost)
	{
		if (maxLevel <= currentLevel) return false;
		return enchantData.getAddLevelCost(currentLevel, currentLevel + 1) <= cost;
	}

	public int maxLevelWithCost(int leftCost)
	{
		return enchantData.maxLevelWithCost(currentLevel, maxLevel, enchantData.getLevelCost(currentLevel) + leftCost);
	}

	public int toLevel(byte level)
	{
		int cost = enchantData.getAddLevelCost(currentLevel, level);
		currentLevel = level;
		return cost;
	}
}
