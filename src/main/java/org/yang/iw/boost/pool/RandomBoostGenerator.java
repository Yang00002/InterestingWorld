package org.yang.iw.boost.pool;

import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.component.BoostComponent;

import java.util.Random;

import static org.yang.iw.util.IWCostUtil.*;

public class RandomBoostGenerator
{
	RandomBoostEntry root = new RandomBoostEntry();
	RandomBoostEntry complete = new RandomBoostEntry();
	final Random random;
	int maxWeight = 0;
	int maxCost = 0;
	int leftCost = 0;
	final int costUnit;
	final int maxBoostTime;
	int boostTime = -1;
	int maxBoostCount;
	final int maxWorldLevel;
	final BoostComponent boostComponent;

	public RandomBoostGenerator(RandomBoostPool pool, int boostTime, int worldLevel, int materialLevel, int seed)
	{
		maxBoostTime = boostTime;
		this.random = new Random(seed);
		RandomBoostPool.Page page = pool.randomPage(worldLevel, random);
		if (page != null)
		{
			maxWorldLevel = Math.min(worldLevel, Math.min(materialLevel, maxBoostTime));
			maxCost = maxWorldLevel < worldLevel ? getMaxAllowXpCostOfWorldLevel(maxWorldLevel)
												 : getRandomXpCostOfWorldLevel(worldLevel, random);
			leftCost = maxCost;
			int entryCount = 0;
			for (RandomBoostPool.Entry entry : page.entries)
			{
				if (entry.maxAppearWorldLevel >= worldLevel && entry.minAppearWorldLevel <= worldLevel &&
					entry.boost.xpCostOfLevel((short) 1) <= maxCost)
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
			if (entryCount > 1)
			{
				int c1 = 1 + Math.max(random.nextInt(entryCount), random.nextInt(entryCount));
				int c2 = 1 + random.nextInt(entryCount);
				maxBoostCount = Math.min(c1, c2);
			}
			else maxBoostCount = 1;
			costUnit = (maxCost + maxBoostCount) / (maxBoostCount << 1);
			boostComponent = generateBoosts();
		}
		else
		{
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
		RandomBoostEntry it = root;
		int cost = boost.xpCostOfLevel((short) 1);
		while (it.next != null && it.next.nextLevelCost_B > cost)
		{
			it = it.next;
		}
		if (it.next == null) it.next = new RandomBoostEntry(cost, weight, boost, it, null);
		else
		{
			RandomBoostEntry next = it.next;
			RandomBoostEntry current = new RandomBoostEntry(cost, weight, boost, it, next);
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

	private void add(RandomBoostEntry entry)
	{
		RandomBoostEntry it = root;
		while (it.next != null && it.next.nextLevelCost_B > entry.nextLevelCost_B)
		{
			it = it.next;
		}
		if (it.next == null)
		{
			it.next = entry;
			entry.next = null;
			entry.pre = it;
		}
		else
		{
			entry.next = it.next;
			entry.pre = it;
			it.next = entry;
			entry.next.pre = entry;
		}
	}

	private RandomBoostEntry selectBoost()
	{
		if (maxWeight == 0) return null;
		int weight = random.nextInt(maxWeight);
		RandomBoostEntry p = root.next;
		while (p != null)
		{
			if (p.weight > weight) return p;
			weight -= p.weight;
			p = p.next;
		}
		return null;
	}

	private void sort(RandomBoostEntry entry)
	{
		entry.pre.next = entry.next;
		if (entry.next != null) entry.next.pre = entry.pre;
		RandomBoostEntry p = root.next;
		RandomBoostEntry m;
		while (p != null && p.nextLevelCost_B > leftCost)
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
		if (entry.reachMax() || (entry.currentLevel > 0 && entry.nextLevelCost_B > leftCost))
		{
			entry.pre = null;
			entry.next = complete.next;
			complete.next = entry;
			maxWeight -= entry.weight;
		}
		else if (entry.currentLevel > 0) add(entry);
		else
		{
			maxWeight -= entry.weight;
			entry.pre = null;
			entry.next = null;
		}
	}

	private void sortAndApplyConflicts(RandomBoostEntry entry)
	{
		entry.pre.next = entry.next;
		if (entry.next != null) entry.next.pre = entry.pre;
		RandomBoostEntry p = root.next;
		RandomBoostEntry m = p;
		while (p != null)
		{
			m = p;
			p = p.next;
			if (m.currentLevel == 0 && entry.boost.conflictWith(m.boost))
			{
				entry.boost.modify(this, entry);
				if (m.weight < 1 || m.nextLevelCost_B > leftCost)
				{
					maxWeight -= m.weight;
					m.pre.next = p;
					if (p != null) p.pre = m.pre;
					RandomBoostEntry temp = m;
					m = m.pre;
					temp.next = null;
					temp.pre = null;
				}
			}
			else if (m.nextLevelCost_B > leftCost)
			{
				maxWeight -= m.weight;
				m.pre.next = p;
				if (p != null) p.pre = m.pre;
				RandomBoostEntry temp = m;
				m = m.pre;
				if (temp.currentLevel == 0)
				{
					temp.next = null;
					temp.pre = null;
				}
				else
				{
					temp.next = complete.next;
					temp.pre = null;
					complete.next = temp;
				}
			}
		}
		root.next = null;
		while (m != root && m != null)
		{
			p = m;
			m = m.pre;
			add(p);
		}
		if (entry.reachMax() || (entry.currentLevel > 0 && entry.nextLevelCost_B > leftCost))
		{
			entry.pre = null;
			entry.next = complete.next;
			complete.next = entry;
			maxWeight -= entry.weight;
		}
		else if (entry.currentLevel > 0) add(entry);
		else
		{
			maxWeight -= entry.weight;
			entry.pre = null;
			entry.next = null;
		}
	}

	private void throwNew()
	{
		RandomBoostEntry p = root.next;
		RandomBoostEntry m;
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

	private void throwNewNoSmallerThan(RandomBoostEntry entry)
	{
		RandomBoostEntry p = entry.pre;
		RandomBoostEntry m;
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

	private void tryHaveNewBoost(RandomBoostEntry entry)
	{
		if (maxBoostCount > 0)
		{
			if (boostTime < maxBoostTime - getWorldLevelOfXpCost(maxCost - leftCost + entry.nextLevelCost_B)) // 用掉的等级
			{
				boostTime++;
				maxBoostCount--;
				int nextMaxCost = Math.min(maxCost, getMaxAllowXpCostOfWorldLevel(maxBoostTime - boostTime));
				leftCost = leftCost - maxCost + nextMaxCost;
				maxCost = nextMaxCost;
				int left = getLeft(entry.nextLevelCost_B);
				leftCost -= entry.setLevel(entry.costAchieveLevel(left));
				sortAndApplyConflicts(entry);
			}
			else throwNewNoSmallerThan(entry);
		}
		else throwNew();
	}

	private void advance(RandomBoostEntry entry)
	{
		int left = getLeft(entry.nextLevelCost_B);
		leftCost -= entry.setLevel(entry.costAchieveLevel(left));
		sort(entry);
	}

	private BoostComponent generateBoosts()
	{
		RandomBoostEntry entry = selectBoost();
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
		return BoostComponent.createLegalWithCost(map, true, maxCost - leftCost);
	}

	public BoostComponent getBoostComponent()
	{
		return boostComponent;
	}

	public int getBoostTimeConsume()
	{
		return boostTime + boostComponent.level();
	}

	public boolean isEmpty()
	{
		return complete == null;
	}
}
