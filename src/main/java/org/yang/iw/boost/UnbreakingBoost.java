package org.yang.iw.boost;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.mutable.MutableInt;
import org.yang.iw.boost.function.BoostFunctionMap;
import org.yang.iw.boost.function.ItemDamageFunction;
import org.yang.iw.util.constants.Numbers;

public class UnbreakingBoost extends AbstractBoost
{

	@Override
	public BoostFunctionMap getFunctions(int level)
	{
		return BoostFunctionMap.builder().add(AttributeModifierSlot.ANY, new ItemDamageFunction()
		{
			@Override
			public void getItemDamage(ServerWorld world, MutableInt preDamage)
			{
				float nextDamage = (float) (preDamage.getValue() << 1) / (level + 2);
				int base = (int) nextDamage;
				float fraction = nextDamage - base;
				preDamage.setValue(base +
								   ((fraction > Numbers.FLOAT_EPSILON && world.getRandom().nextFloat() <= fraction) ? 1
																													:
									0));
			}
		}).build();
	}

	UnbreakingBoost(Identifier identifier)
	{
		super(identifier);
	}

	@Override
	public int xpCostBetweenLevels(short low, short high)
	{
		return (high - low) * 100;
	}

	public short maxTableLevel()
	{
		return 6;
	}

	public short maxRandomLevel()
	{
		return 12;
	}

	@Override
	public int xpCostOfLevel(short level)
	{
		return 100 * level;
	}


	@Override
	public short costAchieveLevel(short from, short to, int costAll)
	{
		return (short) Math.clamp((costAll / 100), from, to);
	}
}
