package org.yang.iw.boost.pool;

import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.yang.iw.IWEnchantments;
import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.component.BoostComponent;

import java.util.Random;

import static org.yang.iw.util.IWCostUtil.getMaxAllowXpCostOfWorldLevel;
import static org.yang.iw.util.IWCostUtil.getWorldLevelOfXpCost;

public class TableBoostGenerator
{
	private static final boolean DEBUG = true;
	boolean unlimited = false;
	TableBoostEntry root = new TableBoostEntry();
	TableBoostEntry complete = new TableBoostEntry();
	final Random random;
	int maxWeight = 0;
	int maxCost = 0;
	int leftCost = 0;
	final int costUnit;
	final int maxBoostTime;
	int boostTime = 0;
	int freeBoostCount = 0;
	int maxBoostCount;
	final int costPerLazurite;
	final int maxWorldLevel;
	final BoostComponent boostComponent;

	public int triangleInt(int min, int max)
	{
		if (min == max) return max;
		if (min == max - 1) return min + random.nextInt(2);
		int a = (random.nextInt(min, max + 1) + random.nextInt(min, max + 1));
		if ((a & 1) == 1) return (a >> 1) + random.nextInt(2);
		return a >> 1;
	}

	public TableBoostGenerator(TableBoostPool pool, ItemEnchantmentsComponent enchants, int boostTime,
							   int lazuriteCount, int worldLevel, int materialLevel, int seed)
	{
		maxBoostTime = boostTime;
		this.random = new Random(
				(long) (seed + Math.min(lazuriteCount, Math.max(materialLevel, 3))) * (1 + materialLevel));
		TableBoostPool.Page page = pool.randomPage(worldLevel, random);
		if (page != null)
		{
			MutableBoolean balance = new MutableBoolean(false);
			MutableInt luckyLevel = new MutableInt(0);
			MutableInt efficientLevel = new MutableInt(0);
			enchants.getEnchantmentEntries().forEach(e -> {
				if (e.getIntValue() > 0)
				{
					var entry = e.getKey();
					if (entry.matchesKey(IWEnchantments.BALANCE)) // 降低每次提供份额, 增加魔咒数
					{
						balance.setValue(true);
					}
					else if (entry.matchesKey(IWEnchantments.RUNE_BOOST)) // 第一个魔咒突破限制
					{
						unlimited = true;
					}
					else if (entry.matchesKey(IWEnchantments.LUCKY)) // 降低强化次数损耗
					{
						luckyLevel.setValue(e.getIntValue());
					}
					else if (entry.matchesKey(IWEnchantments.ENERGY_EFFICIENCY)) // 增加最大开销
					{
						efficientLevel.setValue(e.getIntValue());
					}
				}
			});
			costPerLazurite = (int) ((efficientLevel.getValue() * 0.5f + 1) * 160);
			maxWorldLevel = Math.min(worldLevel, Math.min(materialLevel, maxBoostTime));
			maxCost = Math.min(getMaxAllowXpCostOfWorldLevel(maxWorldLevel), costPerLazurite * lazuriteCount);
			leftCost = maxCost;
			int entryCount = 0;
			for (TableBoostPool.Entry entry : page.entries)
			{
				if (entry.boost.xpCostOfLevel((short) 1) <= maxCost)
				{
					add(entry.boost, entry.weight);
					entryCount++;
				}
			}
			if (entryCount == 0)
			{
				maxBoostCount = 0;
				costUnit = 0;
				boostComponent = BoostComponent.DEFAULT;
				root.next = null;
				root.pre = null;
				root = null;
				complete.next = null;
				complete.pre = null;
				complete = null;
				return;
			}
			freeBoostCount = 1 + luckyLevel.getValue();
			if (balance.getValue())
			{
				if (entryCount > 1)
				{
					int c1 = 1 + Math.max(random.nextInt(entryCount), random.nextInt(entryCount));
					int c2 = 1 + Math.max(random.nextInt(entryCount), random.nextInt(entryCount));
					maxBoostCount = Math.min(c1, c2);
				}
				else maxBoostCount = 1;
				costUnit = maxCost / (maxBoostCount << 1);
			}
			else
			{
				maxBoostCount =
						entryCount > 1 ? 1 + Math.min(random.nextInt(entryCount), random.nextInt(entryCount)) : 1;
				int div = maxBoostCount << 1;
				costUnit = (maxCost + div - 1) / div;
			}
			boostComponent = generateBoosts();
		}
		else
		{
			costPerLazurite = 0;
			maxWorldLevel = 0;
			costUnit = 0;
			boostComponent = BoostComponent.DEFAULT;
		}
		root.next = null;
		root.pre = null;
		root = null;
		complete.next = null;
		complete.pre = null;
		complete = null;
	}

	private void add(AbstractBoost boost, int weight)
	{
		maxWeight += weight;
		TableBoostEntry it = root;
		int cost = boost.xpCostOfLevel((short) 1);
		while (it.next != null && it.next.nextLevelCost > cost)
		{
			it = it.next;
		}
		if (it.next == null) it.next = new TableBoostEntry(cost, weight, boost, it, null);
		else
		{
			TableBoostEntry next = it.next;
			TableBoostEntry current = new TableBoostEntry(cost, weight, boost, it, next);
			it.next = current;
			next.pre = current;
		}
	}

	private int getLeft(int nextLevelCost)
	{
		if (leftCost <= costUnit) return leftCost;
		if (nextLevelCost >= costUnit * 2) return nextLevelCost;
		int maxGate = Math.min(leftCost, costUnit * 2);
		int minGate = Math.max(nextLevelCost, costUnit);
		return Math.min(random.nextInt(minGate, maxGate + 1), random.nextInt(minGate, maxGate + 1));
	}

