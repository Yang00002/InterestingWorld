package org.yang.iw.item.heart;

import com.google.common.collect.HashMultimap;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.yang.iw.boost.CertainSlotAttributeBoost;
import org.yang.iw.util.style.Color;

public class TinkerHeart extends AbilityHeart
{

	public TinkerHeart(Settings settings, Identifier identifier)
	{
		super(settings, Color.WHITE_RGB, identifier,
				new CertainSlotAttributeBoost(identifier, EquipmentSlot.OFFHAND, AttributeModifierSlot.OFFHAND)
				{
					@Override
					protected HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(int level, StringIdentifiable slot)
					{
						HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifierMultiMap =
								HashMultimap.create();
						modifierMultiMap.put(EntityAttributes.ATTACK_DAMAGE,
								new EntityAttributeModifier(attributeModifierID(slot, 0), level * 0.02 + 0.01,
										EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
						modifierMultiMap.put(EntityAttributes.ATTACK_SPEED,
								new EntityAttributeModifier(attributeModifierID(slot, 1), level * 0.02 + 0.01,
										EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
						modifierMultiMap.put(EntityAttributes.ENTITY_INTERACTION_RANGE,
								new EntityAttributeModifier(attributeModifierID(slot, 2), level * 0.04 + 0.05,
										EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
						return modifierMultiMap;
					}
				});
	}
}
