package org.yang.interestingworld.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.rune.IWAbstractRuneUpgrade;
import org.yang.interestingworld.rune.IWRuneUpgrades;

import static org.yang.interestingworld.util.RuneAbility.getModelComponent;

public class RuneUpgrade
{
	public static IWAbstractRuneUpgrade getUpgrade(short index)
	{
		return IWRuneUpgrades.getUpgradeofIndex(index);
	}

	public static IWAbstractRuneUpgrade getUpgrade(ItemStack stack)
	{
		return IWRuneUpgrades.getUpgradeofIndex(stack.getOrDefault(IWComponents.ABILITY_INDEX, (short) 0));
	}

	public static void setUpgradeOfRune(ItemStack stack, IWAbstractRuneUpgrade upgrade)
	{
		IWAbstractRuneUpgrade origin = getUpgrade(stack);
		if (origin.index != upgrade.index)
		{
			stack.set(IWComponents.ABILITY_INDEX, upgrade.index);
			stack.set(IWComponents.ABILITY_COLOR_RGB, upgrade.getColor());
			stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, getModelComponent(upgrade.runeIndex));
		}
	}
}
