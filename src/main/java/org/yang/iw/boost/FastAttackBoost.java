package org.yang.iw.boost;

import com.google.common.collect.HashMultimap;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.yang.iw.boost.function.AttributeModifierFunction;
import org.yang.iw.boost.function.BoostFunctionMap;
import org.yang.iw.boost.pool.RandomBoostEntry;
import org.yang.iw.boost.pool.RandomBoostGenerator;
import org.yang.iw.util.constants.AttributeModifierIds;

public class FastAttackBoost extends AbstractBoost
{
	@Override
	public BoostFunctionMap getFunctions(int level)
	{
		return BoostFunctionMap.builder().add(AttributeModifierSlot.MAINHAND, new AttributeModifierFunction()
		{
			@Override
			protected HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(StringIdentifiable slot)
			{
				HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifierMultiMap =
						HashMultimap.create();
				modifierMultiMap.put(EntityAttributes.ATTACK_SPEED,
						new EntityAttributeModifier(AttributeModifierIds.of(identifier, slot), level * 0.1,
								EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
				return modifierMultiMap;
			}
		}).build();
	}

	FastAttackBoost(Identifier identifier)
	{
		super(identifier);
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
		return IWBoostTags.SHARPNESS_FAMILY.include(boost);
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
		if (IWBoostTags.SHARPNESS_FAMILY.include(this))
		{
			int weight = entry.getWeight();
			if (weight > 1) entry.setWeight(generator, Math.max(1, weight - 5));
			entry.setCostMultiplier(entry.getCostMultiplier() + 0.5f);
		}
	}
}
