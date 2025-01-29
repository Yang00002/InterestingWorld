package org.yang.interestingworld.rune_ability;

import java.util.LinkedList;
import java.util.function.Consumer;

public class IWRuneAbilities
{
	private static AbilityRegister register = new AbilityRegister();
	private static AbstractRuneAbility[] ABILITY_LIST = null;
	public static final AbstractRuneAbility DEFAULT_ABILITY = register.register(new AbstractRuneAbility());
	public static final AbstractRuneAbility SLASHING_ABILITY = register.register(new SlashingAbility());
	public static final AbstractRuneAbility INFINITESLASHING_ABILITY =
			register.register(new InfiniteSlashingAbility());
	public static final AbstractRuneAbility SWEETCURSE_ABILITY = register.register(new SweetCurseAbility());
	public static final AbstractRuneAbility INFINITECURSE_ABILITY = register.register(new InfiniteCurseAbility());
	public static final AbstractRuneAbility REPEATSLASHING_ABILITY = register.register(new RepeatSlashingAbility());
	public static final AbstractRuneAbility BOOSTREPEATSLASHING_ABILITY = register.register(
			new BoostRepeatSlashingAbility());
	public static final AbstractRuneAbility EVISCERATE_ABILITY = register.register(new EviscerateAbility());
	public static final AbstractRuneAbility INFINITEEVISCERATE_ABILITY = register.register(
			new InfiniteEviscerateAbility());
	private static final short ABILITY_COUNT;

	static
	{
		ABILITY_COUNT = register.build();
		register = null;
		/*
		 for (int i = 1; i < ABILITY_COUNT; i++)
		{
			int finalI = i;
			IWItemGroups.addItemToGroup((context, entries) -> {
				ItemStack it = IWItems.ABILITY_RUNE.getDefaultStack();
				setAbility(it, ABILITY_LIST[finalI]);
				entries.add(it);
			}, IWItemGroups.RUNES_GROUP);
		}
		 */

	}

	private static class AbilityRegister
	{
		private final LinkedList<AbstractRuneAbility> list = new LinkedList<>();
		private short[] runeIds = null;
		private short[] toolIds = null;
		private short runeIdAllocator = 0;
		private short toolIdAllocator = 0;
		private short abilityCount = 0;

		public AbstractRuneAbility register(AbstractRuneAbility ability)
		{
			list.add(ability);
			ability.index = abilityCount;
			abilityCount++;
			return ability;
		}

		public short build()
		{
			ABILITY_LIST = new AbstractRuneAbility[abilityCount];
			runeIds = new short[abilityCount];
			toolIds = new short[abilityCount];
			int idx = 0;
			for (AbstractRuneAbility i : list)
			{
				ABILITY_LIST[idx] = i;
				runeIds[idx] = -1;
				toolIds[idx] = -1;
				idx++;
			}
			for (int i = 0; i < abilityCount; i++)
			{
				searchRune(i);
				searchTool(i);
				ABILITY_LIST[i].runeIndex = runeIds[i];
				ABILITY_LIST[i].toolIndex = toolIds[i];
			}
			return abilityCount;
		}

		public void searchRune(int idx)
		{
			if (runeIds[idx] == -1)
			{
				AbstractRuneAbility ab = ABILITY_LIST[idx].getRuneIndexParent();
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
				AbstractRuneAbility ab = ABILITY_LIST[idx].getToolIndexParent();
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
	}

	public static AbstractRuneAbility getAbilityofIndex(short idx)
	{
		if (idx >= ABILITY_COUNT) return DEFAULT_ABILITY;
		if (idx <= 0) return DEFAULT_ABILITY;
		return ABILITY_LIST[idx];
	}

	public static void forEach(Consumer<AbstractRuneAbility> consumer)
	{
		for (int i = 1; i < ABILITY_COUNT; i++)
		{
			consumer.accept(ABILITY_LIST[i]);
		}
	}
}
