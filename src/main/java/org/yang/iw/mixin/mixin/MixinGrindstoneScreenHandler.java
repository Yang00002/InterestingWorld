package org.yang.iw.mixin.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.yang.iw.component.BoostableComponent;
import org.yang.iw.component.IWComponents;
import org.yang.iw.item.tool.EnergyToolItem;

@Mixin(GrindstoneScreenHandler.class)
public abstract class MixinGrindstoneScreenHandler
{
	@Shadow
	protected abstract ItemStack grind(ItemStack item);

	@Inject(method = "getOutputStack", at = @At(value = "HEAD"), cancellable = true)
	private void repairUpSlot(ItemStack firstInput, ItemStack secondInput, CallbackInfoReturnable<ItemStack> cir)
	{
		boolean fe = firstInput.isEmpty();
		boolean se = secondInput.isEmpty();
		if (fe && se) cir.setReturnValue(ItemStack.EMPTY);
		else if (fe || se)
		{
			var itemStack = fe ? secondInput : firstInput;
			var boostComponent = itemStack.interestingWorld$getBoosts();
			if (boostComponent.isEmpty()) return;
			itemStack = itemStack.copy();
			itemStack.set(IWComponents.BOOST, boostComponent.clear());
			var boostableComponent = itemStack.getOrDefault(IWComponents.BOOSTABLE, BoostableComponent.DEFAULT);
			if (!boostableComponent.isEmpty()) itemStack.set(IWComponents.BOOSTABLE, boostableComponent.clear());
			cir.setReturnValue(!EnchantmentHelper.hasEnchantments(itemStack) ? itemStack : grind(itemStack));
		}
		else if (firstInput.getItem() instanceof EnergyToolItem) cir.setReturnValue(ItemStack.EMPTY);
	}
}
