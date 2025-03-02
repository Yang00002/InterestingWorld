package org.yang.iw.mixin.mixin.grindstone;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/screen/GrindstoneScreenHandler$3")
public class MixinGrindstoneScreenHandlerInner3
{
	@Inject(method = "canInsert", at = @At(value = "HEAD"), cancellable = true)
	private void repairUpSlot(ItemStack stack, CallbackInfoReturnable<Boolean> cir)
	{
		if (!stack.interestingWorld$getBoosts().isEmpty()) cir.setReturnValue(true);
	}
}
