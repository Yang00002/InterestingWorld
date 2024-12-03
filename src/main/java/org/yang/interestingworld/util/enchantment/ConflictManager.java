package org.yang.interestingworld.util.enchantment;

import org.yang.interestingworld.resource.enchant.EnchantConflictData;
import org.yang.interestingworld.resource.enchant.RuneEnchantData;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConflictManager
{
	Set<EnchantConflictData> appliedConflictSet = new HashSet<>();

	public void applyConflictToCost(EnchantConflictData conflictData, RuneEnchantData from, Map<RuneEnchantData, ?
			extends canModifyCostByConflict> toMap)
	{
		if (appliedConflictSet.contains(conflictData)) return;
		for (var i : conflictData.getEnchantSet())
		{
			if (i != from)
			{
				var to = toMap.getOrDefault(i, null);
				if (to != null)
				{
					to.addPunishment(conflictData.getCostAddPunish(), conflictData.getCostMultiplierPunish());
				}
			}
		}
	}
}
