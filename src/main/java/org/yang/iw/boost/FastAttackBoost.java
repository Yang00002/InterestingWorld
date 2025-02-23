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
import org.yang.iw.boost.pool.RandomBoostEntry;
import org.yang.iw.boost.pool.RandomBoostGenerator;

public class FastAttackBoost extends CertainSlotAttributeBoost
{
	FastAttackBoost(Identifier identifier)
	{
		super(identifier, EquipmentSlot.MAINHAND, AttributeModifierSlot.MAINHAND);
	}

	@Override
	protected HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(int level,
																								 StringIdentifiable slot)
	{
		HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifierMultimap = HashMultimap.create();
		modifierMultimap.put(EntityAttributes.ATTACK_SPEED,
				new EntityAttributeModifier(attributeModifierID(slot), level * 0.1,
						EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
		return modifierMultimap;
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
	public short costAchieveLevel(short from, int costAll)
	{
		return (short) (costAll / 100);
	}

	@Override
	public boolean conflictWith(AbstractBoost boost)
	{
		return IWBoostTags.SHARPNESS_FAMILY.include(this);
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
