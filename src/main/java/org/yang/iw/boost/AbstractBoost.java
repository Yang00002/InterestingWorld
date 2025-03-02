package org.yang.iw.boost;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;
import org.yang.iw.boost.function.BoostFunctionMap;
import org.yang.iw.boost.pool.RandomBoostEntry;
import org.yang.iw.boost.pool.RandomBoostGenerator;
import org.yang.iw.util.Base;

public abstract class AbstractBoost implements Comparable<AbstractBoost>
{

	private static final AbstractBoost DEFAULT = createInstance();

	public final Identifier identifier;

	public abstract BoostFunctionMap getFunctions(int level);

	private static AbstractBoost createInstance()
	{
		return new AbstractBoost(Identifier.of(Base.MOD_ID, "empty"))
		{

			@Override
			public BoostFunctionMap getFunctions(int level)
			{
				return BoostFunctionMap.DEFAULT;
			}

			@Override
			public int xpCostOfLevel(short level)
			{
				return level;
			}
		};
	}

	public static AbstractBoost getDefault()
	{
		return DEFAULT;
	}

	AbstractBoost(Identifier identifier)
	{
		this.identifier = identifier;
	}


	public abstract int xpCostOfLevel(short level);

	public short costAchieveLevel(short from, short to, int costAll)
	{
		if (xpCostOfLevel(to) <= costAll) return to;
		to--;
		from++;
		while (from <= to)
		{
			short mid = (short) ((from + to) >> 1);
			int cost = xpCostOfLevel(mid);
			if (cost > costAll) to = (short) (mid - 1);
			else if (cost < costAll) from = (short) (mid + 1);
			else return mid;
		}
		return (short) (from - 1);
	}


	public int xpCostBetweenLevels(short low, short high)
	{
		return xpCostOfLevel(high) - xpCostOfLevel(low);
	}

	@Override
	public int compareTo(@NotNull AbstractBoost boost)
	{
		return identifier.compareTo(boost.identifier);
	}

	public final String translationKey()
	{
		return "boost." + identifier.getNamespace() + "." + identifier.getPath();
	}

	public short maxAllowLevel()
	{
		return 255;
	}

	public boolean conflictWith(AbstractBoost boost)
	{
		return false;
	}

	public boolean fatalConflictWith(AbstractBoost boost)
	{
		return false;
	}

	public void modify(RandomBoostGenerator generator, RandomBoostEntry entry)
	{
	}

	public short maxTableLevel()
	{
		return maxRandomLevel();
	}

	public short maxRandomLevel()
	{
		return maxAllowLevel();
	}
}