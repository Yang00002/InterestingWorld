package org.yang.iw.mixin.mixin.loot_table;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.yang.iw.component.BoostComponent;

@Debug(export = true)
@Mixin(EnchantedCountIncreaseLootFunction.class)
public class MixinEnchantedCountIncreaseLootFunction
{

	@Redirect(method = "process", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;" +
																	  "getEquipmentLevel" +
																	  "(Lnet/minecraft/registry/entry" +
																	  "/RegistryEntry;" +
																	  "Lnet/minecraft/entity/LivingEntity;)I"))
	private int addLooting(RegistryEntry<Enchantment> enchantment, LivingEntity entity)
	{
		return EnchantmentHelper.getEquipmentLevel(enchantment, entity) +
			   BoostComponent.pretendedLevel(enchantment, entity);
	}
}
