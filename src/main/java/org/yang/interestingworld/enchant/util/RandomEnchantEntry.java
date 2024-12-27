package org.yang.interestingworld.enchant.util;

import org.yang.interestingworld.enchant.resource.EnchantData;

public class RandomEnchantEntry
{
	int weight;
	boolean chosen;
	boolean remove;
	byte currentLevel;
	int addPunish;
	float mulPunishForSelf;
	float mulPunishForOther;
	EnchantData enchantData;

	public static RandomEnchantEntry of(EnchantData data, int maxCost, int worldGate)
	{
		if (data.getLevelCost(1) > maxCost) return null;
		if (data.getWorldLevelGate() > worldGate) return null;
		RandomEnchantEntry ret = new RandomEnchantEntry();
		ret.enchantData = data;
		ret.chosen = false;
		ret.currentLevel = 0;
		ret.addPunish = 0;
		ret.mulPunishForOther = 1.0f;
		ret.mulPunishForSelf = 1.0f;
		ret.weight = data.getWeight();
		ret.remove = false;
		return ret;
	}
}
