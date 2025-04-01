package org.yang.iw.boost;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.yang.iw.api.util.MutableAttributeValueDetail;
import org.yang.iw.boost.function.BoostFunctionMap;
import org.yang.iw.boost.function.ModifyDamageFunction;
import org.yang.iw.boost.pool.RandomBoostEntry;
import org.yang.iw.boost.pool.RandomBoostGenerator;

public class SmiteBoost extends AbstractBoost
{
	SmiteBoost(Identifier identifier)
	{
		super(identifier);
	}

	@Override
	public BoostFunctionMap getFunctions(int level)
	{
		return BoostFunctionMap.builder().add(AttributeModifierSlot.MAINHAND, new ModifyDamageFunction()
		{
			@Override
			public void modifyDamage(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource,
									 MutableAttributeValueDetail baseDamage)
			{
				if (target.getType().isIn(EntityTypeTags.SENSITIVE_TO_SMITE)) baseDamage.sum += level * 0.15f;
			}
		}).build();
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

	@Override
	public boolean conflictWith(AbstractBoost boost)
	{
		return boost.isIn(IWBoostTags.SHARPNESS_FAMILY);
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
	public void modify(RandomBoostGenerator generator, RandomBoostEntry entry)
	{
		if (entry.isIn(IWBoostTags.SHARPNESS_FAMILY))
		{
			int weight = entry.getWeight();
			if (weight > 1) entry.setWeight(generator, Math.max(1, weight - 5));
			entry.setCostMultiplier(entry.getCostMultiplier() + 0.5f);
		}
	}
}
