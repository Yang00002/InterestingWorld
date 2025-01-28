package org.yang.interestingworld.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class IWAttributeModifierUtil
{
	public static void addModifier(AttributeModifiersComponent.Builder builder,
								   RegistryEntry<EntityAttribute> attributeRegistryEntry, Identifier id, double value,
								   EntityAttributeModifier.Operation operation, AttributeModifierSlot slot)
	{
		builder.add(attributeRegistryEntry, new EntityAttributeModifier(id, value, operation), slot);
	}

	public static AttributeModifiersComponent.Builder getModifiersFromItemStack(ItemStack stack)
	{
		AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();
		for (var modifier : stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS,
				AttributeModifiersComponent.DEFAULT).modifiers())
		{
			builder.add(modifier.attribute(), modifier.modifier(), modifier.slot());
		}
		return builder;
	}

	public static void applyAttributeModifierToItemStack(AttributeModifiersComponent.Builder builder, ItemStack stack)
	{
		stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
	}
}
