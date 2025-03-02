package org.yang.iw.boost.function;

import com.google.common.collect.HashMultimap;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.StringIdentifiable;

import java.util.function.BiConsumer;

public abstract class AttributeModifierFunction
{

	protected abstract HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(StringIdentifiable slot);

	public void applyAttributeModifier(BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> consumer, EquipmentSlot slot)
	{
		getModifiers(slot).forEach(consumer);
	}

	public void attributeModifierForDisplay(BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> consumer, AttributeModifierSlot slot)
	{
		getModifiers(slot).forEach(consumer);
	}

	public void applyAttributeModifier(ServerWorld world, ItemStack stack, LivingEntity user, EquipmentSlot slot)
	{
		user.getAttributes().addTemporaryModifiers(getModifiers(slot));
	}

	public void removeAttributeModifier(ItemStack stack, LivingEntity user, EquipmentSlot slot)
	{
		user.getAttributes().removeModifiers(this.getModifiers(slot));
	}
}
