package org.yang.iw.boost;

import com.google.common.collect.HashMultimap;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

import java.util.function.BiConsumer;

public abstract class AttributeBoost extends AbstractBoost
{

	AttributeBoost(Identifier identifier)
	{
		super(identifier);
	}

	protected abstract HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(int level,
																										  StringIdentifiable slot);

	public Identifier attributeModifierID(StringIdentifiable slot)
	{
		return Identifier.of(identifier.getNamespace(), identifier.getPath() + ".modifier." + slot.asString());
	}

	public Identifier attributeModifierID(StringIdentifiable slot, int idx)
	{
		return Identifier.of(identifier.getNamespace(),
				identifier.getPath() + ".modifier." + slot.asString() + "." + idx);
	}

	public abstract void applyAttributeModifiers(int level, BiConsumer<RegistryEntry<EntityAttribute>,
			EntityAttributeModifier> consumer, EquipmentSlot slot);

	public abstract void applyAttributeModifiers(int level, BiConsumer<RegistryEntry<EntityAttribute>,
			EntityAttributeModifier> consumer, AttributeModifierSlot slot);
}
