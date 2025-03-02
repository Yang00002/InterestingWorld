package org.yang.iw.mixin.mixin.loot_table;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Debug(export = true)
@Mixin(ApplyBonusLootFunction.class)
public class MixinApplyBonusLootFunction
{
	@Shadow
	@Final
	private RegistryEntry<Enchantment> enchantment;

	@ModifyArg(method = "process", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/function" +
																	   "/ApplyBonusLootFunction$Formula;getValue" +
																	   "(Lnet/minecraft/util/math/random/Random;II)I")
			, index = 2)
	private int addLooting(int initialCount, @Local(ordinal = 1) ItemStack stack)
	{
		return initialCount +
			   stack.interestingWorld$getBoosts().pretendedLevel(enchantment, stack, EquipmentSlot.MAINHAND);
	}
}
