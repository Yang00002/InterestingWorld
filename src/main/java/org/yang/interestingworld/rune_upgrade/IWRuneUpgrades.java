package org.yang.interestingworld.rune_upgrade;

import net.minecraft.data.client.Models;
import org.yang.interestingworld.item.item_builder.CommonItemBuilder;
import org.yang.interestingworld.item.upgrade.UpgradeTemplate;
import org.yang.interestingworld.rune_upgrade.sweeping.Sweeping2Upgrade;
import org.yang.interestingworld.rune_upgrade.sweeping.Sweeping3Upgrade;
import org.yang.interestingworld.rune_upgrade.sweeping.Sweeping4Upgrade;
import org.yang.interestingworld.rune_upgrade.sweeping.SweepingUpgrade;

public class IWRuneUpgrades
{
	private static AbstractRuneUpgrade register(AbstractRuneUpgrade upgrade)
	{
		new CommonItemBuilder(settings -> new UpgradeTemplate(settings, upgrade),
				upgrade.getIdentifierString() + "_upgrade_template").setCommonModel(Models.GENERATED).build();
		return upgrade;
	}

	//private static UpgradeRegister register = new UpgradeRegister();
	public static AbstractRuneUpgrade SWEEPING_UPGRADE = register(new SweepingUpgrade());
	public static AbstractRuneUpgrade SWEEPING2_UPGRADE = register(new Sweeping2Upgrade());
	public static AbstractRuneUpgrade SWEEPING3_UPGRADE = register(new Sweeping3Upgrade());
	public static AbstractRuneUpgrade SWEEPING4_UPGRADE = register(new Sweeping4Upgrade());
	public static AbstractRuneUpgrade HEAVY_UPGRADE = register(new HeavyUpgrade());

	public static void initialize()
	{
	}
}
