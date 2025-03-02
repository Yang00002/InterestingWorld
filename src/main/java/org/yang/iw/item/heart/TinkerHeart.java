package org.yang.iw.item.heart;

import com.google.common.collect.HashMultimap;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.yang.iw.boost.function.AttributeModifierFunction;
import org.yang.iw.boost.function.BoostFunctionMap;
import org.yang.iw.util.constants.AttributeModifierIds;
import org.yang.iw.util.style.Color;

public class TinkerHeart extends AbilityHeart
{

	public TinkerHeart(Settings settings, Identifier identifier)
	{
		super(settings, Color.WHITE_RGB, identifier,
				level -> BoostFunctionMap.builder().add(AttributeModifierSlot.HAND, new AttributeModifierFunction()
				{
					@Override
					protected HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(StringIdentifiable slot)
					{
						HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifierMultiMap =
								HashMultimap.create();
						modifierMultiMap.put(EntityAttributes.ATTACK_DAMAGE,
								new EntityAttributeModifier(AttributeModifierIds.of(identifier, slot, 0),
										level * 0.02 + 0.01, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
						modifierMultiMap.put(EntityAttributes.ATTACK_SPEED,
								new EntityAttributeModifier(AttributeModifierIds.of(identifier, slot, 1),
										level * 0.02 + 0.01, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
						modifierMultiMap.put(EntityAttributes.ENTITY_INTERACTION_RANGE,
								new EntityAttributeModifier(AttributeModifierIds.of(identifier, slot, 2),
										level * 0.04 + 0.05, EntityAttributeModifier.Operation.ADD_VALUE));
						return modifierMultiMap;
					}
				}).build());
	}
}
