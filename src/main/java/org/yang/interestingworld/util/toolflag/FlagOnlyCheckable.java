package org.yang.interestingworld.util.toolflag;

public interface FlagOnlyCheckable
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
