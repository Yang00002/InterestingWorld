package org.yang.interestingworld.rune;

import org.yang.interestingworld.IWItemGroups;
import org.yang.interestingworld.rune.upgrade.*;

import java.util.LinkedList;

public class IWRuneUpgrades
{
	private static IWAbstractRuneUpgrade[] UPGRADE_LIST = null;
	public static IWAbstractRuneUpgrade DEFAULT_UPGRADE = new IWAbstractRuneUpgrade();
	public static IWAbstractRuneUpgrade SWEEPING_UPGRADE = new SweepingUpgrade();
	public static IWAbstractRuneUpgrade SWEEPING2_UPGRADE = new Sweeping2Upgrade();
	public static IWAbstractRuneUpgrade SWEEPING3_UPGRADE = new Sweeping3Upgrade();
	public static IWAbstractRuneUpgrade SWEEPING4_UPGRADE = new Sweeping4Upgrade();
	public static IWAbstractRuneUpgrade HEAVY_UPGRADE = new HeavyUpgrade();
	private static short UPGRADE_COUNT = 0;

	private static class UpgradeRegister
	{
		private final LinkedList<IWAbstractRuneUpgrade> list = new LinkedList<>();
		private short[] runeIds = null;
		private short runeIdAllocator = 0;

		public void register(IWAbstractRuneUpgrade ability)
		{
			list.add(ability);
			ability.index = UPGRADE_COUNT;
			UPGRADE_COUNT++;
		}

		public void build()
		{
			UPGRADE_LIST = new IWAbstractRuneUpgrade[UPGRADE_COUNT];
			runeIds = new short[UPGRADE_COUNT];
			int idx = 0;
			for (IWAbstractRuneUpgrade i : list)
			{
				UPGRADE_LIST[idx] = i;
				runeIds[idx] = -1;
				idx++;
			}
			for (int i = 0; i < UPGRADE_COUNT; i++)
			{
				searchRune(i);
				UPGRADE_LIST[i].runeIndex = runeIds[i];
			}
		}

		public void searchRune(int idx)
		{
			if (runeIds[idx] == -1)
			{
				IWAbstractRuneUpgrade ab = UPGRADE_LIST[idx].getRuneIndexParent();
				if (ab == null)
				{
					runeIds[idx] = runeIdAllocator;
					runeIdAllocator++;
				}
				else
				{
					int id2 = ab.index;
					if (runeIds[id2] == -1)
					{
						runeIds[idx] = 0;
						searchRune(id2);
					}
					runeIds[idx] = runeIds[id2];
				}
			}
		}
	}

	public static void addRunesToItemGroup()
	{
		for (int i = 1; i < UPGRADE_COUNT; i++)
			IWItemGroups.addItemToGroup(IWItemGroups.UpgradeRuneItemInitializer.getInstance(UPGRADE_LIST[i]),
					IWItemGroups.RUNES_GROUP);
	}

	public static void initialize()
	{
		IWRuneUpgrades.UpgradeRegister register = new IWRuneUpgrades.UpgradeRegister();
		register.register(DEFAULT_UPGRADE);
		register.register(SWEEPING_UPGRADE);
		register.register(SWEEPING2_UPGRADE);
		register.register(SWEEPING3_UPGRADE);
		register.register(SWEEPING4_UPGRADE);
		register.register(HEAVY_UPGRADE);
		register.build();
	}

	public static IWAbstractRuneUpgrade getUpgradeofIndex(short idx)
	{
		if (idx >= UPGRADE_COUNT || idx <= 0) return DEFAULT_UPGRADE;
		return UPGRADE_LIST[idx];
	}
}
