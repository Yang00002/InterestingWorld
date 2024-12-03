package org.yang.interestingworld.util.enchantment;

public interface canModifyCostByConflict
{
	void addPunishment(int add, float mul);

	int getPunishedCost();
}
