package org.yang.iw.item.heart;

import com.google.common.collect.HashMultimap;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.yang.iw.boost.function.AttributeModifierFunction;
import org.yang.iw.boost.function.BoostFunctionMap;
import org.yang.iw.boost.function.PretendEnchantInLootTableFunction;
import org.yang.iw.util.constants.AttributeModifierIds;
import org.yang.iw.util.style.Color;

public class CherryHeart extends AbilityHeart
{

	public CherryHeart(Settings settings, Identifier identifier)
	{
		super(settings, Color.PINK_RGB, identifier, level -> BoostFunctionMap.builder()
				.add(AttributeModifierSlot.HAND, new PretendEnchantInLootTableFunction()
				{
					@Override
					public RegistryKey<Enchantment> type()
					{
						return Enchantments.LOOTING;
					}

					@Override
					public float level()
					{
						return level * 0.32f;
					}
				}).add(AttributeModifierSlot.HAND, new AttributeModifierFunction()
				{
					@Override
					protected HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(StringIdentifiable slot)
					{
						HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifierMultiMap =
								HashMultimap.create();
						modifierMultiMap.put(EntityAttributes.LUCK,
								new EntityAttributeModifier(AttributeModifierIds.of(identifier, slot),
										level * 0.25 + 0.5, EntityAttributeModifier.Operation.ADD_VALUE));
						return modifierMultiMap;
					}
				}).build());
	}
}
