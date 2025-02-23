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

public class CherryHeart extends AbilityHeart
{

	public CherryHeart(Settings settings, Identifier identifier)
	{
		super(settings, Color.PINK_RGB, identifier,
				new CertainSlotAttributeBoost(identifier, EquipmentSlot.OFFHAND, AttributeModifierSlot.OFFHAND)
				{
					@Override
					protected HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(int level, StringIdentifiable slot)
					{
						HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifierMultiMap =
								HashMultimap.create();
						modifierMultiMap.put(EntityAttributes.LUCK,
								new EntityAttributeModifier(attributeModifierID(slot), level * 0.25 + 0.5,
										EntityAttributeModifier.Operation.ADD_VALUE));
						return modifierMultiMap;
					}
				});
	}
}
