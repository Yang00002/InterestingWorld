package org.yang.interestingworld.enchant;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;
import org.yang.interestingworld.IWItems;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.yang.interestingworld.util.RuneEnchantment.*;

public class RandomEnchantGenerator
{
	public static ItemStack generate(int seed, int level)
	{
		//if (level >= 10) return generateMax(seed);
		Random random = new Random(seed); //rd
		//获得附魔组
		int enchantGroupWeight = random.nextInt(1, RandomEnchantGroup.maxWeight + 1);
		RandomEnchantGroup enchantGroup = null;
		for (var g : RandomEnchantGroup.enchantGroups)
		{
			if (g.weight >= enchantGroupWeight)
			{
				enchantGroup = g;
				break;
			}
			enchantGroupWeight -= g.weight;
		}
		if (enchantGroup == null) return IWItems.EMPTY_RUNE.getDefaultStack();
		int enchantGroupSize = enchantGroup.enchants.size();
		if (enchantGroupSize < 1) return IWItems.EMPTY_RUNE.getDefaultStack();
		//附魔池
		List<RandomEnchantEntry> enchantmentPool = new LinkedList<>();
		//获得最大附魔数
		int maxEnchantCount = Math.min((level >> 1) + 1, enchantGroupSize);
		int dif = random.nextInt(-1, 1) + random.nextInt(0, 2);
		maxEnchantCount += dif;
		if (maxEnchantCount < 1) maxEnchantCount = 1;
		//获得最大消耗
		int maxCost = getRandomXpCostOfWorldLevel(level, random);
		int maxWeight = 0;
		for (var i : enchantGroup.getEnchants())
		{
			var g = RandomEnchantEntry.of(i, maxCost, level);
			if (g != null)
			{
				enchantmentPool.add(g);
				maxWeight += g.weight;
			}
		}
		if (maxWeight < 1) return IWItems.EMPTY_RUNE.getDefaultStack();
		int leftCost = maxCost;
		boolean firstUp = true;
		ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(
				ItemEnchantmentsComponent.DEFAULT);
		do
		{
			RandomEnchantEntry entry = null;
			{
				int entry_w = random.nextInt(1, maxWeight + 1);
				var it = enchantmentPool.iterator();
				while (it.hasNext())
				{
					var e = it.next();
					if (e.remove) it.remove();
					else
					{
						if (e.weight >= entry_w)
						{
							entry = e;
							break;
						}
						entry_w -= e.weight;
					}
				}
				if (entry == null) break;
			}
			var enchantDate = entry.enchantData;
			int maxLevel;
			if (entry.currentLevel == 0)
			{
				int punishedCost;
				if ((leftCost <= entry.addPunish) || (enchantDate.cost[1] >
													  (punishedCost = (int) (entry.mulPunishForOther *
																			 (leftCost - entry.addPunish)))))
				{
					entry.remove = true;
					maxWeight -= entry.weight;
					continue;
				}
				maxLevel = enchantDate.maxLevelWithCost((byte) 1, enchantDate.maxRandomLevel, punishedCost);
			}
			else
			{
				if (entry.currentLevel >= enchantDate.maxRandomLevel)
				{
					builder.set(entry.enchantData.registryEntry, entry.currentLevel);
					entry.remove = true;
					maxWeight -= entry.weight;
					continue;
				}
				maxLevel = enchantDate.maxLevelWithCost(entry.currentLevel, enchantDate.maxRandomLevel,
						(int) (entry.mulPunishForOther * leftCost));
				if (maxLevel <= entry.currentLevel)
				{
					builder.set(entry.enchantData.getRegistryEntry(), entry.currentLevel);
					entry.remove = true;
					maxWeight -= entry.weight;
					continue;
				}
			}
			if (!entry.chosen)
			{
				if (maxEnchantCount > 0)
				{
					entry.chosen = true;
					maxEnchantCount--;
					var conf = enchantDate.conflicts;
					for (RandomEnchantEntry n : enchantmentPool)
					{
						if (!n.remove && !n.chosen)
						{
							var nEntry = n.enchantData.registryEntry;
							if (conf.containsKey(nEntry))
							{
								var d = conf.get(nEntry);
								if (d == ConflictGroup.fatalGroup)
								{
									n.remove = true;
									maxWeight -= n.weight;
								}
								else
								{
									n.addPunish += d.addPunish;
									n.mulPunishForOther /= d.mulPunish;
									n.mulPunishForSelf *= d.mulPunish;
									int punishDecrease = Math.min(d.weightPunish, n.weight - 1);
									n.weight -= punishDecrease;
									maxWeight -= punishDecrease;
								}
							}
						}
					}
				}
				else
				{
					entry.remove = true;
					maxWeight -= entry.weight;
					continue;
				}
			}
			byte getLevel = (byte) (
					((random.nextInt(entry.currentLevel, maxLevel) + random.nextInt(entry.currentLevel, maxLevel)) >>
					 1) + 1);
			int cost = entry.currentLevel < 1 ? (entry.addPunish +
												 (int) (enchantDate.cost[getLevel] * entry.mulPunishForSelf)) : (int) (
					(enchantDate.cost[getLevel] - enchantDate.cost[entry.currentLevel]) * entry.mulPunishForSelf);
			leftCost -= cost;
			entry.currentLevel = getLevel;
			if (entry.currentLevel >= enchantDate.maxRandomLevel)
			{
				builder.set(entry.enchantData.registryEntry, entry.currentLevel);
				entry.remove = true;
				maxWeight -= entry.weight;
				continue;
			}
			if ((enchantDate.cost[entry.currentLevel + 1] - enchantDate.cost[entry.currentLevel]) >
				(int) (entry.mulPunishForOther * leftCost))
			{
				builder.set(entry.enchantData.getRegistryEntry(), entry.currentLevel);
				entry.remove = true;
				maxWeight -= entry.weight;
				continue;
			}
			if (firstUp)
			{
				int weightUp = Math.abs(
						random.nextInt(-maxWeight, 1) + random.nextInt(-(maxWeight >> 1), maxWeight + 1));
				if (weightUp > 0)
				{
					entry.weight += weightUp;
					maxWeight += weightUp;
				}
				firstUp = false;
			}
		} while (maxWeight > 0);
		for (var i : enchantmentPool)
			if (i.currentLevel > 0) builder.set(i.enchantData.registryEntry, i.currentLevel);
		var ec = builder.build();
		if (ec.getSize() < 1) return IWItems.EMPTY_RUNE.getDefaultStack();
		return getEnchantRuneItemStack(builder.build(), getWorldLevelOfXpCost(maxCost - leftCost));
	}

