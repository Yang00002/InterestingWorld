package org.yang.iw.boost.pool;

import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.boost.IWBoosts;

import java.util.*;

public class RandomBoostPool
{
	Page[] pages;

	public static class Page
	{
		Page(int weight, int min, int max, Entry[] entries)
		{
			if (weight < 1)
			{
				this.entries = null;
				this.weight = 0;
				return;
			}
			if (min > max || min < 0 || max > 10)
			{
				this.maxAppearWorldLevel = 10;
				this.minAppearWorldLevel = 0;
				this.entries = null;
				this.weight = 0;
				return;
			}
			int emin = 10;
			int emax = 0;
			List<Entry> legalEntries = new ArrayList<>();
			next:
			for (var entry : entries)
			{
				if (entry.weight > 0 && entry.boost != null && entry.minAppearWorldLevel <= max &&
					entry.maxAppearWorldLevel >= min)
				{
					for (var preEntry : legalEntries)
					{
						if (entry.boost == preEntry.boost || preEntry.boost.fatalConflictWith(entry.boost) ||
							entry.boost.fatalConflictWith(preEntry.boost)) continue next;
					}
					legalEntries.add(entry);
					if (entry.minAppearWorldLevel < emin) emin = entry.minAppearWorldLevel;
					if (entry.maxAppearWorldLevel > emax) emax = entry.maxAppearWorldLevel;
				}
			}
			this.maxAppearWorldLevel = Math.min(max, emax);
			this.minAppearWorldLevel = Math.max(min, emin);
			if (this.maxAppearWorldLevel < this.minAppearWorldLevel)
			{
				this.entries = null;
				this.weight = 0;
				return;
			}
			int size = legalEntries.size();
			if (size < 1)
			{
				this.entries = null;
				this.weight = 0;
				return;
			}
			this.entries = new Entry[size];
			for (int i = 0; i < size; i++)
				this.entries[i] = legalEntries.get(i);
			this.weight = weight;
		}

		Entry[] entries;
		int weight;
		int minAppearWorldLevel;
		int maxAppearWorldLevel;
	}

	public static class Entry
	{
		AbstractBoost boost;
		int weight;
		int minAppearWorldLevel;
		int maxAppearWorldLevel;
		static Entry DEFAULT = new Entry(0, 0, 0, null);
		static Map<AbstractBoost, List<Entry>> entryMap = new HashMap<>();


		public boolean parameterEqual(Entry entry)
		{
			return minAppearWorldLevel == entry.minAppearWorldLevel &&
				   maxAppearWorldLevel == entry.maxAppearWorldLevel && weight == entry.weight;
		}

		public static Entry create(int weight, int min, int max, AbstractBoost boost)
		{
			if (boost == null) return DEFAULT;
			var entry = new Entry(weight, min, max, boost);
			if (entry.weight <= 0) return DEFAULT;
			if (entryMap == null) return entry;
			List<Entry> l = entryMap.getOrDefault(boost, null);
			if (l == null)
			{
				l = new ArrayList<>();
				l.add(entry);
				entryMap.put(boost, l);
				return entry;
			}
			for (Entry entryO : l)
				if (entry.parameterEqual(entryO)) return entryO;
			l.add(entry);
			entryMap.put(boost, l);
			return entry;
		}

		private Entry(int weight, int min, int max, AbstractBoost boost)
		{
			this.boost = boost;
			if (min > max || min < 0 || max > 10)
			{
				this.weight = 0;
				return;
			}
			this.weight = weight;
			this.minAppearWorldLevel = min;
			this.maxAppearWorldLevel = max;
		}
	}

	private RandomBoostPool(Page[] pages)
	{
		List<Page> legalPages = new ArrayList<>();
		for (Page page : pages)
		{
			if (page.weight > 0 && page.entries != null) legalPages.add(page);
		}
		int size = legalPages.size();
		this.pages = new Page[size];
		for (int i = 0; i < size; i++)
			this.pages[i] = legalPages.get(i);
	}

	public Page randomPage(int worldLevel, Random random)
	{
		int maxWeight = 0;
		for (Page page : pages)
		{
			if (page.minAppearWorldLevel <= worldLevel && page.maxAppearWorldLevel >= worldLevel)
				maxWeight += page.weight;
		}
		if (maxWeight == 0) return null;
		int n = random.nextInt(maxWeight);
		for (Page page : pages)
		{
			if (page.minAppearWorldLevel <= worldLevel && page.maxAppearWorldLevel >= worldLevel)
			{
				if (page.weight > n) return page;
				n -= page.weight;
			}
		}
		return null;
	}

	public static RandomBoostPool pool(Page... pages)
	{
		return new RandomBoostPool(pages);
	}

	public static Page page(int weight, Entry... entries)
	{
		return new Page(weight, 0, 10, entries);
	}

	public static Page page(int weight, int min, Entry... entries)
	{
		return new Page(weight, min, 10, entries);
	}

	public static Page page(int weight, int max, int min, Entry... entries)
	{
		return new Page(weight, min, max, entries);
	}

	public static Page page(Entry entry)
	{
		return new Page(1, 0, 10, new Entry[]{entry});
	}

	public static Entry entry(int weight, int min, int max, AbstractBoost boost)
	{
		return Entry.create(weight, min, max, boost);
	}

	public static Entry entry(int weight, int min, AbstractBoost boost)
	{
		return Entry.create(weight, min, 10, boost);
	}

	public static Entry entry(int weight, AbstractBoost boost)
	{
		return Entry.create(weight, 0, 10, boost);
	}

	public static Entry entry(AbstractBoost boost)
	{
		return Entry.create(1, 0, 10, boost);
	}

	public static final RandomBoostPool ALL = pool(
			page(1, entry(5, IWBoosts.FIRE_ASPECT), entry(2, IWBoosts.REPEAT_ATTACK), entry(9, IWBoosts.SHARPNESS),
					entry(9, IWBoosts.FAST_ATTACK), entry(10, IWBoosts.UNBREAKING), entry(5, IWBoosts.SWEEPING_EDGE),
					entry(1, IWBoosts.MENDING)));

	static
	{
		Entry.entryMap = null;
	}
}
