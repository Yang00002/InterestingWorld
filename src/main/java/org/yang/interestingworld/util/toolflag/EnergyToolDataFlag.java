package org.yang.interestingworld.util.toolflag;

import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.rune_ability.IWRuneAbilities;
import org.yang.interestingworld.util.style.Color;

import static org.yang.interestingworld.util.IWEnchantmentUtil.getWorldLevelOfXpCost;
import static org.yang.interestingworld.util.IWRuneAbilityUtil.getAbility;

public class EnergyToolDataFlag implements ToolFlagOnlyCheckable
{
	private static final EnergyToolDataFlag EMPTY = new EnergyToolDataFlag(0);
	private int flag;
	public static Codec<EnergyToolDataFlag> CODEC = Codec.INT.xmap(EnergyToolDataFlag::new,
			EnergyToolDataFlag::getValue);

	EnergyToolDataFlag(int i)
	{
		flag = i;
	}

	private static final int LEVEL_FLAG = 0b01111;
	private static final int LEVEL_UPGRADE_FLAG = 0b011110000;
	private static final int REAL_ENCHANT_FLAG = 0b0100000000;
	private static final int DEFAULT_ENCHANT_FLAG = 0b01000000000;
	private static final int SWEEP_FLAG = 0b010000000000;

	public static ToolFlagOnlyCheckable getFromItemStack(ItemStack stack)
	{
		return stack.getOrDefault(IWComponents.TOOL_FLAG, EMPTY);
	}

	public static EnergyToolDataFlag copyFromItemStack(ItemStack stack)
	{
		var ret = stack.getOrDefault(IWComponents.TOOL_FLAG, null);
		if (ret == null) return new EnergyToolDataFlag(0);
		else return new EnergyToolDataFlag(ret.flag);
	}

	public static EnergyToolDataFlag copyEmpty()
	{
		return new EnergyToolDataFlag(EMPTY.flag);
	}

	public int getValue()
	{
		return flag;
	}

	public boolean canSweep()
	{
		return (flag & SWEEP_FLAG) > 0;
	}

	public EnergyToolDataFlag setCanSweep()
	{
		flag |= SWEEP_FLAG;
		return this;
	}

	public EnergyToolDataFlag setUpgradeLevel(int level)
	{
		flag = (flag & ~LEVEL_UPGRADE_FLAG) | ((level + 1) << 4);
		return this;
	}

	public EnergyToolDataFlag removeCanSweep()
	{
		flag &= ~SWEEP_FLAG;
		return this;
	}

	public boolean haveRealEnchantment()
	{
		return (flag & REAL_ENCHANT_FLAG) > 0;
	}

	public EnergyToolDataFlag setHaveRealEnchantment()
	{
		flag |= REAL_ENCHANT_FLAG;
		return this;
	}

	public EnergyToolDataFlag removeHaveRealEnchantment()
	{
		flag &= ~REAL_ENCHANT_FLAG;
		return this;
	}

	public boolean haveDefaultEnchantment()
	{
		return (flag & DEFAULT_ENCHANT_FLAG) > 0;
	}

	public boolean onlyHaveDefaultEnchantment()
	{
		return ((flag & DEFAULT_ENCHANT_FLAG) > 0) && ((flag & REAL_ENCHANT_FLAG) < 1);
	}

	public boolean haveUpgrade()
	{
		return ((flag & LEVEL_UPGRADE_FLAG) >> 4) > 0;
	}

	public EnergyToolDataFlag setHaveDefaultEnchantment()
	{
		flag |= DEFAULT_ENCHANT_FLAG;
		return this;
	}

	public EnergyToolDataFlag removeHaveDefaultEnchantment()
	{
		flag &= ~DEFAULT_ENCHANT_FLAG;
		return this;
	}

	public int level()
	{
		return flag & LEVEL_FLAG;
	}

	public int levelColor()
	{
		return Color.getLevelColor(flag & LEVEL_FLAG);
	}

	public EnergyToolDataFlag setLevel(int level)
	{
		flag = (flag & ~LEVEL_FLAG) | level;
		return this;
	}

	// 从 ItemStack 获取 ability_level 和 enchant_level
	public EnergyToolDataFlag updateLevelFromItemStack(ItemStack stack)
	{
		var ab = getAbility(stack);
		int ability_level = (ab == IWRuneAbilities.DEFAULT_ABILITY ? -1 : ab.level());
		int upgrade_level = ((flag & LEVEL_UPGRADE_FLAG) >> 4) - 1;
		int n = stack.getOrDefault(IWComponents.ENCHANT_VALUE, -1);
		int enchant_level = n > -1 ? getWorldLevelOfXpCost(n) : -1;
		int maxLevel = Math.max(ability_level, Math.max(upgrade_level, enchant_level));
		if (maxLevel == -1) flag &= ~LEVEL_FLAG;
		else
		{
			int count = 0;
			if (ability_level >= maxLevel) count++;
			if (upgrade_level >= maxLevel) count++;
			if (enchant_level >= maxLevel) count++;
			if (count > 1 && maxLevel < 8) maxLevel++;
			flag = (flag & ~LEVEL_FLAG) | maxLevel;
		}
		return this;
	}
}
