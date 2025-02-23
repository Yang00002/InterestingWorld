package org.yang.iw.boost;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public abstract class CertainSlotAttributeBoost extends AttributeBoost
{
	private final EquipmentSlot equipmentSlot;
	private final AttributeModifierSlot attributeModifierSlot;

	public CertainSlotAttributeBoost(Identifier identifier, EquipmentSlot equipmentSlot, AttributeModifierSlot attributeModifierSlot)
	{
		super(identifier);
		this.equipmentSlot = equipmentSlot;
		this.attributeModifierSlot = attributeModifierSlot;
	}

	@Override
	public void applyAttributeModifiers(int level,
										BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> consumer,
										EquipmentSlot slot)
	{
		if (slot == equipmentSlot) getModifiers(level, slot).forEach(consumer);
	}

	@Override
	public void applyAttributeModifiers(int level,
										BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> consumer,
										AttributeModifierSlot slot)
	{
		if (slot == attributeModifierSlot) getModifiers(level, slot).forEach(consumer);
	}

	@Override
	public void applyLocationBasedEffects(int level, ServerWorld world, ItemStack stack, LivingEntity user,
										  EquipmentSlot slot)
	{
		if (slot == equipmentSlot) user.getAttributes().addTemporaryModifiers(getModifiers(level, slot));
	}


	@Override
	public void removeLocationBasedEffects(int level, ItemStack stack, LivingEntity user, EquipmentSlot slot)
	{
		if (slot == equipmentSlot) user.getAttributes().removeModifiers(this.getModifiers(level, slot));
	}
}
