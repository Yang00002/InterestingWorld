package org.yang.iw.util.heartflag;

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