	private static ItemStack generateMax(int seed)
	{
		Random random = new Random(seed); //rd
		//获得附魔组
		int enchantGroupWeight = random.nextInt(1, RandomEnchantGroup.maxWeight + 1);
		RandomEnchantGroup enchantGroup = null;
		for (var g : RandomEnchantGroup.enchantGroups)
		{
			if (g.weight >= enchantGroupWeight)
			{
				enchantGroup = g;
				break;
			}
			enchantGroupWeight -= g.weight;
		}
		if (enchantGroup == null) return IWItems.EMPTY_RUNE.getDefaultStack();
		int enchantGroupSize = enchantGroup.enchants.size();
		if (enchantGroupSize < 1) return IWItems.EMPTY_RUNE.getDefaultStack();
		//附魔池
		List<RandomEnchantEntry> enchantmentPool = new LinkedList<>();
		//获得最大附魔数
		int maxEnchantCount = Math.min(6, enchantGroupSize);
		int dif = random.nextInt(-1, 1) + random.nextInt(0, 2);
		maxEnchantCount += dif;
		if (maxEnchantCount < 1) maxEnchantCount = 1;
		int maxCost = getRandomXpCostOfWorldLevel(10, random);
		int leftCostGate = maxCost - getXpCostOfWorldLevel(9) - 1;
		int giveUpChances = random.nextInt(6) + random.nextInt(6);
		//获得最大消耗
		int maxWeight = 0;
		for (var i : enchantGroup.getEnchants())
		{
			var g = RandomEnchantEntry.of(i, maxCost, 10);
			if (g != null)
			{
				enchantmentPool.add(g);
				maxWeight += g.weight;
			}
		}
		if (maxWeight < 1) return IWItems.EMPTY_RUNE.getDefaultStack();
		int leftCost = maxCost;
		boolean firstUp = true;
		ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(
				ItemEnchantmentsComponent.DEFAULT);
		do
		{
			RandomEnchantEntry entry = null;
			{
				int entry_w = random.nextInt(1, maxWeight + 1);
				var it = enchantmentPool.iterator();
				while (it.hasNext())
				{
					var e = it.next();
					if (e.remove) it.remove();
					else
					{
						if (e.weight >= entry_w)
						{
							entry = e;
							break;
						}
						entry_w -= e.weight;
					}
				}
				if (entry == null) break;
			}
			var enchantDate = entry.enchantData;
			int maxLevel;
			if (entry.currentLevel == 0)
			{
				int punishedCost;
				if ((leftCost <= entry.addPunish) || (enchantDate.cost[1] >
													  (punishedCost = (int) (entry.mulPunishForOther *
																			 (leftCost - entry.addPunish)))))
				{
					entry.remove = true;
					maxWeight -= entry.weight;
					continue;
				}
				maxLevel = enchantDate.maxLevelWithCost((byte) 1, enchantDate.maxRandomLevel, punishedCost);
			}
			else
			{
				if (entry.currentLevel >= enchantDate.maxRandomLevel)
				{
					builder.set(entry.enchantData.registryEntry, entry.currentLevel);
					entry.remove = true;
					maxWeight -= entry.weight;
					continue;
				}
				maxLevel = enchantDate.maxLevelWithCost(entry.currentLevel, enchantDate.maxRandomLevel,
						(int) (entry.mulPunishForOther * leftCost));
				if (maxLevel <= entry.currentLevel)
				{
					builder.set(entry.enchantData.getRegistryEntry(), entry.currentLevel);
					entry.remove = true;
					maxWeight -= entry.weight;
					continue;
				}
			}
			if (!entry.chosen)
			{
				if (maxEnchantCount > 0)
				{
					entry.chosen = true;
					maxEnchantCount--;
					var conf = enchantDate.conflicts;
					for (RandomEnchantEntry n : enchantmentPool)
					{
						if (!n.remove && !n.chosen)
						{
							var nEntry = n.enchantData.registryEntry;
							if (conf.containsKey(nEntry))
							{
								var d = conf.get(nEntry);
								if (d == ConflictGroup.fatalGroup)
								{
									n.remove = true;
									maxWeight -= n.weight;
								}
								else
								{
									n.addPunish += d.addPunish;
									n.mulPunishForOther /= d.mulPunish;
									n.mulPunishForSelf *= d.mulPunish;
									int punishDecrease = Math.min(d.weightPunish, n.weight - 1);
									n.weight -= punishDecrease;
									maxWeight -= punishDecrease;
								}
							}
						}
					}
				}
				else
				{
					entry.remove = true;
					maxWeight -= entry.weight;
					continue;
				}
			}
			byte getLevel = (byte) (
					((random.nextInt(entry.currentLevel, maxLevel) + random.nextInt(entry.currentLevel, maxLevel)) >>
					 1) + 1);
			int cost = entry.currentLevel < 1 ? (entry.addPunish +
												 (int) (enchantDate.cost[getLevel] * entry.mulPunishForSelf)) : (int) (
					(enchantDate.cost[getLevel] - enchantDate.cost[entry.currentLevel]) * entry.mulPunishForSelf);
			leftCost -= cost;
			entry.currentLevel = getLevel;
			if (leftCost <= leftCostGate && random.nextInt(10) >= giveUpChances) break;
			if (entry.currentLevel >= enchantDate.maxRandomLevel)
			{
				builder.set(entry.enchantData.registryEntry, entry.currentLevel);
				entry.remove = true;
				maxWeight -= entry.weight;
				continue;
			}
			if ((enchantDate.cost[entry.currentLevel + 1] - enchantDate.cost[entry.currentLevel]) >
				(int) (entry.mulPunishForOther * leftCost))
			{
				builder.set(entry.enchantData.getRegistryEntry(), entry.currentLevel);
				entry.remove = true;
				maxWeight -= entry.weight;
				continue;
			}
			if (firstUp)
			{
				int weightUp = Math.abs(
						random.nextInt(-maxWeight, 1) + random.nextInt(-(maxWeight >> 1), maxWeight + 1));
				if (weightUp > 0)
				{
					entry.weight += weightUp;
					maxWeight += weightUp;
				}
				firstUp = false;
			}
		} while (maxWeight > 0);
		for (var i : enchantmentPool)
			if (i.currentLevel > 0) builder.set(i.enchantData.registryEntry, i.currentLevel);
		var ec = builder.build();
		if (ec.getSize() < 1) return IWItems.EMPTY_RUNE.getDefaultStack();
		return getEnchantRuneItemStack(builder.build(), getWorldLevelOfXpCost(maxCost - leftCost));
	}
}
