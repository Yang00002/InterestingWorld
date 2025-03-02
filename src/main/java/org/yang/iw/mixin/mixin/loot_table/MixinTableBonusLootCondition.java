package org.yang.iw.mixin.mixin.loot_table;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.TableBonusLootCondition;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TableBonusLootCondition.class)
public class MixinTableBonusLootCondition
{
	@Shadow
	@Final
	private RegistryEntry<Enchantment> enchantment;

	@ModifyArg(method = "test(Lnet/minecraft/loot/context/LootContext;)Z", at = @At(value = "INVOKE", target =
			"Ljava" +
																											   "/lang" +
																											   "/Math;" +
																											   "min" +
																											   "(II)I"), index = 0)
	private int addLooting(int initialCount, @Local(ordinal = 0) ItemStack stack)
	{
		if (stack == null) return initialCount;
		return initialCount +
			   stack.interestingWorld$getBoosts().pretendedLevel(enchantment, stack, EquipmentSlot.MAINHAND);
	}
}
