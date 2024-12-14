package org.yang.interestingworld.util;

import net.minecraft.item.ItemStack;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.rune.IWRuneAbilitys;

import static org.yang.interestingworld.util.RuneAbility.getAbility;
import static org.yang.interestingworld.util.enchantment.RuneEnchantment.getWorldLevelOfXpCost;

public class EnergyTool
{
	public static final int LEVEL_FLAG = 0b01111;
	public static final int LEVEL_UPGRADE_FLAG = 0b011110000;
	public static final int REAL_ENCHANT_FLAG = 0b0100000000;
	public static final int DEFAULT_ENCHANT_FLAG = 0b01000000000;

	public static boolean onlyHaveDefaultEnchantment(ItemStack stack)
	{
		int data = stack.getOrDefault(IWComponents.DATA_FLAGS, 0);
		return ((data & DEFAULT_ENCHANT_FLAG) != 0) && ((data & REAL_ENCHANT_FLAG) == 0);
	}

	public static boolean haveDefaultEnchantment(ItemStack stack)
	{
		int data = stack.getOrDefault(IWComponents.DATA_FLAGS, 0);
		return (data & DEFAULT_ENCHANT_FLAG) != 0;
	}

	public static boolean haveRealEnchantment(ItemStack stack)
	{
		int data = stack.getOrDefault(IWComponents.DATA_FLAGS, 0);
		return (data & REAL_ENCHANT_FLAG) != 0;
	}

	public static void updateLevel(ItemStack stack)
	{
		int flag = stack.getOrDefault(IWComponents.DATA_FLAGS, 0);
		var ab = getAbility(stack);
		int ability_level = (ab == IWRuneAbilitys.DEFAULT_ABILITY ? -1 : ab.level());
		int upgrade_level = ((flag & LEVEL_UPGRADE_FLAG) >> 4) - 1;
		int n = stack.getOrDefault(IWComponents.ENCHANT_VALUE, -1);
		int enchant_level = n > -1 ? getWorldLevelOfXpCost(n) : -1;
		int maxLevel = Math.max(ability_level, Math.max(upgrade_level, enchant_level));
		if (maxLevel == -1) stack.set(IWComponents.DATA_FLAGS, flag & ~LEVEL_FLAG);
		else
		{
			int count = 0;
			if (ability_level >= maxLevel) count++;
			if (upgrade_level >= maxLevel) count++;
			if (enchant_level >= maxLevel) count++;
			if (count > 1 && maxLevel < 10) maxLevel++;
			stack.set(IWComponents.DATA_FLAGS, (flag & ~LEVEL_FLAG) | maxLevel);
		}
	}

	private static void updateLevel(ItemStack stack, int flag)
	{
		var ab = getAbility(stack);
		int ability_level = (ab == IWRuneAbilitys.DEFAULT_ABILITY ? -1 : ab.level());
		int upgrade_level = ((flag & LEVEL_UPGRADE_FLAG) >> 4) - 1;
		int n = stack.getOrDefault(IWComponents.ENCHANT_VALUE, -1);
		int enchant_level = n > -1 ? getWorldLevelOfXpCost(n) : -1;
		int maxLevel = Math.max(ability_level, Math.max(upgrade_level, enchant_level));
		if (maxLevel == -1) stack.set(IWComponents.DATA_FLAGS, flag & ~LEVEL_FLAG);
		else
		{
			int count = 0;
			if (ability_level >= maxLevel) count++;
			if (upgrade_level >= maxLevel) count++;
			if (enchant_level >= maxLevel) count++;
			if (count > 1 && maxLevel < 10) maxLevel++;
			stack.set(IWComponents.DATA_FLAGS, (flag & ~LEVEL_FLAG) | maxLevel);
		}
	}

	public static void setFlagOfRealEnchant(ItemStack stack, int value)
	{
		stack.set(IWComponents.ENCHANT_VALUE, value);
		updateLevel(stack, stack.getOrDefault(IWComponents.DATA_FLAGS, 0) | REAL_ENCHANT_FLAG);
	}

	public static void clearFlagOfRealEnchant(ItemStack stack)
	{
		stack.remove(IWComponents.ENCHANT_VALUE);
		updateLevel(stack, stack.getOrDefault(IWComponents.DATA_FLAGS, 0) & ~REAL_ENCHANT_FLAG);
	}

	public static int getLevelColor(ItemStack stack)
	{
		return IWUtil.TextStyle.getColorByLevel(stack.getOrDefault(IWComponents.DATA_FLAGS, 0) & LEVEL_FLAG);
	}

	public static int getLevel(ItemStack stack)
	{
		return stack.getOrDefault(IWComponents.DATA_FLAGS, 0) & LEVEL_FLAG;
	}

}
