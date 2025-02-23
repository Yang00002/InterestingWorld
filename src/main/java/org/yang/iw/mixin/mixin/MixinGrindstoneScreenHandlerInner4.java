package org.yang.iw.mixin.mixin;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/screen/GrindstoneScreenHandler$4")
public class MixinGrindstoneScreenHandlerInner4
{
	@Inject(method = "getExperience(Lnet/minecraft/item/ItemStack;)I", at = @At(value = "RETURN"), cancellable = true)
	private void repairUpSlot(ItemStack stack, CallbackInfoReturnable<Integer> cir)
	{
		cir.setReturnValue(cir.getReturnValue() + stack.interestingWorld$getBoosts().clearCost());
	}
}
