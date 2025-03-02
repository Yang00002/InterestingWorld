package org.yang.iw.mixin.helper;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class HelperEnchantmentHelper
{

	public static void onTargetDamaged(ServerWorld world, Entity target, DamageSource damageSource,
									   @Nullable ItemStack weapon)
	{
		if (weapon != null && target instanceof LivingEntity entity)
			weapon.interestingWorld$getBoosts().onTargetDamaged(weapon, world, entity, damageSource);
	}

	public static void applyLocationBasedEffects(ServerWorld world, ItemStack stack, LivingEntity user,
												 EquipmentSlot slot)
	{
		stack.interestingWorld$getBoosts().applyLocationBasedEffects(stack, world, user, slot);
	}

	public static void applyLocationBasedEffects(ServerWorld world, LivingEntity user)
	{
		for (EquipmentSlot slot : EquipmentSlot.VALUES)
		{
			ItemStack stack = user.getEquippedStack(slot);
			stack.interestingWorld$getBoosts().applyLocationBasedEffects(stack, world, user, slot);
		}
	}

	public static void removeLocationBasedEffects(LivingEntity user)
	{
		for (EquipmentSlot slot : EquipmentSlot.VALUES)
		{
			ItemStack stack = user.getEquippedStack(slot);
			stack.interestingWorld$getBoosts().removeLocationBasedEffects(stack, user, slot);
		}
	}

	public static void removeLocationBasedEffects(ItemStack stack, LivingEntity user, EquipmentSlot slot)
	{
		stack.interestingWorld$getBoosts().removeLocationBasedEffects(stack, user, slot);
	}

	public static void applyAttributeModifiers(ItemStack stack, AttributeModifierSlot slot,
											   BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeModifierConsumer)
	{
		stack.interestingWorld$getBoosts().applyAttributeModifiers(stack, slot, attributeModifierConsumer);
	}

	public static void applyAttributeModifiers(ItemStack stack, EquipmentSlot slot,
											   BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeModifierConsumer)
	{
		stack.interestingWorld$getBoosts().applyAttributeModifiers(stack, slot, attributeModifierConsumer);
	}

	public static int getItemDamage(ServerWorld world, ItemStack stack, int damage)
	{
		return stack.interestingWorld$getBoosts().getItemDamage(world, stack, damage);
	}

	public static float getEquipmentDropChance(ServerWorld world, LivingEntity attacker, DamageSource damageSource,
											   float baseEquipmentDropChance)
	{
		MutableFloat mutableFloat = new MutableFloat(baseEquipmentDropChance);
		for (EquipmentSlot equipmentSlot : EquipmentSlot.VALUES)
		{
			ItemStack stack = attacker.getEquippedStack(equipmentSlot);
			stack.interestingWorld$getBoosts()
					.getEquipmentDropChance(stack, equipmentSlot, world, attacker, damageSource, mutableFloat);
		}
		return mutableFloat.floatValue();
	}
}
