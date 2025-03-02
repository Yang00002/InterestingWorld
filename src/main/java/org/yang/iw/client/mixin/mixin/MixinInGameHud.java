package org.yang.iw.client.mixin.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.iw.client.mixin.helper.HelperInGameHud;

@Debug(export = true)
@Mixin(InGameHud.class)
public abstract class MixinInGameHud
{

	@Shadow
	public abstract TextRenderer getTextRenderer();

	@Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler" +
																			 "/Profilers;" +
																			 "get()Lnet/minecraft/util/profiler" +
																			 "/Profiler;", ordinal = 2))
	public void renderEnergy(DrawContext context, CallbackInfo ci, @Local PlayerEntity playerEntity, @Local(ordinal =
			3) int m, @Local(ordinal = 8) int r)

	{
		HelperInGameHud.renderCustom(context, getTextRenderer(), playerEntity, m, r);
	}

	@ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud" +
																				"/InGameHud;renderAirBubbles" +
																				"(Lnet/minecraft/client/gui" +
																				"/DrawContext;Lnet/minecraft/entity" +
																				"/player/PlayerEntity;III)V"), index
			= 3)
	private int changeAirPlace(int x)
	{
		return x - 10;
	}
}