	private TableBoostEntry selectBoost()
	{
		if (maxWeight == 0) return null;
		int weight = random.nextInt(maxWeight);
		TableBoostEntry p = root.next;
		while (p != null)
		{
			if (p.weight > weight) return p;
			weight -= p.weight;
			p = p.next;
		}
		return null;
	}

	private void sort(TableBoostEntry entry)
	{
		entry.pre.next = entry.next;
		if (entry.next != null) entry.next.pre = entry.pre;
		TableBoostEntry p = root.next;
		TableBoostEntry m;
		while (p != null && p.nextLevelCost > leftCost)
		{
			m = p;
			p = p.next;
			maxWeight -= m.weight;
			if (m.currentLevel > 0)
			{
				m.pre = null;
				m.next = complete.next;
				complete.next = m;
			}
			else
			{
				m.pre = null;
				m.next = null;
			}
		}
		root.next = p;
		if (p != null) p.pre = root;
		if (entry.reachMax() || (entry.currentLevel > 0 && entry.nextLevelCost > leftCost))
		{
			entry.pre = null;
			entry.next = complete.next;
			complete.next = entry;
			maxWeight -= entry.weight;
		}
		else if (entry.currentLevel > 0)
		{
			p = root;
			while (p.next != null && p.next.nextLevelCost > entry.nextLevelCost)
			{
				p = p.next;
			}
			if (p.next == null)
			{
				p.next = entry;
				entry.next = null;
				entry.pre = p;
			}
			else
			{
				entry.next = p.next;
				entry.pre = p;
				p.next = entry;
				entry.next.pre = entry;
			}
		}
		else
		{
			maxWeight -= entry.weight;
			entry.pre = null;
			entry.next = null;
		}
	}

	private void throwNew()
	{
		TableBoostEntry p = root.next;
		TableBoostEntry m;
		while (p != null)
		{
			m = p;
			p = p.next;
			if (m.currentLevel == 0)
			{
				m.pre.next = m.next;
				if (p != null) m.next.pre = m.pre;
				m.pre = null;
				m.next = null;
				maxWeight -= m.weight;
			}
		}
	}

	private void throwNewNoSmallerThan(TableBoostEntry entry)
	{
		TableBoostEntry p = entry.pre;
		TableBoostEntry m;
		while (p != root)
		{
			m = p;
			p = p.pre;
			if (m.currentLevel == 0)
			{
				m.pre.next = m.next;
				m.next.pre = m.pre;
				m.pre = null;
				m.next = null;
				maxWeight -= m.weight;
			}
		}
		if (entry.next != null) entry.next.pre = root;
		entry.pre.next = entry.next;
		entry.next = null;
		entry.pre = null;
		maxWeight -= entry.weight;
	}

	private void tryHaveNewBoost(TableBoostEntry entry)
	{
		if (maxBoostCount > 0)
		{
			if (freeBoostCount > 0)
			{
				freeBoostCount--;
				maxBoostCount--;
				int left = getLeft(entry.nextLevelCost);
				if (unlimited && entry.setUnlimit()) unlimited = false;
				leftCost -= entry.setLevel(entry.costAchieveLevel(left));
				sort(entry);
			}
			else if (boostTime <
					 maxBoostTime - getWorldLevelOfXpCost(maxCost - leftCost + entry.nextLevelCost)) // 用掉的等级
			{
				boostTime++;
				maxBoostCount--;
				int nextMaxCost = Math.min(maxCost, getMaxAllowXpCostOfWorldLevel(maxBoostTime - boostTime));
				leftCost = leftCost - maxCost + nextMaxCost;
				maxCost = nextMaxCost;
				int left = getLeft(entry.nextLevelCost);
				if (unlimited && entry.setUnlimit()) unlimited = false;
				leftCost -= entry.setLevel(entry.costAchieveLevel(left));
				sort(entry);
			}
			else
			{
				throwNewNoSmallerThan(entry);
			}
		}
		else
		{
			throwNew();
		}
	}

	private void advance(TableBoostEntry entry)
	{
		int left = getLeft(entry.nextLevelCost);
		if (unlimited && entry.setUnlimit()) unlimited = false;
		leftCost -= entry.setLevel(entry.costAchieveLevel(left));
		sort(entry);
	}

	private BoostComponent generateBoosts()
	{
		TableBoostEntry entry = selectBoost();
		while (entry != null)
		{
			if (entry.currentLevel == 0)
			{
				tryHaveNewBoost(entry);
			}
			else
			{
				advance(entry);
			}
			entry = selectBoost();
		}
		if (complete.next == null) return BoostComponent.DEFAULT;
		entry = complete.next;
		Object2ShortOpenHashMap<AbstractBoost> map = new Object2ShortOpenHashMap<>();
		while (entry != null)
		{
			if (entry.currentLevel > 0) map.put(entry.boost, entry.currentLevel);
			entry = entry.next;
		}
		return BoostComponent.createLegal(map, true);
	}

	public BoostComponent getBoostComponent()
	{
		return boostComponent;
	}

	public int getBoostTimeConsume()
	{
		return boostTime + boostComponent.level();
	}

	public int getCost()
	{
		return maxCost - leftCost;
	}

	public int getLazuriteCost()
	{
		return (getCost() + costPerLazurite - 1) / costPerLazurite;
	}

	public boolean isEmpty()
	{
		return complete == null;
	}
}
