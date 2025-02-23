package org.yang.iw.boost;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.boost.pool.RandomBoostEntry;
import org.yang.iw.boost.pool.RandomBoostGenerator;
import org.yang.iw.util.Base;

public class AbstractBoost implements Comparable<AbstractBoost>
{
	private static final AbstractBoost DEFAULT = createInstance();

	public final Identifier identifier;

	private static AbstractBoost createInstance()
	{
		return new AbstractBoost(Identifier.of(Base.MOD_ID, "empty"));
	}

	public static AbstractBoost getDefault()
	{
		return DEFAULT;
	}

	AbstractBoost(Identifier identifier)
	{
		this.identifier = identifier;
	}

	public void applyLocationBasedEffects(int level, ServerWorld world, ItemStack stack, LivingEntity user,
										  EquipmentSlot slot)
	{
	}

	public int xpCostOfLevel(short level)
	{
		return 0;
	}

	public short costAchieveLevel(short from, int costAll)
	{
		while (xpCostOfLevel(++from) <= costAll) ;
		return (short) (from - 1);
	}

	public int xpCostBetweenLevels(short low, short high)
	{
		return xpCostOfLevel(high) - xpCostOfLevel(low);
	}

	public void removeLocationBasedEffects(int level, ItemStack stack, LivingEntity user, EquipmentSlot slot)
	{
	}

	public void onTargetDamaged(int level, ServerWorld world, LivingEntity target, DamageSource damageSource)
	{

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