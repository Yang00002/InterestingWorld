package org.yang.interestingworld.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.item.tool.EnergyToolItem;

import static org.yang.interestingworld.IWUtil.TextStyle.getBoldTextStyle;

@Mixin(AnvilScreenHandler.class)
public abstract class MixinAnvilScreenHandler implements ComponentHolder, FabricItemStack
{
	@Accessor("newItemName")
	abstract String getnewItemName();

	@Inject(method = "updateResult()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;literal" +
																			"(Ljava/lang/String;)" +
																			"Lnet/minecraft/text/MutableText;", shift
			= At.Shift.BY, by = 2))
	private void injected(CallbackInfo ci, @Local(ordinal = 1) ItemStack itemStack2)
	{
		if (itemStack2.getItem() instanceof EnergyToolItem)
		{
			itemStack2.set(DataComponentTypes.CUSTOM_NAME, Text.literal(getnewItemName())
					.setStyle(getBoldTextStyle(itemStack2.getOrDefault(IWComponents.ITEM_COLOR, -1))));
		}
	}

	@Inject(method = "setNewItemName(Ljava/lang/String;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/text" +
																								"/Text;literal" +
																								"(Ljava/lang/String;)" +
																								"Lnet/minecraft/text" +
																								"/MutableText;",
			shift = At.Shift.BY, by = 2))
	private void injected(String newItemName, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) String string,
						  @Local ItemStack itemStack)
	{
		if (itemStack.getItem() instanceof EnergyToolItem)
		{
			itemStack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(string)
					.setStyle(IWUtil.TextStyle.getBoldTextStyle(itemStack.getOrDefault(IWComponents.ITEM_COLOR, -1))));
		}
	}
}
