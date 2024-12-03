package org.yang.interestingworld.resource.enchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.yang.interestingworld.util.Base.iwlogger;

/***
 * 此类资源无法被 reload 加载
 */
public class RuneEnchantData
{
	static Map<RegistryKey<Enchantment>, RuneEnchantData> Data = null;
	Set<EnchantConflictData> conflictGroups;
	int levelCostParameterA;
	int levelCostParameterB;
	// C > maxAllowLevel 为反比例
	// 线性 AX+B
	// 反比例 A/(C-X) + B
	int levelCostParameterC;

	int weight;

	static class IO
	{
		int maxAchieveLevel = 1;
		int maxRandomLevel = 1;
		int maxAllowLevel = 1;
		int levelCostParameterA = 1;
		int levelCostParameterB = 1;
		// C > maxAllowLevel 为反比例
		// 线性 AX+B
		// 反比例 A/(C-X) + B
		int levelCostParameterC = 0;
		int weight = 1;

		void validate()
		{
			if (maxAllowLevel < 1) maxAllowLevel = 1;
			if (maxRandomLevel > maxAllowLevel) maxRandomLevel = maxAllowLevel;
			if (maxAchieveLevel > maxRandomLevel) maxAchieveLevel = maxRandomLevel;
			if (weight < 1) weight = 1;
			if (levelCostParameterA <= 0) levelCostParameterA = 1;
			if (levelCostParameterC > maxAllowLevel)
			{
				if (levelCostParameterA / (levelCostParameterC - 1) + levelCostParameterB <= 0) levelCostParameterB
						= 0;
			}
			else
			{
				if (levelCostParameterA + levelCostParameterB <= 0) levelCostParameterB = 0;
			}
		}

		int getLevelCost(int level)
		{
			if (maxAllowLevel < levelCostParameterC && level <= maxAllowLevel)
				return levelCostParameterA / (levelCostParameterC - level) + levelCostParameterB;
			return levelCostParameterA * level + levelCostParameterB;
		}
	}

	int maxAchieveLevel;
	int maxRandomLevel;
	int maxAllowLevel;
	/**
	 * 总开销
	 */
	int[] levelcost;
	RegistryKey<Enchantment> tiedEnchantment;
	RegistryEntry<Enchantment> tiedEntry;

	RuneEnchantData(IO io, RegistryKey<Enchantment> ec, RegistryEntry<Enchantment> entry)
	{
		conflictGroups = new HashSet<>();
		levelcost = new int[io.maxAllowLevel + 1];
		maxAchieveLevel = io.maxAchieveLevel;
		maxRandomLevel = io.maxRandomLevel;
		maxAllowLevel = io.maxAllowLevel;
		for (int i = 1; i <= maxAllowLevel; i++)
		{
			levelcost[i] = io.getLevelCost(i);
		}
		levelcost[0] = 0;
		tiedEnchantment = ec;
		tiedEntry = entry;
		levelCostParameterA = io.levelCostParameterA;
		levelCostParameterB = io.levelCostParameterB;
		levelCostParameterC = io.levelCostParameterC;
		weight = io.weight;
	}

	void addConfictGroup(EnchantConflictData conflictData)
	{
		conflictGroups.add(conflictData);
	}

	public RegistryKey<Enchantment> getRegistryKey()
	{
		return tiedEnchantment;
	}

	public RegistryEntry<Enchantment> getRegistryEntry()
	{
		return tiedEntry;
	}

	public int getMaxAllowLevel()
	{
		return maxAllowLevel;
	}

	public int getMaxAchieveLevel()
	{
		return maxAchieveLevel;
	}

	public Set<EnchantConflictData> getConflictGroups()
	{
		return Collections.unmodifiableSet(conflictGroups);
	}

	public int getMaxRandomLevel()
	{
		return maxRandomLevel;
	}

	public int getLevelCost(int level)
	{
		return levelcost[level];
	}

	public static void clearData()
	{
		Data = null;
	}

	@Nullable
	public static RuneEnchantData fromRegistryKey(RegistryKey<Enchantment> key)
	{
		return Data.getOrDefault(key, null);
	}

	@Nullable
	public static RuneEnchantData fromRegistryEntry(RegistryEntry<Enchantment> entry)
	{
		var k = entry.getKey();
		return k.map(RuneEnchantData::fromRegistryKey).orElse(null);
	}

	public int costAchieveLevel(int cost)
	{
		int f = cost - levelCostParameterB;
		if (f <= 0) return 0;
		if (levelCostParameterC > maxAllowLevel)
		{
			int l = 0;
			int r = maxAllowLevel;
			int k = (l + r) / 2;
			while (l + 1 > r)
			{
				if (levelcost[k] < cost)
				{
					l = k;
					k = (l + r) / 2;
				}
				else if (levelcost[k] > cost)
				{
					r = k;
					k = (l + r) / 2;
				}
				else return k;
			}
			if (levelcost[r] <= cost) return cost;
			else return l;
		}
		else return f / levelCostParameterA;
	}

	public int costAchieveLevel(int cost, int promiseAchieve, int promiseMax)
	{
		if (levelCostParameterC > maxAllowLevel)
		{
			int l = promiseAchieve;
			int r = promiseMax;
			int k = (l + r) / 2;
			iwlogger.info("have " + cost);
			while (l + 1 < r)
			{
				iwlogger.info("cost " + k + " is " + levelcost[k]);
				if (levelcost[k] < cost)
				{
					l = k;
					k = (l + r) / 2;
				}
				else if (levelcost[k] > cost)
				{
					r = k;
					k = (l + r) / 2;
				}
				else return k;
			}
			if (levelcost[r] <= cost) return r;
			else return l;
		}
		else return Math.clamp((cost - levelCostParameterB) / levelCostParameterA, 0, maxAllowLevel);
	}

	public int getWeight()
	{
		return weight;
	}

	public void log()
	{
		iwlogger.warn("Begin " + this);
		iwlogger.info("maxAllowLevel " + maxAllowLevel);
		iwlogger.info("maxRandomLevel " + maxRandomLevel);
		iwlogger.info("maxAchieveLevel " + maxAchieveLevel);
		iwlogger.info("levelCostParameterA " + levelCostParameterA);
		iwlogger.info("levelCostParameterB " + levelCostParameterB);
		iwlogger.info("levelCostParameterC " + levelCostParameterC);
		iwlogger.info("weight " + weight);
		iwlogger.warn("End " + this);
	}
}
