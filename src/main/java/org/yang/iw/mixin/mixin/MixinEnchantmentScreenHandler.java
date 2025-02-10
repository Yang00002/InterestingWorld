package org.yang.iw.mixin.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.yang.iw.item.heart.AbstractHeart;

@Debug(export = true)
@Mixin(EnchantmentScreenHandler.class)
public class MixinEnchantmentScreenHandler
{

	@Inject(method = "onButtonClick", at = @At(value = "INVOKE", target =
			"Lnet/minecraft/screen/ScreenHandlerContext;" + "run(Ljava/util/function/BiConsumer;)V"))
	private void addPreEnchantFlag(PlayerEntity player, int id, CallbackInfoReturnable<Boolean> cir, @Local(ordinal =
			0) ItemStack itemStack)
	{
		if (itemStack.getItem() instanceof AbstractHeart heart)
		{
			heart.setPreEnchantFlag(itemStack);
		}
	}
}
