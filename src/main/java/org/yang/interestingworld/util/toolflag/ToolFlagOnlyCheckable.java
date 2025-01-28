package org.yang.interestingworld.util.toolflag;

public interface ToolFlagOnlyCheckable
{
	int getValue();

	boolean canSweep();

	boolean haveRealEnchantment();

	boolean haveDefaultEnchantment();

	boolean onlyHaveDefaultEnchantment();

	int level();

	int levelColor();

	boolean haveUpgrade();
}
