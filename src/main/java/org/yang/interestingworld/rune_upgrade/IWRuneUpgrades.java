package org.yang.interestingworld.rune_upgrade;

import net.minecraft.item.ItemStack;
import org.yang.interestingworld.IWItemGroups;
import org.yang.interestingworld.item.IWItems;
import org.yang.interestingworld.rune_upgrade.sweeping.Sweeping2Upgrade;
import org.yang.interestingworld.rune_upgrade.sweeping.Sweeping3Upgrade;
import org.yang.interestingworld.rune_upgrade.sweeping.Sweeping4Upgrade;
import org.yang.interestingworld.rune_upgrade.sweeping.SweepingUpgrade;
import org.yang.interestingworld.util.IWRuneUpgradeUtil;

import java.util.LinkedList;

public class IWRuneUpgrades
{
	private static UpgradeRegister register = new UpgradeRegister();
	private static AbstractRuneUpgrade[] UPGRADE_LIST = null;
	public static AbstractRuneUpgrade DEFAULT_UPGRADE = register.register(new AbstractRuneUpgrade());
	public static AbstractRuneUpgrade SWEEPING_UPGRADE = register.register(new SweepingUpgrade());
	public static AbstractRuneUpgrade SWEEPING2_UPGRADE = register.register(new Sweeping2Upgrade());
	public static AbstractRuneUpgrade SWEEPING3_UPGRADE = register.register(new Sweeping3Upgrade());
	public static AbstractRuneUpgrade SWEEPING4_UPGRADE = register.register(new Sweeping4Upgrade());
	public static AbstractRuneUpgrade HEAVY_UPGRADE = register.register(new HeavyUpgrade());
	private static final short UPGRADE_COUNT;

	static
	{
		UPGRADE_COUNT = register.build();
		register = null;
		for (int i = 1; i < UPGRADE_COUNT; i++)
		{
			int finalI = i;
			IWItemGroups.addItemToGroup((context, entries) -> {
				ItemStack it = IWItems.UPGRADE_RUNE.getDefaultStack();
				IWRuneUpgradeUtil.setUpgradeOfRune(it, UPGRADE_LIST[finalI]);
				entries.add(it);
			}, IWItemGroups.RUNES_GROUP);
		}
	}

	private static class UpgradeRegister
	{
		private final LinkedList<AbstractRuneUpgrade> list = new LinkedList<>();
		private short[] runeIds = null;
		private short runeIdAllocator = 0;
		private short upgradeCount = 0;

		public AbstractRuneUpgrade register(AbstractRuneUpgrade upgrade)
		{
			list.add(upgrade);
			upgrade.index = upgradeCount;
			upgradeCount++;
			return upgrade;
		}

		public short build()
		{
			UPGRADE_LIST = new AbstractRuneUpgrade[upgradeCount];
			runeIds = new short[upgradeCount];
			int idx = 0;
			for (AbstractRuneUpgrade i : list)
			{
				UPGRADE_LIST[idx] = i;
				runeIds[idx] = -1;
				idx++;
			}
			for (int i = 0; i < upgradeCount; i++)
			{
				searchRune(i);
				UPGRADE_LIST[i].runeIndex = runeIds[i];
			}
			return upgradeCount;
		}

		public void searchRune(int idx)
		{
			if (runeIds[idx] == -1)
			{
				AbstractRuneUpgrade ab = UPGRADE_LIST[idx].getRuneIndexParent();
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

	public static void initialize()
	{
	}

	public static AbstractRuneUpgrade getUpgradeofIndex(short idx)
	{
		if (idx >= UPGRADE_COUNT || idx <= 0) return DEFAULT_UPGRADE;
		return UPGRADE_LIST[idx];
	}
}
