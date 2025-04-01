package org.yang.iw.mixin.helper;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.Nullable;
import org.yang.iw.api.util.MutableAttributeValueDetail;

import java.util.function.BiConsumer;

import static org.yang.iw.util.constants.Numbers.floatEqual2Ep;

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

	public static float getDamage(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource,
								  float baseDamage)
	{
		MutableAttributeValueDetail mutableAttributeValueDetail;
		var source = damageSource.getSource();
		if (source instanceof LivingEntity livingEntity &&
			floatEqual2Ep(baseDamage, (float) livingEntity.getAttributeValue(EntityAttributes.ATTACK_DAMAGE)))
		{
			var instance = livingEntity.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
			if (instance != null) mutableAttributeValueDetail = new MutableAttributeValueDetail(
					instance.interestingWorld$getValueDetail());
			else mutableAttributeValueDetail = new MutableAttributeValueDetail(baseDamage);
		}
		else mutableAttributeValueDetail = new MutableAttributeValueDetail(baseDamage);
		stack.interestingWorld$getBoosts()
				.modifyDamage(world, stack, target, damageSource, mutableAttributeValueDetail);
		return mutableAttributeValueDetail.value();
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

	public static float modifyKnockback(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource,
										float baseKnockback)
	{
		return stack.interestingWorld$getBoosts().modifyKnockback(world, stack, target, damageSource, baseKnockback);
	}
}
