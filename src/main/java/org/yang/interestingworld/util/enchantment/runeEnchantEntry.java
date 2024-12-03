package org.yang.interestingworld.util.enchantment;

import org.yang.interestingworld.resource.enchant.RuneEnchantData;

public interface runeEnchantEntry
{
	RuneEnchantData getData();

	int maxLevel();

	boolean canAddLevel(int cost);

	int getWeight();

	int maxCanAddLevel(int cost);

	int addLevel(int level);

	int getCost();

	int currentLevel();
}
