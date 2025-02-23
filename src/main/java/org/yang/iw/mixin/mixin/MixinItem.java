package org.yang.iw.mixin.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.yang.iw.mixin.mixin_interface.InterfaceItem;

@Mixin(Item.class)
public class MixinItem implements InterfaceItem
{
	@Inject(method = "hasGlint", at = @At("RETURN"), cancellable = true)
	private void glintForBoosts(ItemStack stack, CallbackInfoReturnable<Boolean> cir)
	{
		if (!cir.getReturnValue()) cir.setReturnValue(!stack.interestingWorld$getBoosts().isEmpty());
	}
}
