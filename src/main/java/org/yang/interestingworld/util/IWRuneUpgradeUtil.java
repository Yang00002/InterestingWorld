package org.yang.interestingworld.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.rune_upgrade.AbstractRuneUpgrade;
import org.yang.interestingworld.rune_upgrade.IWRuneUpgrades;

import static org.yang.interestingworld.util.IWRuneAbilityUtil.getModelComponent;

public class IWRuneUpgradeUtil
{
	public static AbstractRuneUpgrade getUpgrade(short index)
	{
		return IWRuneUpgrades.getUpgradeofIndex(index);
	}

	public static AbstractRuneUpgrade getUpgrade(ItemStack stack)
	{
		return IWRuneUpgrades.getUpgradeofIndex(stack.getOrDefault(IWComponents.ABILITY_INDEX, (short) 0));
	}

	public static void setUpgradeOfRune(ItemStack stack, AbstractRuneUpgrade upgrade)
	{
		AbstractRuneUpgrade origin = getUpgrade(stack);
		if (origin.index != upgrade.index)
		{
			stack.set(IWComponents.ABILITY_INDEX, upgrade.index);
			stack.set(IWComponents.ABILITY_COLOR_RGB, upgrade.getColor());
			stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, getModelComponent(upgrade.runeIndex));
		}
	}
}
