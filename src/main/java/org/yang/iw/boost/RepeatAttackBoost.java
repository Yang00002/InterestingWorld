package org.yang.iw.boost;

import com.google.common.collect.HashMultimap;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.yang.iw.IWEntityAttributes;

public class RepeatAttackBoost extends CertainSlotAttributeBoost
{
	RepeatAttackBoost(Identifier identifier)
	{
		super(identifier, EquipmentSlot.MAINHAND, AttributeModifierSlot.MAINHAND);
	}

	@Override
	protected HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(int level,
																								 StringIdentifiable slot)
	{
		HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifierMultimap = HashMultimap.create();
		modifierMultimap.put(IWEntityAttributes.ATTACK_DURATION_NEGLECT,
				new EntityAttributeModifier(attributeModifierID(slot), level,
						EntityAttributeModifier.Operation.ADD_VALUE));
		return modifierMultimap;
	}

	@Override
	public int xpCostOfLevel(short level)
	{
		return level > 0 ? 120 + 80 * level : 0;
	}

	@Override
	public int xpCostBetweenLevels(short low, short high)
	{
		return low > 0 ? 80 * (high - low) : (high > 0 ? 120 + 80 * high : 0);
	}
}
