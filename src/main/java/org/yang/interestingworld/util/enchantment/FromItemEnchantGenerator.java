package org.yang.interestingworld.util.enchantment;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import org.jetbrains.annotations.Nullable;
import org.yang.interestingworld.resource.enchant.RuneEnchantData;
import org.yang.interestingworld.util.CPPStyleIterator;
import org.yang.interestingworld.util.Rand;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import static org.yang.interestingworld.util.Base.iwlogger;

/***
 * 一个 Generator 仅能生成一次附魔
 */
public class FromItemEnchantGenerator
{
	public static final int PER_LAPIS_WORTH = 200;

	public static final float NO_DIAMOND_RATE = 0.9f;

	public static final float HAVE_DIAMOND_RATE = 0.99f;
	/**
	 * 附魔池全部开销
	 */
	int totalCostOfPool = 0;
	List<EnchantEntryWithLevelAndCost> enchantmentPool;
	int maxTryAmount = 0;
	int tryAmount = 0;
	int maxCost = 0;
	int maxWeight = 0;
	int leftCost = 0;
	Rand random;

	public FromItemEnchantGenerator(ItemEnchantmentsComponent component, ItemEnchantmentsComponent defaultComponent)
	{
		random = new Rand();
		enchantmentPool = new LinkedList<>();
		for (var enchantmentEntry : component.getEnchantments())
		{
			var entry = EnchantEntryWithLevelAndCost.fromItemEnchantmentsComponent(enchantmentEntry, component,
					defaultComponent);
			if (entry == null) continue;
			var data = entry.getData();
			totalCostOfPool += data.getLevelCost(entry.maxLevel);
			enchantmentPool.add(entry);
		}
	}

	public void setExtraData(boolean haveDiamond, int lapisCount, int crystalCount)
	{
		maxTryAmount = crystalCount + 1;
		int a = (int) (totalCostOfPool * (haveDiamond ? HAVE_DIAMOND_RATE : NO_DIAMOND_RATE));
		maxCost = Math.min(a, lapisCount * PER_LAPIS_WORTH);
	}

	public int getMaxCost()
	{
		return maxCost;
	}

	private CPPStyleIterator<EnchantEntryWithLevelAndCost> entryMatch(int idx)
	{
		CPPStyleIterator<EnchantEntryWithLevelAndCost> iterator = CPPStyleIterator.beginOf(enchantmentPool);
		while (iterator.notNull())
		{
			var e = iterator.getValue();
			if (idx <= e.getWeight()) return iterator;
			idx -= e.getWeight();
			iterator.toNext();
		}
		return iterator;
	}

	private CPPStyleIterator<EnchantEntryWithLevelAndCost> getRandomEntryByWeight()
	{
		int idx = random.nextEvenInt(1, maxWeight);
		return entryMatch(idx);
	}

	@Nullable
	public ItemEnchantmentsComponent generateOutput()
	{
		Iterator<EnchantEntryWithLevelAndCost> it = enchantmentPool.iterator();
		while (it.hasNext())
		{
			EnchantEntryWithLevelAndCost entry = it.next();
			if (!entry.canAddLevel(maxCost)) it.remove();
			else maxWeight += entry.getWeight();
		}
		leftCost = maxCost;
		if (maxWeight < 1) return null;
		LinkedHashMap<RuneEnchantData, EnchantEntryWithLevelAndCost> outputPool = new LinkedHashMap<>();
		while (maxWeight > 0)
		{
			CPPStyleIterator<EnchantEntryWithLevelAndCost> entryIterator = getRandomEntryByWeight();
			EnchantEntryWithLevelAndCost entry = entryIterator.getValue();
			iwlogger.info("handle entry " + entry.getData().getRegistryKey().getValue().toString());
			int canAddLevel = entry.maxCanAddLevel(leftCost);
			iwlogger.info("canAddLevel " + canAddLevel);
			if (canAddLevel < 1)
			{
				entryIterator.remove();
				maxWeight -= entry.getWeight();
				continue;
			}
			int addLevel = random.nextTriangleInt(1, canAddLevel);
			leftCost -= entry.addLevel(addLevel);
			iwlogger.info("add level " + addLevel);
			tryAmount++;
			outputPool.put(entry.getData(), entry);
			if (!entry.canAddLevel(leftCost))
			{
				entryIterator.remove();
				maxWeight -= entry.getWeight();
			}
			if (tryAmount >= maxTryAmount) break;
		}
		log();
		leftCost = 0;
		ConflictManager conflictManager = new ConflictManager();
		ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(
				ItemEnchantmentsComponent.DEFAULT);
		for (var entry : outputPool.entrySet())
		{
			var data = entry.getKey();
			var et = entry.getValue();
			for (var conf : data.getConflictGroups())
			{
				conflictManager.applyConflictToCost(conf, data, outputPool);
			}
			leftCost += et.getPunishedCost();
			builder.add(data.getRegistryEntry(), et.currentLevel());
		}
		return builder.build();
	}

	public int getRuneLevelByCost()
	{
		return RuneEnchantment.getRuneLevelOfCost(leftCost);
	}

	public boolean needDiamond()
	{
		return maxCost > (int) (totalCostOfPool * NO_DIAMOND_RATE);
	}

	public int getLapisNeedCount()
	{
		return maxCost / PER_LAPIS_WORTH;
	}

	public int getCrystalNeed()
	{
		return Math.max(tryAmount - 1, 0);
	}

	public void log()
	{
		iwlogger.warn("Begin " + this);
		iwlogger.info("totalCostOfPool " + totalCostOfPool);
		iwlogger.info("maxCost " + maxCost);
		iwlogger.info("leftCost " + leftCost);
		iwlogger.info("maxTryAmount " + maxTryAmount);
		iwlogger.info("tryAmount " + tryAmount);
		iwlogger.info("maxWeight " + maxWeight);
		iwlogger.info("enchantmentPool: ");
		for (var i : enchantmentPool)
		{
			iwlogger.info(i);
		}
		iwlogger.warn("End " + this);
	}
}
