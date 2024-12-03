package org.yang.interestingworld.resource.enchant;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnchantConflictData
{
	public static Set<EnchantConflictData> Data;
	Set<RuneEnchantData> enchantSet;
	boolean allowConflict;
	int weightPunish;
	int costAddPunish;
	float costMultiplierPunish;

	static class IO
	{
		List<String> enchantments;
		boolean allowConflict;
		int weightPunish;

		// C_T = (M1 + M2 + M3 + 1)C_S + A1 + A2 + A3
		int costAddPunish;
		float costMultiplierPunish;

		void validate()
		{
			if (!allowConflict)
			{
				if (costMultiplierPunish < 0.0f) costMultiplierPunish = 0.0f;
				if (costAddPunish < 0) costAddPunish = 0;
			}
		}
	}

	EnchantConflictData(IO io, Set<RuneEnchantData> data)
	{
		allowConflict = io.allowConflict;
		if (io.allowConflict)
		{
			weightPunish = io.weightPunish;
			costAddPunish = io.costAddPunish;
			costMultiplierPunish = io.costMultiplierPunish;
		}
		else
		{
			weightPunish = 0;
			costAddPunish = 0;
			costMultiplierPunish = 0;
		}
		enchantSet = data;
		for (var i : data)
		{
			i.addConfictGroup(this);
		}
	}

	public int getWeightPunish()
	{
		return weightPunish;
	}

	public int getCostAddPunish()
	{
		return costAddPunish;
	}

	public float getCostMultiplierPunish()
	{
		return costMultiplierPunish;
	}

	public boolean getAllowConflict()
	{
		return allowConflict;
	}

	public Set<RuneEnchantData> getEnchantSet()
	{
		return Collections.unmodifiableSet(enchantSet);
	}

	@Nullable
	public Set<RuneEnchantData> copyEnchantSet()
	{
		return new HashSet<>(enchantSet);
	}

}
