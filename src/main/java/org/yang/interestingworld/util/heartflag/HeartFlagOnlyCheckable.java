package org.yang.interestingworld.util.heartflag;

public interface HeartFlagOnlyCheckable
{

	enum HeartTypeTaking
	{
		NULL, ENCHANT, ABILITY, PREENCHANT
	}

	int getValue();

	int getMaterialLevel();

	int getTakingLevel();

	HeartTypeTaking getTypeTaking();
}
