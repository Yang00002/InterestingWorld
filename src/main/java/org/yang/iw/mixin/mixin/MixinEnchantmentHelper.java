package org.yang.iw.mixin.mixin;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.yang.iw.mixin.helper.HelperEnchantmentHelper;

import java.util.function.BiConsumer;

@Mixin(EnchantmentHelper.class)
public abstract class MixinEnchantmentHelper
{
	@Inject(method =
			"applyLocationBasedEffects(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;" +
			"Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;)V", at = @At(value = "TAIL"))
	private static void applyBoosts(ServerWorld world, ItemStack stack, LivingEntity user, EquipmentSlot slot,
									CallbackInfo ci)
	{
		HelperEnchantmentHelper.applyLocationBasedEffects(world, stack, user, slot);
	}

	@Inject(method = "applyLocationBasedEffects(Lnet/minecraft/server/world/ServerWorld;" +
					 "Lnet/minecraft/entity/LivingEntity;)V", at = @At(value = "TAIL"))
	private static void applyBoosts2(ServerWorld world, LivingEntity user, CallbackInfo ci)
	{
		HelperEnchantmentHelper.applyLocationBasedEffects(world, user);
	}

	@Inject(method = "removeLocationBasedEffects(Lnet/minecraft/entity/LivingEntity;)V", at = @At(value = "TAIL"))
	private static void removeBoosts(LivingEntity user, CallbackInfo ci)
	{
		HelperEnchantmentHelper.removeLocationBasedEffects(user);
	}

	@Inject(method = "removeLocationBasedEffects(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;" +
					 "Lnet/minecraft/entity/EquipmentSlot;)V", at = @At(value = "TAIL"))
	private static void removeBoosts2(ItemStack stack, LivingEntity user, EquipmentSlot slot, CallbackInfo ci)
	{
		HelperEnchantmentHelper.removeLocationBasedEffects(stack, user, slot);
	}

	@Inject(method = "applyAttributeModifiers(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EquipmentSlot;" +
					 "Ljava/util/function/BiConsumer;)V", at = @At(value = "TAIL"))
	private static void applyBoostAttributeModifiers(ItemStack stack, EquipmentSlot slot,
													 BiConsumer<RegistryEntry<EntityAttribute>,
															 EntityAttributeModifier> attributeModifierConsumer,
													 CallbackInfo ci)
	{
		HelperEnchantmentHelper.applyAttributeModifiers(stack, slot, attributeModifierConsumer);
	}

	@Inject(method = "getDamage", at = @At(value = "RETURN"), cancellable = true)
	private static void getDamage(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource,
								  float baseDamage, CallbackInfoReturnable<Float> cir)
	{
		cir.setReturnValue(cir.getReturnValue() +
						   HelperEnchantmentHelper.getDamage(world, stack, target, damageSource, baseDamage) -
						   baseDamage);
	}


	@Inject(method = "applyAttributeModifiers(Lnet/minecraft/item/ItemStack;" +
					 "Lnet/minecraft/component/type/AttributeModifierSlot;Ljava/util/function/BiConsumer;)V", at =
	@At(value = "TAIL"))
	private static void applyBoostAttributeModifiers2(ItemStack stack, AttributeModifierSlot slot,
													  BiConsumer<RegistryEntry<EntityAttribute>,
															  EntityAttributeModifier> attributeModifierConsumer,
													  CallbackInfo ci)
	{
		HelperEnchantmentHelper.applyAttributeModifiers(stack, slot, attributeModifierConsumer);
	}

	@Inject(method = "onTargetDamaged(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;" +
					 "Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/item/ItemStack;" +
					 "Ljava/util/function/Consumer;)V", at = @At("TAIL"))
	private static void applyBoostDamage2(ServerWorld world, Entity target, DamageSource damageSource,
										  ItemStack weapon, java.util.function.Consumer<Item> breakCallback,
										  CallbackInfo ci)
	{
		HelperEnchantmentHelper.onTargetDamaged(world, target, damageSource, weapon);
	}

	@Inject(method = "getItemDamage", at = @At("RETURN"), cancellable = true)
	private static void modifyGetItemDamage(ServerWorld world, ItemStack stack, int baseItemDamage,
											CallbackInfoReturnable<Integer> cir)
	{
		cir.setReturnValue(HelperEnchantmentHelper.getItemDamage(world, stack, cir.getReturnValue()));
	}

	@Inject(method = "getEquipmentDropChance", at = @At("RETURN"), cancellable = true)
	private static void modifyGetEquipmentDropChance(ServerWorld world, LivingEntity attacker,
													 DamageSource damageSource, float baseEquipmentDropChance,
													 CallbackInfoReturnable<Float> cir)
	{
		var ret = HelperEnchantmentHelper.getEquipmentDropChance(world, attacker, damageSource, cir.getReturnValue());
		cir.setReturnValue(ret);
	}

	@Inject(method = "modifyKnockback", at = @At("RETURN"), cancellable = true)
	private static void modifyModifyKnockback(ServerWorld world, ItemStack stack, Entity target,
											  DamageSource damageSource, float baseKnockback,
											  CallbackInfoReturnable<Float> cir)
	{
		cir.setReturnValue(cir.getReturnValue() +
						   HelperEnchantmentHelper.modifyKnockback(world, stack, target, damageSource, baseKnockback) -
						   baseKnockback);
	}
}
