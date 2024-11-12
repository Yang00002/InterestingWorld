package org.yang.interestingworld.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.IWComponents;

import static org.yang.interestingworld.IWUtil.TextStyle.getBoldTextStyle;


@Debug(export = true)
@Mixin(AnvilScreenHandler.class)
public abstract class ForName implements ComponentHolder, FabricItemStack
{
    @Accessor("newItemName")
    abstract String getnewItemName();

    @Inject(method = "updateResult()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;literal" + "(Ljava/lang/String;)Lnet/minecraft/text/MutableText;", shift = At.Shift.BY, by = 2))
    private void injected(CallbackInfo ci, @Local(ordinal = 1) ItemStack itemStack2)
    {
        if (itemStack2.getItem() instanceof EnergyToolItem)
        {
            itemStack2.set(DataComponentTypes.CUSTOM_NAME, Text.literal(getnewItemName())
                    .setStyle(getBoldTextStyle(itemStack2.getOrDefault(IWComponents.ITEM_COLOR, -1))));
        }
    }
}
