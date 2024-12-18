package org.yang.interestingworld.enchant;

public class RandomEnchantEntry
{
	int weight;
	boolean choosen;
	boolean remove;
	byte currentLevel;
	int addPunish;
	float mulPunishForSelf;
	float mulPunishForOther;
	EnchantData enchantData;

	public static RandomEnchantEntry of(EnchantData data, int maxCost, int worldGate)
	{
		if (data.cost[1] > maxCost) return null;
		if (data.worldLevelGate > worldGate) return null;
		RandomEnchantEntry ret = new RandomEnchantEntry();
		ret.enchantData = data;
		ret.choosen = false;
		ret.currentLevel = 0;
		ret.addPunish = 0;
		ret.mulPunishForOther = 1.0f;
		ret.mulPunishForSelf = 1.0f;
		ret.weight = data.weight;
		ret.remove = false;
		return ret;
	}
}
