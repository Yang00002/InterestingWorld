package org.yang.interestingworld.rune;

import org.yang.interestingworld.IWItemGroups;
import org.yang.interestingworld.rune.ability.*;

import java.util.LinkedList;

public class IWRuneAbilitys
{
	private static IWAbstractRuneAbility[] ABILITY_LIST = null;
	public static IWAbstractRuneAbility DEFAULT_ABILITY = new IWAbstractRuneAbility();
	public static IWAbstractRuneAbility SWEEP_ABILITY = new SweepingAbility();
	public static IWAbstractRuneAbility SLASHING_ABILITY = new SlashingAbility();
	public static IWAbstractRuneAbility INFINITESLASHING_ABILITY = new InfiniteSlashingAbility();
	public static IWAbstractRuneAbility SWEETCURSE_ABILITY = new SweetCurseAbility();
	public static IWAbstractRuneAbility INFINITECURSE_ABILITY = new InfiniteCurseAbility();

	private static short ABILITY_COUNT = 0;

	private static class AbilityRegister
	{
		private final LinkedList<IWAbstractRuneAbility> list = new LinkedList<>();
		private short[] runeIds = null;
		private short[] toolIds = null;
		private short runeIdAllocator = 0;
		private short toolIdAllocator = 0;

		public void register(IWAbstractRuneAbility ability)
		{
			list.add(ability);
			ability.index = ABILITY_COUNT;
			ABILITY_COUNT++;
		}

		public void build()
		{
			ABILITY_LIST = new IWAbstractRuneAbility[ABILITY_COUNT];
			runeIds = new short[ABILITY_COUNT];
			toolIds = new short[ABILITY_COUNT];
			int idx = 0;
			for (IWAbstractRuneAbility i : list)
			{
				ABILITY_LIST[idx] = i;
				runeIds[idx] = -1;
				toolIds[idx] = -1;
				idx++;
			}
			for (int i = 0; i < ABILITY_COUNT; i++)
			{
				searchRune(i);
				searchTool(i);
				ABILITY_LIST[i].runeIndex = runeIds[i];
				ABILITY_LIST[i].toolIndex = toolIds[i];
				if (i != 0)
					IWItemGroups.addItemToGroup(IWItemGroups.AbilityRuneItemInitializer.getInstance(ABILITY_LIST[i]),
							IWItemGroups.RUNES_GROUP);
			}
		}

		public void searchRune(int idx)
		{
			if (runeIds[idx] == -1)
			{
				IWAbstractRuneAbility ab = ABILITY_LIST[idx].getRuneIndexParent();
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

		public void searchTool(int idx)
		{
			if (toolIds[idx] == -1)
			{
				IWAbstractRuneAbility ab = ABILITY_LIST[idx].getToolIndexParent();
				if (ab == null)
				{
					toolIds[idx] = toolIdAllocator;
					toolIdAllocator++;
				}
				else
				{
					int id2 = ab.index;
					if (toolIds[id2] == -1)
					{
						toolIds[idx] = 0;
						searchTool(id2);
					}
					toolIds[idx] = toolIds[id2];
				}
			}
		}
	}


	public static void initialize()
	{
		AbilityRegister register = new AbilityRegister();
		register.register(DEFAULT_ABILITY);
		register.register(SWEEP_ABILITY);
		register.register(SLASHING_ABILITY);
		register.register(INFINITESLASHING_ABILITY);
		register.register(SWEETCURSE_ABILITY);
		register.register(INFINITECURSE_ABILITY);
		register.build();
	}

	public static IWAbstractRuneAbility getAbilityofIndex(short idx)
	{
		if (idx >= ABILITY_COUNT) return DEFAULT_ABILITY;
		if (idx <= 0) return DEFAULT_ABILITY;
		return ABILITY_LIST[idx];
	}
}
