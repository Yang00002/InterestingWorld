package org.yang.iw.boost;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.util.Identifier;
import org.yang.iw.boost.function.BoostFunctionMap;
import org.yang.iw.boost.function.LeveledSignalFunction;

public class MendingBoost extends AbstractBoost
{

	@Override
	public BoostFunctionMap getFunctions(int level)
	{
		return BoostFunctionMap.builder().add(AttributeModifierSlot.ANY, new LeveledSignalFunction()
		{
			@Override
			public Signal type()
			{
				return Signal.MENDING;
			}

			@Override
			public int level()
			{
				return level;
			}
		}).build();
	}

	MendingBoost(Identifier identifier)
	{
		super(identifier);
	}

	@Override
	public int xpCostBetweenLevels(short low, short high)
	{
		return low == 0 ? (1000 + 800 * high) : ((high - low) * 1000);
	}

	public short maxTableLevel()
	{
		return 1;
	}

	public short maxRandomLevel()
	{
		return 3;
	}

	@Override
	public int xpCostOfLevel(short level)
	{
		return level == 0 ? 0 : (1000 + 800 * level);
	}

	@Override
	public short costAchieveLevel(short from, short to, int costAll)
	{
		return (short) Math.clamp((costAll - 1000) / 800, from, to);
	}
}
