package org.yang.iw.boost;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.util.Identifier;
import org.yang.iw.api.util.MutableAttributeValueDetail;
import org.yang.iw.boost.function.BoostFunctionMap;
import org.yang.iw.boost.function.ModifyMaxEnergyFunction;

public class EnergySavingBoost extends AbstractBoost
{
	EnergySavingBoost(Identifier identifier)
	{
		super(identifier);
	}

	@Override
	public BoostFunctionMap getFunctions(int level)
	{
		return BoostFunctionMap.builder().add(AttributeModifierSlot.ANY, new ModifyMaxEnergyFunction()
		{
			@Override
			public void modifyMaxEnergy(MutableAttributeValueDetail energy)
			{
				energy.base += 1;
				energy.sum += 0.2f * level;
			}
		}).build();
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
		return level * 100;
	}

	@Override
	public int xpCostBetweenLevels(short low, short high)
	{
		return (high - low) * 100;
	}

	@Override
	public short costAchieveLevel(short from, short to, int costAll)
	{
		return (short) Math.clamp((costAll / 100), from, to);
	}
}
