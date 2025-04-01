package org.yang.iw.boost.pool;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Pair;
import org.yang.iw.api.register.ServerDependLoader;
import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.boost.IWBoosts;
import org.yang.iw.util.ConsoleStringBuilder;

import java.util.*;
import java.util.function.Supplier;

@ServerDependLoader
public class TableBoostPool
{
	private static final ArrayList<Pair<TableBoostPool, Supplier<TableBoostPool>>> LIST = new ArrayList<>();

	@Override
	public String toString()
	{
		ConsoleStringBuilder builder = new ConsoleStringBuilder();
		builder.append("TableBoostPool of %s pages.".formatted(pages.length));
		builder.tab();
		for (Page page : pages)
		{
			builder.newLine();
			builder.append(page.toString());
		}
		return builder.toString();
	}

	Page[] pages;

	public static class Page
	{
		@Override
		public String toString()
		{
			ConsoleStringBuilder builder = new ConsoleStringBuilder();
			builder.append("Page of %s entries. Weight %s, minLevel %s, maxLevel %s".formatted(entries.length, weight,
					minAppearWorldLevel, maxAppearWorldLevel));
			builder.tab();
			for (Entry entry : entries)
			{
				builder.newLine();
				builder.append(entry.toString());
			}
			return builder.toString();
		}

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
			this.maxAppearWorldLevel = max;
			this.minAppearWorldLevel = min;
			List<Entry> legalEntries = new ArrayList<>();
			next:
			for (var entry : entries)
			{
				if (entry.weight > 0 && entry.boost != null)
				{
					for (var preEntry : legalEntries)
					{
						if (entry.boost == preEntry.boost || preEntry.boost.conflictWith(entry.boost) ||
							entry.boost.conflictWith(preEntry.boost)) continue next;
					}
					legalEntries.add(entry);
				}
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
		static Entry DEFAULT = new Entry(0, null);
		static Map<AbstractBoost, List<Entry>> entryMap = new HashMap<>();

		Entry(int weight, AbstractBoost boost)
		{
			this.boost = boost;
			this.weight = weight;
		}

		@Override
		public String toString()
		{
			return "Entry of %s, weight %s.".formatted(boost.getClass(), weight);
		}

		public static Entry create(int weight, AbstractBoost boost)
		{
			if (boost == null) return DEFAULT;
			var entry = new Entry(weight, boost);
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
				if (entry.weight == entryO.weight) return entryO;
			l.add(entry);
			entryMap.put(boost, l);
			return entry;
		}
	}

	public static TableBoostPool empty()
	{
		return new TableBoostPool(new Page[0]);
	}

	private void load(Supplier<TableBoostPool> from)
	{
		this.pages = from.get().pages;
	}

	private static TableBoostPool create(Supplier<TableBoostPool> supplier)
	{
		var ret = empty();
		LIST.add(new Pair<>(ret, supplier));
		return ret;
	}

	private TableBoostPool(Page[] pages)
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

	public static TableBoostPool pool(Page... pages)
	{
		return new TableBoostPool(pages);
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

	public static Entry entry(int weight, AbstractBoost boost)
	{
		return new Entry(weight, boost);
	}

	public static Entry entry(AbstractBoost boost)
	{
		return new Entry(1, boost);
	}

	public static final TableBoostPool POOL_ROD = create(() -> pool(
			page(1, entry(5, IWBoosts.FIRE_ASPECT), entry(2, IWBoosts.REPEAT_ATTACK), entry(10, IWBoosts.FAST_ATTACK),
					entry(10, IWBoosts.UNBREAKING))));

	public static final TableBoostPool POOL_SWORD = create(() -> pool(
			page(1, entry(5, IWBoosts.FIRE_ASPECT), entry(2, IWBoosts.REPEAT_ATTACK), entry(10, IWBoosts.SHARPNESS),
					entry(10, IWBoosts.UNBREAKING), entry(5, IWBoosts.SWEEPING_EDGE)),
			page(1, entry(5, IWBoosts.FIRE_ASPECT), entry(2, IWBoosts.REPEAT_ATTACK), entry(10, IWBoosts.FAST_ATTACK),
					entry(10, IWBoosts.UNBREAKING), entry(5, IWBoosts.SWEEPING_EDGE))));

	static
	{
		Entry.entryMap = null;
	}

	public static void boostrap()
	{
		Entry.entryMap = new Object2ObjectOpenHashMap<>();
		LIST.forEach(i -> i.getLeft().load(i.getRight()));
		Entry.entryMap = null;
	}
}
